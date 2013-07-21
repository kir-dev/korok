package hu.sch.services.exceptions;

/**
 *
 * @author tomi
 */
public class DuplicateUserException extends Exception {

    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
