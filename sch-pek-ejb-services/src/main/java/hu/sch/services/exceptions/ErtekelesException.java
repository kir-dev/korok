package hu.sch.services.exceptions;

/**
 *
 * @author hege
 */
public class ErtekelesException extends Exception {

    /**
     * Creates a new instance of <code>ErtekelesException</code> without detail message.
     */
    public ErtekelesException() {
    }

    /**
     * Constructs an instance of <code>ErtekelesException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ErtekelesException(String msg) {
        super(msg);
    }
}
