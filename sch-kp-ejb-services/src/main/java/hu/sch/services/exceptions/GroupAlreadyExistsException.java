/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.services.exceptions;

/**
 *
 * @author hege
 */
public class GroupAlreadyExistsException extends Exception {

    /**
     * Creates a new instance of <code>GroupAlreadyExistsException</code> without detail message.
     */
    public GroupAlreadyExistsException() {
    }


    /**
     * Constructs an instance of <code>GroupAlreadyExistsException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public GroupAlreadyExistsException(String msg) {
        super(msg);
    }
}
