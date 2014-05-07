/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.comm;

/**
 * An exception used by KTA02.warehouse to indicates that there are too little
 * hardware devices available for the code to function.
 *
 * @author Roelof
 */
public class InsufficientDevicesException extends Exception {

    public static final int E_NO_DEVICES = 1;
    public static final int E_DEVICE_COUNT_TOO_LOW = 2;
    public static final int E_DEVICE_COUNT_TOO_HIGH = 3;

    /**
     * The error message
     */
    String message;
    /**
     * The error code
     */
    int code;

    /**
     * Creates a new InsufficientDevicesException with the given message
     *
     * @param message The message
     */
    public InsufficientDevicesException(String message) {
        this.message = message;
        this.code = -1;
    }

    /**
     * Creates a new InsufficientDevicesException with the given exception code
     *
     * @param code The exception code
     */
    public InsufficientDevicesException(int code) {
        this.message = "";
        this.code = code;
    }

    /**
     * Creates a new InsufficientDevicesException with the given message and
     * exception code
     *
     * @param message The message
     * @param code The exception code
     */
    public InsufficientDevicesException(String message, int code) {
        this.message = message;
        this.code = code;
    }

    /**
     * Returns the message
     *
     * @return The message of this Exception
     */
    @Override
    public String getMessage() {
        return message;

    }

    /**
     * Returns the exception code
     *
     * @return The exception code of this Exception
     */
    public int getCode() {
        return code;
    }

}
