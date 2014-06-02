/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.warehouse;

/**
 * Holds all the configurable parameters for RobotMover
 *
 * @author Roelof
 */
public abstract class RobotConfig
{

    /**
     * Duration of move in between regular columns
     */
    protected final float MOVE_X_SYNC = 0.95f;
    protected final float MOVE_Y_SYNC = 0.94f;

    protected final int MOVE_X_RIGHT = 730;
    /**
     * Duration of move in between regular columns
     */
    protected final int MOVE_X_LEFT = 750;
    /**
     * Duration of move in between the column block and the drop-off point
     */
    protected final int MOVE_X_BASE = 1200;
    /**
     * Duration of the startup (added to the total move time)
     */
    protected final int MOVE_X_CORRECTION = 100;

    /**
     * Duration of moving up one column
     */
    protected final int MOVE_Y_UP = 610;
    /**
     * Duration of moving down one column
     */
    protected final int MOVE_Y_DOWN = 610;
    /**
     * Duration of moving from the row block to max height
     */
    protected final int MOVE_Y_UP_MAX = 600;
    /**
     * Duration of moving from the max height to the row block
     */
    protected final int MOVE_Y_DOWN_MAX = 600;
    /**
     * Duration of the startup (added to the total move time)
     */
    protected final int MOVE_Y_DEPOSIT = 300;

    /**
     * Correction of each move, applied to both up and down movements (added to
     * the total move time)
     */
    protected final int MOVE_Y_CORRECTION = 100;

    /**
     * How long the robot should move up with the arm extended
     */
    protected final int MOVE_Y_PICKUP = 400;

    /**
     * How long the arm needs to extend to the desired position
     */
    protected final int MOVE_Z_IN_DUR = 4000;
    /**
     * How long the arm needs to retract to the move position
     */
    protected final int MOVE_Z_OUT_DUR = 4500;

    protected final int MOVE_B_DUR = 1000;

    protected final int RESET_TIME_X = 8;
    protected final int RESET_TIME_Y = 6;
    protected final int RESET_TIME_Z = 3;

    protected final int STORAGE_COLS = 6;
    protected final int STORAGE_ROWS = 4;

    public static final int STATE_RETRIEVE = 1;
    public static final int STATE_PICKUP = 2;
    public static final int STATE_RESET = 3;
    public static final int STATE_DEPOSIT = 4;
    public static final int STATE_IDLE = 5;

}
