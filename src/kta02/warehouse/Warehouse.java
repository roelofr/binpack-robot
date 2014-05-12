package kta02.warehouse;

import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import kta02.comm.ArduinoConnection;
import kta02.comm.InsufficientDevicesException;
import kta02.comm.SerialCommunicator;
import kta02.dev.TestWindow;

/**
 *
 * @author Huib
 */
public class Warehouse
{

    public static void main(String[] args)
    {
        setLookAndFeel();

        TestWindow testWin = new TestWindow();

        ArrayList<ArduinoConnection> arduinos;

        System.out.println("KTA02");
        System.out.println("Bin-Packing Problem Simulator");

        // Connect to the Arduino's
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
            return;
        }

        System.out.println("Connected to " + arduinos.size() + " Arduino('s).");

        testWin.setArduinoList(arduinos);
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
