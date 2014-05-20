/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.warehouse;

import java.awt.Point;
import java.util.ArrayList;
import kta02.comm.ArduinoConnection;

/**
 *
 * @author Roelof
 */
public class RobotMover implements Runnable
{

    private final int MOTOR_X = 1;
    private final int MOTOR_Y = 2;
    private final int MOTOR_Z = 1;
    private final int MOTOR_BIN = 1;

    private final int RESET_TIME_X = 12;
    private final int RESET_TIME_Y = 8;
    private final int RESET_TIME_Z = 2;

    private final int STORAGE_COLS = 6;
    private final int STORAGE_ROWS = 4;

    private final int STATE_RETRIEVE = 1;
    private final int STATE_EXTEND = 2;
    private final int STATE_PICKUP = 3;
    private final int STATE_RETRACT = 4;
    private final int STATE_RESET = 5;

    private final Point MARKER_END = new Point(0, STORAGE_ROWS);

    private ArduinoConnection movementArduino;
    private ArduinoConnection binPackingArduino;

    private int currentState;

    private Point currentDestination;

    private Thread moveR1;
    private Thread moveR2;
    private Thread resetThread;

    private int moveX = 0;
    private int moveY = 0;

    private boolean stateX = false;
    private boolean stateY = false;
    private boolean stateZ = false;
    private boolean stateB = false;

    private boolean threadsStarted = false;

    /**
     * Colum at which the robot is currently situated
     */
    private int locationX;
    /**
     * Row at which the robot is currently situated
     */
    private int locationY;
    /**
     * If true, the arm is extracted, if false the arm is contracted
     */
    private boolean locationZ;
    private boolean locationBin;

    private ArrayList<Point> fetchQueue;

    /**
     * Returns a boolean which indicates if the robot has been initialised.
     * Checks if the resetThread exists and has finished executing
     *
     * @return
     */
    private boolean canMove()
    {
        if (movementArduino == null || !movementArduino.isValidArduino())
        {
            return false;
        }
        if (binPackingArduino == null || !binPackingArduino.isValidArduino())
        {
            return false;
        }
        return true;
    }

    /**
     * Sets up everything and starts the reset thread, so the robot is always at
     * the same location
     */
    public RobotMover()
    {
        currentState = STATE_RESET;

        resetThread = new Thread(this);
        moveR1 = new Thread(this);
        moveR2 = new Thread(this);
    }

    private void activateThreads()
    {
        if (threadsStarted == true)
        {
            return;
        }
        if (binPackingArduino == null || movementArduino == null)
        {
            return;
        }

        if (!binPackingArduino.isOnline() || !binPackingArduino.isValidArduino())
        {
            return;
        }

        if (!movementArduino.isOnline() || !movementArduino.isValidArduino())
        {
            return;
        }

        if (!resetThread.isAlive())
        {
            resetThread.start();
        }
        if (!moveR1.isAlive())
        {
            moveR1.start();
        }
        if (!moveR2.isAlive())
        {
            moveR2.start();
        }
        threadsStarted = true;
    }

    public void setBinRobot(ArduinoConnection conn)
    {
        binPackingArduino = conn;
        activateThreads();
    }

    public void setMoveRobot(ArduinoConnection conn)
    {
        movementArduino = conn;
        activateThreads();
    }

    public void setBothArduinos(ArduinoConnection xyRobot, ArduinoConnection zbRobot)
    {
        setBinRobot(zbRobot);
        setMoveRobot(xyRobot);
    }

