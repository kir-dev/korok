/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.services.exceptions.valuation;

/**
 *
 * @author balint
 */
public class ValuationException extends Exception {

    /**
     * Creates a new instance of <code>ValuationException</code> without detail message.
     */
    public ValuationException() {
    }


    /**
     * Constructs an instance of <code>ValuationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ValuationException(String msg) {
        super(msg);
    }
}
