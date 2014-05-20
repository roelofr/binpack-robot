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

    private static final boolean DEBUG = kta02.warehouse.Warehouse.DEBUG;

    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port. (baud)
     */
    private static final int DATA_RATE = 9600;

    private static final String emergencyOn = "q";
    private static final String emergencyOff = "r";

    /**
     * Serial Port connection
     */
    SerialPort serialPort;

    CommPortIdentifier portIdentifier;
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

    private int sensorData[];

    private long lastSeen;

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

        lastSeen = 0;

        comPort = portNumber.getName();
        portIdentifier = portNumber;

    }

    protected void connectToArduino() throws IOException, PortInUseException, UnsupportedCommOperationException, TooManyListenersException
    {
        // open serial port, and use class name for the appName.
        serialPort = (SerialPort) portIdentifier.open("KTA02 Storage Manager",
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

    }

    /**
     * Returns true as soon as a signal has been received over the serial
     * communication from the Arduino
     *
     * @return
     */
    public Boolean isOnline()
    {
        return arduinoAvailable;
    }

    public long getLastSeen()
    {
        return lastSeen;
    }

    /**
     * Returns the COM port this arduino is on
     *
     * @return
     */
    public String getComPort()
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

    private int getMotorData(int motorNr)
    {
        if (motorNr < 0 || motorNr > motorSpeeds.length)
        {
            return 0;
        }
        return motorSpeeds[motorNr];
    }

    public int getSensorData(int sensorNr)
    {
        if (sensorNr < 0 || sensorNr > sensorData.length)
        {
            return 0;
        }
        return sensorData[sensorNr];
    }

    /**
     * Returns the speed of the first motor
     *
     * @return Speed of motor, range between -3 and 3 (-3 is backwards, 3 is
     * forwards, 0 is stop)
     */
    public int getMotor1Velocity()
    {
        return getMotorData(0);
    }

    /**
     * Returns the speed of the second motor
     *
     * @return Speed of motor, range between -3 and 3 (-3 is backwards, 3 is
     * forwards, 0 is stop)
     */
    public int getMotor2Velocity()
    {
        return getMotorData(1);
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
            arduinoAvailable = false;
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
                lastSeen = new Date().getTime();
                if (line.charAt(0) == '-')
                {
                    arduinoAvailable = true;
                    line.replaceAll("([^0-9]+)", "");
                    if (line.length() >= 3)
                    {
                        motorSpeeds[0] = Integer.parseInt(line.substring(1, 2)) - 4;
                        motorSpeeds[1] = Integer.parseInt(line.substring(2, 3)) - 4;
                        line = line.substring(3);
                    }
                    if (sensorData == null)
                    {
                        sensorData = new int[line.length()];
                    }
                    if (line.length() == 0)
                    {
                        continue;
                    }
                    int i = 0;
                    while (line.length() > 0)
                    {
                        if (sensorData.length < i)
                        {
                            break;
                        }
                        sensorData[i] = Integer.parseInt(line.substring(i, i + 1));
                        i++;
                    }
                } else
                {
                    lastData = line;
                    lastRead = new Date().getTime();
                    if (DEBUG)
                    {
                        System.out.println("[" + getComPort() + "] " + lastData + ".");
                    }
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
        if (DEBUG)
        {
            System.out.println("Sending \"" + data + "\"...");
        }

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

    public void setEmergencyFlag(boolean emergency)
    {
        if (emergency)
        {
            write(emergencyOn);
            write(emergencyOn);
            write(emergencyOn);
            write(emergencyOn);
            write(emergencyOn);
        } else
        {
            write(emergencyOff);
            write(emergencyOff);
            write(emergencyOff);
            write(emergencyOff);
            write(emergencyOff);
        }
    }
}
