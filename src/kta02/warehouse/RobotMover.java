/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.warehouse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import kta02.comm.ArduinoConnection;

/**
 *
 * @author Roelof
 */
public class RobotMover extends RobotConfig
{

    private final Point MARKER_END = new Point(0, STORAGE_ROWS);

    private ArduinoConnection movementArduino;
    private ArduinoConnection binPackingArduino;

    private int currentState;

    private Point currentDestination;
    private int currentIndex = 0;

    private Thread resetThread;
    private Thread retrieveThread;

    private int moveX = 0;
    private int moveY = 0;

    private int currentPosX = 0;
    private int currentPosY = 0;
    private int numberOfPackets = 0;

    private boolean stateX = false;
    private boolean stateY = false;
    private boolean stateZ = false;
    private boolean stateB = false;

    private boolean threadsStarted = false;

    private final Warehouse warehouse;

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
     * Sets up everything and starts the reset thread, so the robot is always at
     * the same location
     *
     * @param wh Instance of a warehouse
     */
    public RobotMover(Warehouse wh)
    {
        warehouse = wh;
        currentState = STATE_RESET;

        fetchQueue = new ArrayList<>();

        retrieveThread = new Thread(new robotMoveThread());
        resetThread = new Thread(new robotResetThread());
    }

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
        threadsStarted = true;
    }

    /**
     * Sets the robot that is used for bin movement and arm extension /
     * retraction.
     *
     * @param conn
     */
    public void setBinRobot(ArduinoConnection conn)
    {
        binPackingArduino = conn;
        activateThreads();
    }

    /**
     * Sets the robot that is used for x/y movementF
     *
     * @param conn
     */
    public void setMoveRobot(ArduinoConnection conn)
    {
        movementArduino = conn;
        activateThreads();
    }

    /**
     * Utility function to set both the bin/arm robot and the x/y robot
     *
     * @param xyRobot Robot used for X/Y movement
     * @param zbRobot Robot used for bin / arm movement
     */
    public void setBothArduinos(ArduinoConnection xyRobot, ArduinoConnection zbRobot)
    {
        setBinRobot(zbRobot);
        setMoveRobot(xyRobot);
    }

    /**
     * Tells the robot to start retrieveing the selected items
     *
     * @todo This function needs to be finished, as it's already being called
     * from within Warehouse
     * @param list
     */
    public void retrieveItems(ArrayList<Point> list)
    {
        if (!fetchQueue.isEmpty())
        {
            return;
        }

        fetchQueue.clear();
        fetchQueue.addAll(list);

        if (!retrieveThread.isAlive())
        {
            retrieveThread.start();
        }
    }

    /**
     * Makes the code sleep for a while, while simultaneously catching the
     * InterruptedException so you don't have to handle it.
     *
     * @param duration How long to sleep, in milliseconds
     */
    private synchronized void sleep(long duration)
    {
        try
        {
            Thread.currentThread().sleep(duration);

        } catch (InterruptedException e)
        {
            // Say NO to the error!
        }
    }

    /**
     * Completely stops an Arduino. Use this if you're moving the arduino down
     *
     * @param conn The connection to send the command
     * @param motor
     */
    private synchronized void doFullStop(ArduinoConnection conn, String motor)
    {
        conn.performAction(motor, ArduinoConnection.PARAM_MOTOR_FW1);
        sleep(100);
        conn.performAction(motor, ArduinoConnection.PARAM_MOTOR_STOP);
    }

    /**
     * Tells a motor to move at a certain velocity
     *
     * @param direction The motor to move, a char. Options:<ul><li>'x' -
     * left/right</li><li>'y' - up/down</li><li>'z' - arm
     * extend/retract</li><li>'b' - Bins forward/backward</li></ul>
     * @param speed The direction to move, ranges between --1
     * @return
     */
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

    /**
     * Moves the robot one column to the right
     */
    private synchronized void moveRight()
    {

        moveMotor('x', 2);
        sleep(Math.round(MOVE_X_SYNC * MOVE_X_RIGHT));
        moveMotor('x', 0);

        this.currentPosX++;

        sleep(1000);
    }

    /**
     * Moves the robot one column to the left
     */
    private synchronized void moveLeft()
    {
        moveMotor('x', -2);
        sleep(Math.round(MOVE_X_SYNC * MOVE_X_LEFT));
        moveMotor('x', 0);

        this.currentPosX--;

        sleep(1000);
    }

    /**
     * Moves the robot one row upwards
     */
    private synchronized void moveUp()
    {
        moveMotor('y', 3);
        sleep(Math.round(MOVE_Y_SYNC * (1 + this.numberOfPackets * 0.07f) * MOVE_Y_UP));
        moveMotor('y', 0);

        this.currentPosY--;

        sleep(1000);
    }

    /**
     * Moves the robot one row downwards
     */
    private synchronized void moveDown()
    {
        moveMotor('y', -2);
        sleep(Math.round(MOVE_Y_SYNC * (1 - this.numberOfPackets * 0.02f) * MOVE_Y_DOWN));
        moveMotor('y', 0);

        this.currentPosY++;

        sleep(1000);
    }

    /**
     * Starts a pickup action, extracting the arm, lifting the robot and
     * retracting the arm, to then lower the robot to it's start position.
     */
    private synchronized void pickUp()
    {
        this.currentState = STATE_PICKUP;
        moveMotor('z', 3);
        sleep(850);
        moveMotor('z', 0);
        sleep(500);
        moveMotor('y', 3);
        sleep(Math.round(MOVE_Y_SYNC * 300));
        moveMotor('y', 0);
        sleep(500);
        moveMotor('z', -3);
        sleep(1200);
        moveMotor('z', 0);
        sleep(500);

        this.numberOfPackets++;

        moveMotor('y', -2);
        sleep(Math.round(MOVE_Y_SYNC * 245));
        moveMotor('y', 0);
        sleep(500);
    }

    /**
     * Moves the robot from <strong>(0,0)</strong> to the bin deposit point.
     */
    private synchronized void moveToBins()
    {
        moveMotor('y', 2);
        sleep(Math.round(MOVE_Y_SYNC * 1200));
        moveMotor('y', 0);
        sleep(1000);
        moveMotor('x', -2);
        sleep(1200);
        moveMotor('x', 0);
        sleep(1000);

        moveMotor('z', 3);
        sleep(2500);
        moveMotor('z', 0);
        sleep(1000);

        moveMotor('y', -1);
        sleep(Math.round(MOVE_Y_SYNC * 400));
        moveMotor('y', 0);

    }

    private synchronized void runResetter()
    {

        if (currentState != STATE_RESET)
        {
            return;
        }

        moveMotor('z', -3);
        sleep(2500);
        moveMotor('z', 0);
        sleep(500);

        moveMotor('y', 3);
        sleep(3000);
        moveMotor('y', 0);
        sleep(500);

        moveMotor('x', -2);
        sleep(4000);
        moveMotor('x', 0);
        sleep(500);
        
        moveMotor('x', 2);
        sleep(Math.round(MOVE_X_SYNC * 1100));
        moveMotor('x', 0);
        sleep(500);
        
        moveMotor('y', -1);
        sleep(Math.round(MOVE_Y_SYNC * 400));
        moveMotor('y', 0);
        sleep(500);

        currentState = STATE_IDLE;
    }

    /**
     * Runner for the main thread
     */
    private synchronized boolean runQueueRetrievalThread()
    {
        if (currentState != STATE_IDLE)
        {
            return false;
        }

        this.numberOfPackets = 0;
        
        /*
        this.fetchQueue.empty();
        this.fetchQueue.add(new Point(1, 1));
        this.fetchQueue.add(new Point(1, 2));
        this.fetchQueue.add(new Point(4, 2));
        this.fetchQueue.add(new Point(2, 0));
        */
        
        // always go back to zero
        this.fetchQueue.add(new Point(0, 0));

        //Index of the current package
        this.currentIndex = 0;

        // loop trough all the positions where the robot needs to pick up packages
        for (Point point : this.fetchQueue)
        {
            this.currentState = STATE_RETRIEVE;
            // adjust the x position until its the same of the next point
            while (this.currentPosX != point.x)
            {
                if (this.currentPosX < point.x)
                {
                    this.moveRight();
                } else
                {
                    this.moveLeft();
                }
            }
            // adjust the y position until its the same of the next point
            while (this.currentPosY != point.y)
            {
                if (this.currentPosY < point.y)
                {
                    this.moveDown();
                } else
                {
                    this.moveUp();
                }
            }
            if (point != this.fetchQueue.get(this.fetchQueue.size() - 1))
            {
                this.currentState = STATE_PICKUP;
                this.pickUp();
            } else
            {
                this.currentState = STATE_DEPOSIT;
                this.moveToBins();
            }
            this.currentIndex++;
        }

        return true;
    }

    /**
     * Runs the thread that pulls the packages off the arm and puts them into
     * the bin.
     */
    public void runBinDepositThread()
    {
        // Reverse the fetch queue
        Collections.reverse(fetchQueue);

        for (Point location : fetchQueue)
        {
            // Do stuff with the point
        }
    }

    private class robotMoveThread implements Runnable
    {

        @Override
        public void run()
        {
            while (!Thread.currentThread().isInterrupted())
            {
                if (!canMove() || currentState == STATE_RESET)
                {
                    //Wait a little
                    sleep(100);
                } else
                {
                    if (runQueueRetrievalThread())
                    {
                        runBinDepositThread();
                    }
                    break;
                }
                sleep(50);
            }
        }

    }

    private class robotResetThread implements Runnable
    {

        @Override
        public void run()
        {
            while (!Thread.currentThread().isInterrupted())
            {
                if (!canMove() || currentState != STATE_RESET)
                {
                    //Wait a little
                    sleep(100);
                } else
                {
                    System.out.println("Running reset thread");
                    runResetter();
                    break;
                }
                sleep(50);
            }
        }

    }

    public int getCurrentIndex()
    {
        return currentIndex;
    }

    /**
     * Returns the current state, which is one of the STATE_ contants.
     *
     * @return
     */
    public int getCurrentState()
    {
        return currentState;
    }

    /**
     * DEPRECATED FUNCTIONS
     */
    /**
     * Returns the number of items that are in the queue
     *
     * @return
     * @deprecated Deprecated in favor of getCurrentIndex
     */
    public int getQueueLength()
    {
        return 0;
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
     * @deprecated Use retrieveItems
     */
    public void addToFetchQueue(Point point) throws IndexOutOfBoundsException
    {

    }

    /**
     * Pulls the next entry off the fetch queue and sets the
     * <code>currentState</code> to <code>STATE_RETRIEVE</code>. If there are no
     * more destinations to go to the <code>currentDestination</code> is set to
     * <code>MARKER_END</code>.
     *
     * @deprecated Deprecated in favour of internal target selection via
     * <code>for</code> loop.
     */
    private synchronized void selectNextTarget()
    {
        return;
    }

    /**
     * Starts a pickup, can only be called when not already picking stuff up.
     *
     * @deprecated Deprecated in favour of internal pickup in <code>for</code>
     * loop.
     */
    public void startPickup()
    {
        return;
    }
}
