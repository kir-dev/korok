/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.services.exceptions;

/**
 *
 * @author hege
 */
public class UserAlreadyExistsException extends Exception {

    /**
     * Creates a new instance of <code>UserAlreadyExistsException</code> without detail message.
     */
    public UserAlreadyExistsException() {
    }


    /**
     * Constructs an instance of <code>UserAlreadyExistsException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
