package kta02.warehouse;

import database.DatabaseConnection;
import database.DatabaseProcessor;
import java.awt.Point;
import gui.GUI;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import kta02.comm.ArduinoConnection;
import kta02.comm.InsufficientDevicesException;
import kta02.comm.SerialCommunicator;
import kta02.gui.EmergencyPanel;
import kta02.gui.MainGUI;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.Klant;
import kta02.xml.XMLWriter;
import xml.XMLReader;

/**
 *
 * @author Huib
 */
public class Warehouse implements Runnable
{
	
    static XMLReader reader;
    static Bestelling bestelling;

    static DatabaseProcessor dbProcessor;
    
    public static final boolean DEBUG = true;

    private static Warehouse warehouse;

    private final MainGUI UI;

    private final RobotMover mover;

    private ArrayList<ArduinoConnection> arduinos;

    private final Thread recognizerThread;

    private EmergencyPanel emPanel;

    public static void main(String[] args)
    {
        warehouse = new Warehouse();
    }

    /**
     * Forces all systems to stop, on all arduinos
     */
    public static void emergency()
    {
        warehouse.emergencyStop();
    }

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

    private Warehouse()
    {
        setLookAndFeel();

        UI = new MainGUI(this);
        mover = new RobotMover();

        arduinos = new ArrayList<>();

        System.out.println("KTA02");
        System.out.println("Bin-Packing Problem Simulator");
        DatabaseConnection dbCon = new DatabaseConnection();
        try
        {
            dbCon.linkToQueryCollector();
        } catch (SQLException ex)
        {
            System.err.println(ex.getMessage());
        }

        recognizerThread = new Thread(this);

        // Connect to the Arduino's
        connectToArduinos();
    }

    public void addPointToFetchQueue(Point target)
    {
        mover.addToFetchQueue(target);
    }

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
        if (!recognizerThread.isAlive())
        {
            recognizerThread.start();
    }

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
            if (arduinos == null)
            {

                System.err.println("No Arduinos yet, waiting 5 seconds");
                sleep(5000);
                continue;
    }

            if (arduinos.size() < 2)
            {
                System.err.println("Not enough arduinos for recognizer, waiting 5 seconds.");
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

    public static void setXMLFile(File file)
    {
        reader = new XMLReader(file.getPath());

        bestelling = reader.readFromXml();

        dbProcessor = new DatabaseProcessor(bestelling);

        try
        {
            dbProcessor.processArticles();
            System.out.println("__________________________________________________________________");
            for (Artikel artikel : bestelling.getArtikelen())
            {
                System.out.println(artikel);
            }
            System.out.println("__________________________________________________________________");
        }
        catch (SQLException ex)
        {
            System.err.println(ex.getMessage());
        }

        //als er meer dan 1 pakbon is, zorg dan dat de order met meerdere pakbonnen gemaakt zijn!
        //for(int i = 0; i < aantalpakbonnen; i++){
        String bestandsNaam = "";
        String volledigeKlantNaam = bestelling.getKlant().getVoornaam() + " " + bestelling.getKlant().getAchternaam();
        bestandsNaam += bestelling.getBestelNummer() + ". " + volledigeKlantNaam /* + " - " + i */; //Zorg voor andere naam voor pakbon!
        bestandsNaam += ".xml";

        new XMLWriter(bestelling).writeXML(bestandsNaam);
        //}

        //TEST FOR ALGORITHM
        //REMOVE WHEN DONE
        //ArrayList<Integer> orderSorting = Algoritm.tourImprovement(bestelling.getArtikelen(), 0, 0);
    }

    public static DatabaseProcessor getDbProcessor()
    {
        return dbProcessor;
    }

    public static Bestelling getBestelling()
    {
        return bestelling;
    }

    public static Klant getKlant()
    {
        return bestelling.getKlant();
    }

}
