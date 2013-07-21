package hu.sch.services.exceptions;

/**
 *
 * @author tomi
 */
public class DuplicatedUserException extends Exception {

    public DuplicatedUserException(String message) {
        super(message);
    }

    public DuplicatedUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
