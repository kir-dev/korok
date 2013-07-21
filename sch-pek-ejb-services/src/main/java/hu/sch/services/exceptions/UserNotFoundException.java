package hu.sch.services.exceptions;

/**
 *
 * @author tomi
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
