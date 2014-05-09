package kta02.warehouse;

import java.util.ArrayList;
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
                System.out.println("No Arduino's connected!");

            } else if (errorCode == InsufficientDevicesException.E_DEVICE_COUNT_TOO_HIGH)
            {
                System.out.println("Please disconnect some Arduino's, there are too many Arduino's connected!");

            } else if (errorCode == InsufficientDevicesException.E_DEVICE_COUNT_TOO_LOW)
            {
                System.out.println("Please connect some Arduino's, there are insufficient Arduino's connected!");
            }
            return;
        }

        System.out.println("Connected to " + arduinos.size() + " Arduino('s).");

        new TestWindow(arduinos);

    }

}
