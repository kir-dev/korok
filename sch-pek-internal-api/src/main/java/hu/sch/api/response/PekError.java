package hu.sch.api.response;

/**
 *
 * @author tomi
 */
public class PekError {

    private int code;
    private String message;

    public PekError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
