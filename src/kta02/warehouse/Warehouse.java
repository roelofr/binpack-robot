package kta02.warehouse;

import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import kta02.comm.ArduinoConnection;
import kta02.comm.InsufficientDevicesException;
import kta02.comm.SerialCommunicator;
import kta02.gui.MainGUI;

/**
 *
 * @author Huib
 */
public class Warehouse
{

    private static MainGUI UI;

    private static ArrayList<ArduinoConnection> arduinos;

    public static void main(String[] args)
    {
        setLookAndFeel();

        UI = new MainGUI();

        arduinos = new ArrayList<>();

        System.out.println("KTA02");
        System.out.println("Bin-Packing Problem Simulator");

        // Connect to the Arduino's
        connectToArduinos();
    }

    public synchronized static void disconnectArduinos()
    {
        System.out.println("Disconnecting from arduino's");
        for (ArduinoConnection conn : arduinos)
        {
            conn.close();
        }
        arduinos.clear();

    }

    public synchronized static void connectToArduinos()
    {
        System.out.println("Connecting to arduino's");
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

    public synchronized static void reconnectToArduinos()
    {
        Warehouse.disconnectArduinos();
        Warehouse.connectToArduinos();
    }

    private static void setLookAndFeel()
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

}