    /**
     * Adds a Point (x- and y-coordinate) to the to-be-fetched queue, which is
     * handled in order. The Arduino will return to it's start position as soon
     * as the queue has been completely emptied. Figuring out the shortest route
     * by itself.
     *
     * @param point The X/Y coordinate, X is the column, Y the row.
     * @throws IndexOutOfBoundsException If the row or column doesn't exist,
     * this exception is thrown
     */
    public void addToFetchQueue(Point point) throws IndexOutOfBoundsException
    {
        point.setLocation(
                (int) Math.floor(point.getX()),
                (int) Math.floor(point.getY())
        );
        if (point.getX() < 0 || point.getX() > STORAGE_COLS)
        {
            throw new IndexOutOfBoundsException("Column " + point.getY() + " doesn't exist");
        }

        if (point.getY() < 0 || point.getY() > STORAGE_ROWS)
        {
            throw new IndexOutOfBoundsException("Row " + point.getY() + " doesn't exist");
        }

        fetchQueue.add(point);

    }

    private synchronized void sleep(long duration)
    {
        try
        {
            Thread.sleep(duration);

        } catch (InterruptedException e)
        {
            // Say NO to the error!
        }
    }

    private synchronized void checkMotorLevels(ArduinoConnection conn)
    {
        if (currentState == STATE_RESET)
        {
            return;

        }
        if (conn == movementArduino)
        {
            int sensorX = conn.getSensorData(0);
            int sensorY = conn.getSensorData(1);

            if (sensorX == 1 != stateX)
            {
                stateX = sensorX == 1;
                if (stateX)
                {
                    locationX += (moveX > 0) ? 1 : -1;
                }
                if (currentDestination != null && locationX == currentDestination.x)
                {
                    conn.performAction(ArduinoConnection.ACTION_MOTOR1, ArduinoConnection.PARAM_MOTOR_STOP);
                }
            }
            if (sensorY == 1 != stateY)
            {
                stateY = sensorY == 1;
                if (stateY && currentState == STATE_PICKUP)
                {
                    conn.performAction(ArduinoConnection.ACTION_MOTOR2, ArduinoConnection.PARAM_MOTOR_STOP);
                } else if (stateY)
                {
                    locationY += (moveY > 0) ? 1 : -1;
                    if (currentDestination != null && locationY == currentDestination.y)
                    {
                        conn.performAction(ArduinoConnection.ACTION_MOTOR2, ArduinoConnection.PARAM_MOTOR_STOP);
                    }
                }
            }
        } else
        {
            int sensorZ = conn.getSensorData(0);
            int sensorB = conn.getSensorData(1);

            if (sensorZ == 1 != stateZ)
            {
                stateZ = sensorZ == 1;
                if (stateZ)
                {
                    conn.performAction(ArduinoConnection.ACTION_MOTOR1, ArduinoConnection.PARAM_MOTOR_STOP);
                    if (currentState == STATE_EXTEND)
                    {
                        currentState = STATE_PICKUP;
                    } else if (currentState == STATE_RETRACT)
                    {
                        selectNextTarget();
                    }
                }
            }
            if (sensorB == 1 != stateB)
            {
                stateB = sensorB == 1;
                if (stateB)
                {
                    conn.performAction(ArduinoConnection.ACTION_MOTOR2, ArduinoConnection.PARAM_MOTOR_STOP);
                }
            }
        }
    }

    private synchronized void doFullStop(ArduinoConnection conn, String motor)
    {
        conn.performAction(motor, ArduinoConnection.PARAM_MOTOR_FW1);
        sleep(100);
        conn.performAction(motor, ArduinoConnection.PARAM_MOTOR_STOP);
    }

