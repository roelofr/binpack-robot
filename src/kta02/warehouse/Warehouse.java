package kta02.warehouse;

import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import kta02.binpackage.BestFit;
import kta02.comm.ArduinoConnection;
import kta02.comm.DatabaseConnection;
import kta02.comm.DatabaseProcessor;
import kta02.comm.DatabaseQueryCollector;
import kta02.comm.InsufficientDevicesException;
import kta02.comm.SerialCommunicator;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.Klant;
import kta02.easteregg.EasterEggKeyListener;
import kta02.gui.EmergencyPanel;
import kta02.gui.LoadingDialog;
import kta02.gui.MainGUI;
import kta02.tsp.Algoritm;
import kta02.xml.XMLReader;
import kta02.xml.XMLWriter;

/**
 *
 * @author Huib
 */
public class Warehouse implements Runnable
{

    private final long DB_KEEP_ALIVE = 30 * 1000;

    private XMLReader reader;

    private Bestelling bestelling;

    private DatabaseProcessor dbProcessor;

    public static final boolean DEBUG = true;

    private static Warehouse warehouse;

    private final MainGUI UI;

    private final RobotMover mover;

    private ArrayList<ArduinoConnection> arduinos;

    private final Thread recognizerThread;

    private EmergencyPanel emPanel;

    private EasterEggKeyListener keyListener;

    private long lastKeepAlive = 0;

    public static void main(String[] args)
    {
        warehouse = new Warehouse();
    }

    private Warehouse()
    {
        setLookAndFeel();

        UI = new MainGUI(this);
        mover = new RobotMover(this);

        arduinos = new ArrayList<>();

        System.out.println("KTA02");
        System.out.println("Bin-Packing Problem Simulator");

        connectToDatabase();
        recognizerThread = new Thread(this);
    }

    private void linkKeyInput()
    {
        if (keyListener != null)
        {
            return;
        }

        keyListener = new EasterEggKeyListener(this);
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(keyListener);
    }

    private void connectToDatabase()
    {
        new Thread(new Runnable()
        {

            private void sleep(long delay)
            {
                try
                {
                    Thread.sleep(delay);
                } catch (Exception e)
                {

                }
            }

            @Override
            public void run()
            {
                LoadingDialog dialog = new LoadingDialog("Verbinden met database...");
                sleep(100);
                DatabaseConnection dbCon = new DatabaseConnection();
                try
                {
                    dbCon.linkToQueryCollector();
                } catch (SQLException ex)
                {
                    UI.dispose();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(null, "Er kon geen verbinding met de database gemaakt worden!\nDe applicatie zal nu afsluiten.", "Databaseverbinding fout!", JOptionPane.WARNING_MESSAGE);
                    System.exit(1);
                    return;
                }
                dialog.dispose();
                UI.setVisible(true);
                if (!recognizerThread.isAlive())
                {
                    recognizerThread.start();
                }
                linkKeyInput();
                connectToArduinos();
            }
        }).start();

    }

    /**
     * Forces all systems to stop, on all arduinos
     */
    public static void emergency()
    {
        warehouse.emergencyStop();
    }

    /**
     * Immediately stops all Arduino's
     */
    public void emergencyStop()
    {
        int i = 0;
        while (i < 4)
        {
            for (ArduinoConnection conn : arduinos)
            {
                conn.setEmergencyFlag(true);
            }
            sleep(30);
            i++;
        }

        if (emPanel != null && emPanel.isVisible())
        {
            return;
        }

        emPanel = new EmergencyPanel(this);
    }

    /**
     * Restores the emergency state of the Arduino's so they can be controlled
     * again
     */
    public void restoreSystems()
    {
        int i = 0;
        while (i < 4)
        {
            for (ArduinoConnection conn : arduinos)
            {
                conn.setEmergencyFlag(false);
            }
            sleep(30);
            i++;
        }

        if (emPanel != null && emPanel.isVisible())
        {
            emPanel.dispose();
        }
    }

    /**
     * Adds a Point to the fetch queue of the robot mover
     *
     * @param target
     */
    public void addPointToFetchQueue(Point target)
    {
        mover.addToFetchQueue(target);
    }

    /**
     * Starts the pickup process, there is usually no need to call this.
     */
    public void startPickup()
    {
        mover.startPickup();
    }

    /**
     * Disconnect all Arduino's
     */
    public synchronized void disconnectArduinos()
    {
        for (ArduinoConnection conn : arduinos)
        {
            conn.close();
        }
        arduinos.clear();

    }

    /**
     * Connects to Arduino's
     */
    public synchronized void connectToArduinos()
    {
        try
        {
            arduinos = SerialCommunicator.initialize();
        } catch (InsufficientDevicesException e)
        {
            int errorCode = e.getCode();
            if (errorCode == InsufficientDevicesException.E_NO_DEVICES)
            {
                System.err.println("No Arduino's connected!");

            } else if (errorCode == InsufficientDevicesException.E_DEVICE_COUNT_TOO_HIGH)
            {
                System.err.println("Please disconnect some Arduino's, there are too many Arduino's connected!");

            } else if (errorCode == InsufficientDevicesException.E_DEVICE_COUNT_TOO_LOW)
            {
                System.err.println("Please connect some Arduino's, there are insufficient Arduino's connected!");
            }
        }

        UI.setArduinos(arduinos);

    }

