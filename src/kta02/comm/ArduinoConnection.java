/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.comm;

import gnu.io.CommPortIdentifier;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author Roelof
 */
public class ArduinoConnection extends ArduinoBackend implements Runnable
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
     * Indicates that this Arduino isn't ready yet
     */
    public static final char TYPE_OFFLINE = 'w';

    /**
     * Arduino type
     */
    private char arduinoType;

    private String arduinoName;

    public ArduinoConnection(CommPortIdentifier portNumber) throws IOException
    {
        super(portNumber);
        this.arduinoType = ArduinoConnection.TYPE_NONE;
        this.arduinoName = "n/a";
    }

    public char getType()
    {
        return arduinoType;
    }

    public String getName()
    {
        return arduinoName;
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
        return write(command);
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

    @Override
    public void run()
    {
        final long MAX_DELAY = 4 * 1000;

        arduinoType = TYPE_OFFLINE;

        boolean isSent = false;
        long sentTime = new Date().getTime();

        while (!Thread.currentThread().isInterrupted())
        {
            if (isOnline())
            {
                if (!isSent)
                {
                    arduinoType = TYPE_LOADING;
                    this.write("i");
                    isSent = true;
                    sentTime = new Date().getTime();
                } else
                {
                    String tempLast = getLastData();
                    if (tempLast.length() > 1 && tempLast.charAt(0) == 'i')
                    {
                        arduinoType = tempLast.charAt(1);
                        break;
                    } else if (sentTime < new Date().getTime() - MAX_DELAY)
                    {
                        break;
                    }
                }
            }
            try
            {
                Thread.sleep((long) 100);

            } catch (InterruptedException e)
            {

            }
        }
        if (arduinoType == TYPE_LOADING)
        {
            arduinoType = TYPE_NONE;
        }
    }

}
