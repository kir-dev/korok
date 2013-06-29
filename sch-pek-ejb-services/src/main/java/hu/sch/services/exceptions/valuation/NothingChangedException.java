/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.services.exceptions.valuation;

/**
 *
 * @author balint
 */
public class NothingChangedException extends ValuationException {

    /**
     * Creates a new instance of <code>NothingChangedException</code> without detail message.
     */
    public NothingChangedException() {
    }


    /**
     * Constructs an instance of <code>NothingChangedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NothingChangedException(String msg) {
        super(msg);
    }
}
