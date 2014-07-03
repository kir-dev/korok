package hu.sch.services.exceptions;

/**
 * Type for PekErrorCode. Every error code has a 2-character type.
 *
 * The type denotes the class of the error. Basically it helps to the client to
 * indentify "unknown" errors.
 *
 * @author tomi
 */
public enum PekErrorType {

    ENITITY("01"),
    VALIDATION("02"),
    IO("03"),
    WEB("04"),
    SYSTEM("05"),
    ACCOUNT("06")
    ;

    private final String value;

    private PekErrorType(String value) {
        this.value = value;
    }

    /**
     * Gets 2-character representation of the error type.
     *
     * @return
     */
    public String getValue() {
        return value;
    }

}
