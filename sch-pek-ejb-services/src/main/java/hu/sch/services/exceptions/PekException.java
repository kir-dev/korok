package hu.sch.services.exceptions;

import javax.ejb.ApplicationException;

/**
 * Base exception for all PEK exceptions.
 *
 * This should be thrown for most of the errors that occur in the application.
 * If an error should be handled differently, subclass the PekException class.
 *
 * @author tomi
 */
@ApplicationException(inherited = true, rollback = true)
public class PekException extends RuntimeException {

    private final PekErrorCode errorCode;
    private final PekErrorDetails details;

    public PekException(PekErrorCode errorCode, String message) {
        this(errorCode, message, null, null);
    }

    public PekException(PekErrorCode errorCode, String message, Throwable cause) {
        this(errorCode, message, cause, null);
    }

    public PekException(PekErrorCode errorCode, String message, PekErrorDetails details) {
        this(errorCode, message, null, details);
    }

    public PekException(PekErrorCode errorCode, String message, Throwable cause, PekErrorDetails details) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    public PekErrorCode getErrorCode() {
        return errorCode;
    }

    public PekErrorDetails getDetails() {
        return details;
    }
}