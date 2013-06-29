/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.services.exceptions.valuation;

/**
 *
 * @author balint
 */
public class NoExplanationException extends ValuationException {

    /**
     * Creates a new instance of <code>NoExplanation</code> without detail message.
     */
    public NoExplanationException() {
    }


    /**
     * Constructs an instance of <code>NoExplanation</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoExplanationException(String msg) {
        super(msg);
    }
}
