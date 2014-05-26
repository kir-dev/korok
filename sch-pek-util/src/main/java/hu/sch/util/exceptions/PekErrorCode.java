package hu.sch.util.exceptions;

import org.apache.commons.lang3.StringUtils;

/**
 * Error codes for the exceptions. It has a code and a message key for
 * localization purposes.
 *
 * The name of the error codes should be hierarchical so that it is easy to
 * understand what it refers to. The naming convention is the following:
 *
 * TYPE_[SUBTYPE_]OPERATION_WHATJUSTHAPPENED
 *
 * Eg. DATABASE_CREATE_FAILED refers to a database error, create operation and
 * that it failed.
 *
 * @author tomi
 */
public enum PekErrorCode {

    // TODO: add proper messages to error (github/#41)
    // TODO: rename DATABASE to ENTITY (github/#41)
    // TODO: create an error for all custom exceptions from sch-pek-ejb* (github/#41)

    UNSPECIFIED(-1, "unspecified error"),
    // ENTITY ERRORS 1xx
    DATABASE_CREATE_FAILED(100),
    DATABASE_UPDATE_FAILED(101),
    DATABASE_CREATE_VALUATION_DUPLICATE(102),
    ENTITY_NOTFOUND(103, "entity not found"),
    // VALIDATION ERRORS 2xx
    VALIDATION_IMAGE_FORMAT(200),
    VALIDATION_TOKEN_EXPIRED(201),
    VALIDATION_TOKEN_NOTFOUND(202),
    // FILE IO ERRORS 3xx
    FILE_CREATE_FAILED(300),
    FILE_OPEN_FAILED(301),
    // SYSTEM ERRROS 4xx
    SYSTEM_ENCODING_NOTSUPPORTED(400),
    UNKNOWN(401),
    // USER ERRORS 5xx
    USER_NOTFOUND(500),
    USER_PASSWORD_INVALID(501),
    // web layer errors 6xx
    REQUEST_TIMESTAMP_INVALID(600, "invalid timestamp"),
    REQUEST_SIGNATURE_INVALID(601, "invalid signature"),
    REQUEST_FORMAT_INVALID(602, "invalid request format"),
    RESOURCE_NOT_FOUND(604, "resource not found");
    //------------------------------------------
    private int code;
    private String shortMessage;

    private PekErrorCode(int code) {
        this(code, null);
    }

    private PekErrorCode(int code, String shortMessage) {
        this.code = code;
        this.shortMessage = shortMessage;
    }

    /**
     * Gets the numerical code for the error.
     */
    public int getCode() {
        return code;
    }

    public String getShortMessage() {
        String msg = shortMessage;
        if (StringUtils.isBlank(msg)) {
            msg = this.name().toLowerCase().replace('_', ' ');
        }
        return msg;
    }
}
