/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.services.exceptions.valuation;

/**
 *
 * @author balint
 */
public class AlreadyModifiedException extends ValuationException {

    /**
     * Creates a new instance of <code>AlreadyModifiedException</code> without detail message.
     */
    public AlreadyModifiedException() {
    }


    /**
     * Constructs an instance of <code>AlreadyModifiedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AlreadyModifiedException(String msg) {
        super(msg);
    }
}
