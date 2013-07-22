package hu.sch.services.exceptions;

/**
 *
 * @author hege
 */
public class UserAlreadyExistsException extends Exception {

    private String uid;

    /**
     * Creates a new instance of <code>UserAlreadyExistsException</code> without detail message.
     */
    public UserAlreadyExistsException() {
    }

    /**
     * Constructs an instance of <code>UserAlreadyExistsException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UserAlreadyExistsException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>UserAlreadyExistsException</code> with the specified detail message.
     *
     * @param msg wicket message key
     * @param uid person.uid, it will be replaced in the message template
     */
    public UserAlreadyExistsException(final String msg, final String uid) {
        super(msg);
        this.uid = uid;
    }

    /**
     *
     * @return person.uid
     */
    public String getUid() {
        return uid;
    }
}
