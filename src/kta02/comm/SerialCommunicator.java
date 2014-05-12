package kta02.comm;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author Huib, Roelof
 */
public class SerialCommunicator
{

    SerialPort serialPort;
    /**
     * All available ports
     */
    private static final String PORT_NAMES[] =
    {
        "COM1",
        "COM2",
        "COM3",
        "COM4",
        "COM5",
        "COM6"
    };

    /**
     * Indicates the number of required Devices (Arduino's)
     */
    private static final int REQUIRED_DEVICE_COUNT = 1;

    public static ArrayList<ArduinoConnection> initialize() throws InsufficientDevicesException
    {

        ArrayList<CommPortIdentifier> connectedDevices;

        try
        {
            //Obtain a list of connected devices
            connectedDevices = getConnectedPorts();
        } catch (InsufficientDevicesException e)
        {
            //Throw the exception upwards
            throw e;
        }

        ArrayList<ArduinoConnection> arduinoConnections;
        arduinoConnections = new ArrayList<>();

        for (CommPortIdentifier comPort : connectedDevices)
        {
            ArduinoConnection temporaryConnection;
            try
            {
                temporaryConnection = new ArduinoConnection(comPort);
                arduinoConnections.add(temporaryConnection);
            } catch (IOException e)
            {
                System.err.println(e.getMessage());
            }
        }

        return arduinoConnections;
    }

    public static ArrayList<CommPortIdentifier> getConnectedPorts() throws InsufficientDevicesException
    {

        ArrayList<CommPortIdentifier> availablePorts = new ArrayList<>();
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements())
        {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES)
            {
                if (currPortId.getName().equals(portName))
                {
                    availablePorts.add(currPortId);
                }
            }
        }

        if (availablePorts.isEmpty())
        {
            throw new InsufficientDevicesException("No devices are connected to the COM ports.", InsufficientDevicesException.E_NO_DEVICES);
        }

        if (availablePorts.size() < REQUIRED_DEVICE_COUNT)
        {
            throw new InsufficientDevicesException("There is an insufficient number of devices connected to this computer's COM ports.", InsufficientDevicesException.E_DEVICE_COUNT_TOO_LOW);
        }

        if (availablePorts.size() > REQUIRED_DEVICE_COUNT)
        {
            throw new InsufficientDevicesException("There is an insufficient number of devices connected to this computer's COM ports.", InsufficientDevicesException.E_DEVICE_COUNT_TOO_HIGH);
        }

        return availablePorts;
    }
}
