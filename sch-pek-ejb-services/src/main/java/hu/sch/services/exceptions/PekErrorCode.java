package hu.sch.services.exceptions;

import static hu.sch.services.exceptions.PekErrorType.*;

/**
 * Error codes are returned to the client when something goes wrong while
 * processing a request.
 *
 * An error code is a 5-character string. The first two characters are the type
 * (or the class) of the error. It helps the client identify unknown errors. For
 * every error code there is a human readable message. Its sole purpose is
 * helping developers identify the error. It must not contain context specific
 * information.
 *
 * @author tomi
 */
public enum PekErrorCode {

    // ENTITY type errors
    ENTITY_CREATE_FAILED(ENITITY, "001", "failed to create entity"),
    ENTITY_UPDATE_FAILED(ENITITY, "002", "failed to update entity"),
    ENTITY_DUPLICATE(ENITITY, "003", "duplicate entity found"),
    ENTITY_NOT_FOUND(ENITITY, "004", "entity not found"),
    // VALIDATION type errors
    INVALID_IMAGE_MIME_TYPE(VALIDATION, "001", "invalid image mime-type"),
    TOKEN_NOT_FOUND(VALIDATION, "002", "token not found"),
    TOKEN_EXPIRED(VALIDATION, "003", "token is expired"),
    // IO type errors
    FILE_CREATE_FAILED(IO, "001", "failed to create file"),
    FILE_OPEN_FAILED(IO, "002", "failed to open file"),
    FILE_NOT_FOUND(IO, "003", "file not found"),
    // WEB type errors
    INVALID_REQUEST_TIMESTAMP(WEB, "001", "invalid timestamp"),
    INVALID_REQUEST_SIGNATURE(WEB, "002", "invalid signature"),
    INVALID_JSON_FORMAT(WEB, "003", "invalid json format"),
    MISSING_CONTENT(WEB, "004", "missing content"),
    RESOURCE_NOT_FOUND(WEB, "005", "resource not found"),
    METHOD_NOT_ALLOWED(WEB, "006", "method not allowed"),
    // SYSTEM type errors
    INTERNAL_ERROR(SYSTEM, "001", "internal error"),
    // ACOUNT type errors
    INVALID_PASSWORD(ACCOUNT, "001", "invalid password"),
    ;

    private final PekErrorType type;
    private final String code;
    private final String message;

    private PekErrorCode(PekErrorType type, String code, String message) {
        this.type = type;
        this.code = code;
        this.message = message;
    }

    /**
     * Gets the type (class) of the error.
     *
     * @return the type of the error
     */
    public PekErrorType getType() {
        return type;
    }

    /**
     * Gets the (3-character) code of the error.
     *
     * @return code of the error
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the human readable message of the error.
     *
     * @return message of the error
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the 5-character representation of the error
     *
     * @return
     */
    public String getValue() {
        return type.getValue() + getCode();
    }
}
