package hu.sch.services.exceptions;

/**
 * Base exception for all EJB exceptions.
 *
 * This should be thrown for most of the errors that occur in the EJB layer. If
 * an error should be handled differently, subclass the PekEJBException class.
 *
 * @author tomi
 */
public class PekEJBException extends Exception {

    private PekErrorCode errorCode;
    private Object[] parameters;

    public PekEJBException(PekErrorCode errorCode, Object... parameters) {
        this(errorCode, null, null, parameters);
    }

    public PekEJBException(PekErrorCode errorCode, String message, Object... parameters) {
        this(errorCode, message, null, parameters);
    }

    public PekEJBException(PekErrorCode errorCode, Throwable cause, Object... parameters) {
        this(errorCode,null, cause, parameters);
    }

    public PekEJBException(PekErrorCode errorCode, String message, Throwable cause, Object... parameters) {
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
