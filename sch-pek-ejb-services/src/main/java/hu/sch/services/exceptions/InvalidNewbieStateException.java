package hu.sch.services.exceptions;

/**
 * Exception used in register.
 * Indicates whether the user gives different newbie state than
 * we have in our database
 *
 * @author balo
 */
public class InvalidNewbieStateException extends Exception {

    /**
     * Constructs an instance of
     * <code>InvalidNewbieStateException</code> with the specified detail
     * message.
     *
     * @param msg the detail message key
     */
    public InvalidNewbieStateException(String msg) {
        super(msg);
    }
}
