/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.comm;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.TooManyListenersException;

/**
 *
 * @author Roelof
 */
public class ArduinoBackend implements SerialPortEventListener
{

    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port. (baud)
     */
    private static final int DATA_RATE = 9600;

    /**
     * Serial Port connection
     */
    SerialPort serialPort;

    /**
     * The input stream
     */
    private InputStream input;
    /**
     * The output stream to the port
     */
    private OutputStream output;

    /**
     * The timestamp of the most recent data
     */
    private long lastRead;
    /**
     * The most recent data
     */
    private String lastData;

    private String comPort;

    private int motorSpeeds[];

    /**
     * Indicates if the port is closed, all commands will fail if this happens.
     */
    private Boolean arduinoAvailable;

    public ArduinoBackend(CommPortIdentifier portNumber) throws IOException
    {

        motorSpeeds = new int[2];
        motorSpeeds[0] = 0;
        motorSpeeds[1] = 0;

        lastRead = 0;
        lastData = "";
        arduinoAvailable = false;

        comPort = portNumber.getName();

        if (portNumber.isCurrentlyOwned())
        {
            throw new IOException("Comport " + comPort + " is currently owned by " + portNumber.getCurrentOwner());
        }
        try
        {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portNumber.open("KTA02 Storage Manager",
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

        } catch (PortInUseException | UnsupportedCommOperationException | IOException | TooManyListenersException e)
        {
            if (e.getClass().getName() == "PortInUseException")
            {
                System.err.println("Port in use on " + portNumber.getName() + "! Details: " + e.getLocalizedMessage());
            } else if (e.getClass().getName() == "TooManyListenersException")
            {
                System.err.println("Port " + portNumber.getName() + " has too many device listeners! Details: " + e.getLocalizedMessage());
            } else if (e.getClass().getName() == "IOException")
            {
                System.err.println("Port " + portNumber.getName() + " reported an Input/Output error! Details: " + e.getLocalizedMessage());
            } else if (e.getClass().getName() == "UnsupportedCommOperationException")
            {
                System.err.println("Attempt to use unsupported operation on port " + portNumber.getName() + "! Details: " + e.getLocalizedMessage());
            } else
            {
                System.err.println(e.toString());
            }
        }
    }

    /**
     * Returns true as soon as a signal has been received over the serial
     * communication from the Arduino
     *
     * @return
     */
    protected Boolean isOnline()
    {
        return arduinoAvailable;
    }

    /**
     * Returns the COM port this arduino is on
     *
     * @return
     */
    protected String getComPort()
    {
        return comPort;
    }

    /**
     * Returns the time the most recent data was received, with milliseconds.
     *
     * @return The timestamp at which the latest data was received
     */
    public long getLastRead()
    {
        return lastRead;
    }

    /**
     * Returns the most recent set of data
     *
     * @return The latest data (one line)
     */
    public String getLastData()
    {
        return lastData;
    }

    /**
     * Returns the speed of the first motor
     *
     * @return Speed of motor, range between -3 and 3 (-3 is backwards, 3 is
     * forwards, 0 is stop)
     */
    public int getMotor1Velocity()
    {
        return motorSpeeds[0];
    }

    /**
     * Returns the speed of the second motor
     *
     * @return Speed of motor, range between -3 and 3 (-3 is backwards, 3 is
     * forwards, 0 is stop)
     */
    public int getMotor2Velocity()
    {
        return motorSpeeds[0];
    }

    /**
     * Closes the serial connection.
     */
    public synchronized void close()
    {
        if (serialPort != null)
        {
            serialPort.removeEventListener();
            serialPort.close();
            arduinoAvailable = true;
        }
    }

    /**
     * Waits for a while, without triggering errors (errors aren't logged)
     *
     * @param time time to wait in milliseconds
     */
    protected synchronized void silentSleep(int time)
    {
        try
        {
            wait((long) time);
        } catch (InterruptedException e)
        {

        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     *
     * @param oEvent The event data
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            String data = "";
            byte chunk[];
            int available;
            try
            {
                while ((available = input.available()) > 0)
                {
                    chunk = new byte[available];
                    input.read(chunk, 0, available);

                    data += new String(chunk);
                    silentSleep(10);
                }
            } catch (IOException e)
            {
                System.err.println("Error when reading data: " + e.getMessage());
            }

            if (data.isEmpty())
            {
                return;
            }

            // Explode the data, there may be some keep-alives there
            String lines[] = data.split("([\r\n]+)");

            for (String line : lines)
            {
                if (line.charAt(0) == '-')
                {
                    if (line.length() >= 3)
                    {
                        motorSpeeds[0] = Integer.parseInt(line.substring(1, 2)) - 4;
                        motorSpeeds[1] = Integer.parseInt(line.substring(2, 3)) - 4;
                    }
                    arduinoAvailable = true;
                } else
                {
                    lastData = line;
                    lastRead = new Date().getTime();
                    System.out.println("[" + getComPort() + "] " + lastData + ".");
                }
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    /**
     * Sends data to the Arduino
     *
     * @param data The data to send
     * @return TRUE on success, FALSE on error (error is printed to stderr)
     */
    protected Boolean write(String data)
    {
        if (!arduinoAvailable || data.isEmpty())
        {
            return false;
        }
        System.out.println("Sending \"" + data + "\"...");

        try
        {
            output.write(data.getBytes());
        } catch (IOException e)
        {
            System.err.println("Error when sending data! " + e.getMessage());
            return false;
        }
        return true;

    }
}
