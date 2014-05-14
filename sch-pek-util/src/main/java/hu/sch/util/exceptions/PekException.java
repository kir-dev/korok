package hu.sch.util.exceptions;

/**
 * Base exception for all PEK exceptions.
 *
 * This should be thrown for most of the errors that occur in the application. If
 * an error should be handled differently, subclass the PekException class.
 *
 * @author tomi
 */
public class PekException extends Exception {

    private PekErrorCode errorCode;
    private Object[] parameters;

    public PekException(PekErrorCode errorCode, Object... parameters) {
        this(errorCode, null, null, parameters);
    }

    public PekException(PekErrorCode errorCode, String message, Object... parameters) {
        this(errorCode, message, null, parameters);
    }

    public PekException(PekErrorCode errorCode, Throwable cause, Object... parameters) {
        this(errorCode,null, cause, parameters);
    }

    public PekException(PekErrorCode errorCode, String message, Throwable cause, Object... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public PekErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
