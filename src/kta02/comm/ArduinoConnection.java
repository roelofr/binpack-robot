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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.TooManyListenersException;

/**
 *
 * @author Roelof
 */
public class ArduinoConnection implements SerialPortEventListener, Runnable
{

    // Action definitions
    /**
     * Verification action, to be used by SerialCom only
     */
    public static final String ACTION_VERIFY = "i";
    /**
     * Identification action, to be used by SerialCom only
     */
    public static final String ACTION_WHOIS = "i";
    /**
     * Action sent to motor 1
     */
    public static final String ACTION_MOTOR1 = "j";
    /**
     * Action sent to motor 2
     */
    public static final String ACTION_MOTOR2 = "k";

    // Action parameters
    /**
     * Motor, move backwards at speed 3
     */
    public static final String PARAM_MOTOR_BW3 = "1";
    /**
     * Motor, move backwards at speed 2
     */
    public static final String PARAM_MOTOR_BW2 = "2";
    /**
     * Motor, move backwards at speed 1
     */
    public static final String PARAM_MOTOR_BW1 = "3";
    /**
     * Motor, stop
     */
    public static final String PARAM_MOTOR_STOP = "4";
    /**
     * Motor, move forward at speed 3
     */
    public static final String PARAM_MOTOR_FW3 = "7";
    /**
     * Motor, move forward at speed 2
     */
    public static final String PARAM_MOTOR_FW2 = "6";
    /**
     * Motor, move forward at speed 1
     */
    public static final String PARAM_MOTOR_FW1 = "5";

    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port. (baud)
     */
    private static final int DATA_RATE = 9600;
    /**
     * Application name, used for connection identification
     */
    private static final String APP_NAME = "KTA02 Warehouse App";

    /**
     * Indicates that this is a motor/motor Arduino (x/y movement)
     */
    public static final char TYPE_MOTOR = 'a';

    /**
     * Indicates that this is a motor/bin Arduino (z/bin movement)
     */
    public static final char TYPE_BIN = 'b';

    /**
     * Indicates that the type of this Arduino isn't known just yet.
     */
    public static final char TYPE_LOADING = 'l';

    /**
     * Indicates that this Arduino's type could not be determined.
     */
    public static final char TYPE_NONE = 'x';

    /**
     * Serial Port connection
     */
    SerialPort serialPort;

    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent
     */
    private BufferedReader input;
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

    /**
     * Arduino type
     */
    private char arduinoType;

    private Thread arduinoTypeThread;

    /**
     * Indicates if the port is closed, all commands will fail if this happens.
     */
    private Boolean portClosed;

    public ArduinoConnection(CommPortIdentifier portNumber)
    {
        lastRead = 0;
        lastData = "";
        portClosed = false;
        arduinoType = ArduinoConnection.TYPE_NONE;
        try
        {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portNumber.open(APP_NAME,
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            arduinoType = ArduinoConnection.TYPE_LOADING;

            arduinoTypeThread = new Thread(this);
            arduinoTypeThread.start();

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

    public char getType()
    {
        return arduinoType;
    }

    /**
     * Performs the action in <code>action</code> using <code>parameter</code>
     * as parameter.
     *
     * @param action An action, see the ACTION_ constants for this
     * @param parameter A parameter, see the PARAM_ constants for this
     * @return TRUE on success, FALSE on failure
     */
    public synchronized Boolean performAction(String action, String parameter)
    {
        String command = action + parameter;
        return write(command.getBytes());
    }

    /**
     * Performs the action in <code>action</code> using <code>parameter</code>
     * as parameter until new data is delivered by the connection.
     * <strong>Always call this in a new Thread since it will freeze the
     * application if not called in one!</strong>
     *
     * @param action
     * @param parameter
     * @param timeout
     * @return
     */
    public synchronized String performWhileSilent(String action, String parameter, int timeout) throws IOException, Exception
    {
        long maxDelay = Math.min(30, timeout) * 1000;

        double timeoutAt = new Date().getTime() + maxDelay;
        double lastSend = getLastRead();
        while (getLastRead() == lastSend)
        {
            if (!performAction(action, parameter))
            {
                throw new IOException("Failed to send data!");
            }

            try
            {
                wait(250);
            } catch (InterruptedException e)
            {
                // Do nothing with the Exception
            }
            if (new Date().getTime() > timeoutAt)
            {
                throw new Exception("Return data took more than the max duration");
            }
        }
        return getLastData();
    }

    public synchronized String performWhileSilent(String action, String parameter) throws IOException, Exception
    {
        try
        {
            return performWhileSilent(action, parameter, 30);
        } catch (Exception e)
        {
            throw e;
        }
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
            portClosed = true;
        }
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
     * Handle an event on the serial port. Read the data and print it.
     *
     * @param oEvent The event data
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                String inputLine = input.readLine();
                lastData = inputLine;
                lastRead = new Date().getTime();
            } catch (Exception e)
            {
                System.err.println(e.toString());
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
    private Boolean write(byte data[])
    {
        if (portClosed)
        {
            return false;
        }

        try
        {
            output.write(data);
        } catch (IOException e)
        {
            System.err.println("Error when sending data! " + e.getMessage());
            return false;
        }
        return true;

    }

    @Override
    public void run()
    {

        final int TYPESTATE_NONE = 0;
        final int TYPESTATE_SENT = 0;

        final long MAX_DELAY = 4 * 1000;

        arduinoType = TYPE_LOADING;

        boolean isSent = false;
        long sentTime = new Date().getTime();

        while (!Thread.currentThread().isInterrupted())
        {
            if (!isSent)
            {
                this.write("i".getBytes());
                isSent = true;
                sentTime = new Date().getTime();
            } else
            {
                String tempLast = getLastData();
                if (tempLast.charAt(0) == 'i')
                {
                    arduinoType = tempLast.charAt(1);
                    break;
                } else if (sentTime < new Date().getTime() - MAX_DELAY)
                {
                    break;
                }
            }
            try
            {
                wait(100);
            } catch (InterruptedException e)
            {
                break;
            }
        }
        if (arduinoType == TYPE_LOADING)
        {
            arduinoType = TYPE_NONE;
        }
    }

}
