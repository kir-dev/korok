package hu.sch.services.exceptions;

/**
 *
 * @author Adam Lantos
 */
public class InvalidPasswordException extends Exception {

    public InvalidPasswordException() {
    }

    public InvalidPasswordException(String message) {
        super(message);
    }

}
