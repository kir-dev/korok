/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.services.exceptions;

/**
 *
 * @author hege
 */
public class NoSuchAttributeException extends RuntimeException {

    /**
     * Creates a new instance of <code>NoSuchAttributeException</code> without detail message.
     */
    public NoSuchAttributeException() {
    }


    /**
     * Constructs an instance of <code>NoSuchAttributeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoSuchAttributeException(String msg) {
        super(msg);
    }
}