    private synchronized boolean moveMotor(char direction, int speed)
    {
        String motor = null;
        String param;

        ArduinoConnection conn;

        switch (speed)
        {
            case -3:
                param = ArduinoConnection.PARAM_MOTOR_BW3;
                break;
            case -2:
                param = ArduinoConnection.PARAM_MOTOR_BW2;
                break;
            case -1:
                param = ArduinoConnection.PARAM_MOTOR_BW1;
                break;
            case 1:
                param = ArduinoConnection.PARAM_MOTOR_FW1;
                break;
            case 2:
                param = ArduinoConnection.PARAM_MOTOR_FW2;
                break;
            case 3:
                param = ArduinoConnection.PARAM_MOTOR_FW3;
                break;
            default:
                param = ArduinoConnection.PARAM_MOTOR_STOP;
        };

        if (direction == 'x' || direction == 'y')
        {
            conn = movementArduino;
        } else if (direction == 'z' || direction == 'b')
        {
            conn = binPackingArduino;
        } else
        {
            return false;
        }

        if (direction == 'x')
        {
            if (speed != 0)
            {
                moveX = (speed > 0) ? 1 : -1;
            }

            motor = ArduinoConnection.ACTION_MOTOR1;
        } else if (direction == 'y')
        {

            if (speed != 0)
            {
                moveY = (speed > 0) ? 1 : -1;
            }
            if (speed == 0 && moveY < 0)
            {
                doFullStop(conn, ArduinoConnection.ACTION_MOTOR2);
                return true;
            }

            motor = ArduinoConnection.ACTION_MOTOR2;
        } else if (direction == 'z')
        {
            motor = ArduinoConnection.ACTION_MOTOR1;
        } else if (direction == 'b')
        {
            motor = ArduinoConnection.ACTION_MOTOR2;
        }

        if (motor == null)
        {
            return false;
        }

        System.err.println("Running action " + param + " on " + motor + " on arduino " + conn.getTypeName());

        conn.performAction(motor, param);

        return true;
    }

    private synchronized void selectNextTarget()
    {
        if (fetchQueue.size() > 0)
        {
            currentDestination = fetchQueue.remove(fetchQueue.size() - 1);
        } else
        {
            currentDestination = MARKER_END;
        }
        currentState = STATE_RETRIEVE;
    }

    public void startPickup()
    {
        if (currentState == STATE_RETRIEVE)
        {
            currentState = STATE_EXTEND;
        }
    }

    private synchronized void runResetThread()
    {
        if (currentState != STATE_RESET)
        {
            return;
        }

        System.err.println("Starting Z motor");

        moveMotor('z', -1);
        sleep(RESET_TIME_Z * 1000);
        moveMotor('z', 0);

        sleep(50);
        System.err.println("Starting Y motor");

        moveMotor('y', 3);
        sleep(RESET_TIME_Y * 1000);
        moveMotor('y', 0);

        sleep(50);
        System.err.println("Starting X motor");

        moveMotor('x', -3);
        sleep(RESET_TIME_X * 1000);
        moveMotor('x', 0);

        sleep(50);
        currentState = STATE_RETRIEVE;

        Thread.currentThread().interrupt();
    }

    private synchronized void runMotor1Thread()
    {
        checkMotorLevels(movementArduino);
        if (currentState == STATE_PICKUP)
        {
            moveMotor('y', 3);
            sleep(700);
            moveMotor('y', 0);
            currentState = STATE_RETRACT;
        } else if (currentDestination == MARKER_END)
        {
            /// moveMotor('y', 3);
            // moveMotor('x', -3);
        }
    }

    private synchronized void runMotor2Thread()
    {
        checkMotorLevels(binPackingArduino);
        if (currentState == STATE_EXTEND)
        {
            moveMotor('z', 1);
            sleep(1000);
            moveMotor('z', 0);

            currentState = STATE_PICKUP;
        } else if (currentState == STATE_RETRACT)
        {
            moveMotor('z', -1);
            sleep(2000);
            moveMotor('z', 0);

            currentState = STATE_RETRIEVE;
        }
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            if (!canMove())
            {
                //Wait a little
                sleep(100);
            } else if (Thread.currentThread().equals(resetThread))
            {
                System.out.println("Running reset thread");
                runResetThread();
                break;
            } else if (currentState == STATE_RESET)
            {
                // Also wait a bit
                sleep(250);
            } else if (Thread.currentThread().equals(moveR1))
            {
                runMotor1Thread();
            } else if (Thread.currentThread().equals(moveR2))
            {
                runMotor2Thread();
            } else
            {
                System.err.println("Unknown thread running!");
                break;
            }
            sleep(50);
        }
    }
}
