package kta02.warehouse;

import database.DatabaseConnection;
import java.awt.Point;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import kta02.comm.ArduinoConnection;
import kta02.comm.InsufficientDevicesException;
import kta02.comm.SerialCommunicator;
import kta02.gui.EmergencyPanel;
import kta02.gui.MainGUI;

/**
 *
 * @author Huib
 */
public class Warehouse implements Runnable
{

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

    public synchronized void disconnectArduinos()
    {
        for (ArduinoConnection conn : arduinos)
        {
            conn.close();
        }
        arduinos.clear();

    }

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
        recognizerThread.start();

    }

    public synchronized void reconnectToArduinos()
    {
        disconnectArduinos();
        connectToArduinos();
    }

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

    @Override
    public void run()
    {
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

}
