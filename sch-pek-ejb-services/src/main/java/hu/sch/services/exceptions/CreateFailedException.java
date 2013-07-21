package hu.sch.services.exceptions;

/**
 *
 * @author tomi
 */
public class CreateFailedException extends Exception {

    public CreateFailedException(String message) {
        super(message);
    }

    public CreateFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