    /**
     * Makes the program reconnect
     */
    public synchronized void reconnectToArduinos()
    {
        disconnectArduinos();
        connectToArduinos();
    }

    /**
     * Sets the look and feel to Windows-ish
     */
    private void setLookAndFeel()
    {

        try
        {
            // Make it look better
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            // Do nothing
        }
    }

    private void sleep(long delay)
    {
        try
        {
            Thread.sleep(delay);
        } catch (InterruptedException ex)
        {
            // No, not gonna do anything
        }
    }

    /**
     * Thread runner
     */
    @Override
    public void run()
    {
        // Continue untill interrupted
        while (!Thread.currentThread().isInterrupted())
        {
            if (lastKeepAlive < new Date().getTime())
            {
                lastKeepAlive = new Date().getTime() + DB_KEEP_ALIVE;
                DatabaseQueryCollector.getInstance().sendKeepAlive();
            }

            if (arduinos == null)
            {
                sleep(5000);
                continue;
            }

            if (arduinos.size() < 2)
            {
                sleep(5000);
                continue;
            }

            for (ArduinoConnection conn : arduinos)
            {
                if (conn.isValidArduino())
                {
                    if (conn.getType() == ArduinoConnection.TYPE_BIN)
                    {
                        mover.setBinRobot(conn);
                    } else if (conn.getType() == ArduinoConnection.TYPE_MOTOR)
                    {
                        mover.setMoveRobot(conn);
                    }
                }
            }
            sleep(5 * 1000);
        }
    }

    /**
     * Sets the XML file, called from <code>kta02.gui.XMLPicker</code>.
     *
     * @param file
     */
    public void setXMLFile(File file)
    {
        reader = new XMLReader(file.getPath());

        Bestelling temporaryBestelling;
        temporaryBestelling = reader.readFromXml();

        dbProcessor = new DatabaseProcessor(temporaryBestelling);

        try
        {
            dbProcessor.processArticles();
            if (DEBUG)
            {
                System.out.println("__________________________________________________________________");
                for (Artikel artikel : temporaryBestelling.getArtikelen())
                {
                    System.out.println(artikel);
                }
                System.out.println("__________________________________________________________________");
            }
        } catch (SQLException ex)
        {
            System.err.println(ex.getMessage());
        }
        //als er meer dan 1 pakbon is, zorg dan dat de order met meerdere pakbonnen gemaakt zijn!
        if (temporaryBestelling.getArtikelen().size() < 5)
        {
            ArrayList<Integer> idOrder = Algoritm.tourImprovement(temporaryBestelling.getArtikelen(), 0, 0);
            for (int i = 0; i < BestFit.BestFit(temporaryBestelling, idOrder).size(); i++)
            {
                String bestandsNaam = "";
                String volledigeKlantNaam = temporaryBestelling.getKlant().getVoornaam() + " " + temporaryBestelling.getKlant().getAchternaam();
                bestandsNaam += temporaryBestelling.getBestelNummer() + ". " + volledigeKlantNaam + " - " + i;
                bestandsNaam += ".xml";

                new XMLWriter(temporaryBestelling, i).writeXML(bestandsNaam);
            }

            ArrayList<Point> positions = new ArrayList<>();
            for (int q = 0; q < idOrder.size(); q++)
            {
                positions.add(new Point(temporaryBestelling.getArtikelen().get(idOrder.get(q)).getLocatie()));

            }
            
            ArrayList<Integer> binOrder = new ArrayList<>();
            for (int i = 0; i < idOrder.size(); i++) {
                for(int x = 0; x < BestFit.BestFit(temporaryBestelling, idOrder).size(); x++){
                    for(int y = 0; y < BestFit.BestFit(temporaryBestelling, idOrder).get(x).size(); y++){
                        if(BestFit.BestFit(temporaryBestelling, idOrder).get(x).get(y).equals(idOrder.get(i))){
                            binOrder.add(x);
                        }
                    }
                }
            }
            Collections.reverse(binOrder);
            if (DEBUG)
            {
                for (int q = 0; q < binOrder.size(); q++)
                {
                    System.out.println("******");
                    System.out.println(binOrder.get(q));
                }
            }


            if (DEBUG)
            {
                for (int q = 0; q < positions.size(); q++)
                {
                    System.out.println("******");
                    System.out.println(positions.get(q).x + ", " + positions.get(q).y);
                }
            }

            mover.retrieveItems(positions, binOrder);
        } else
        {
            JOptionPane.showMessageDialog(emPanel, "Maximaal aantal producten is 5.", "Product amount Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Haal een aantal artikelen uit de XML file weg.");
        }

        bestelling = temporaryBestelling;

        UI.toggleInterface(true);
    }

    public DatabaseProcessor getDbProcessor()
    {
        return dbProcessor;
    }

    public Bestelling getBestelling()
    {
        return bestelling;
    }

    public Klant getKlant()
    {
        return bestelling.getKlant();
    }

    public RobotMover getRobotMover()
    {
        return mover;
    }

    public MainGUI getMainGUI()
    {
        return UI;
    }

}
