package hu.sch.services.exceptions;

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

    // DATABASE ERRORS 1xx
    DATABASE_CREATE_FAILED(100),
    DATABASE_UPDATE_FAILED(101),
    // VALIDATION ERRORS 2xx
    VALIDATION_IMAGE_FORMAT(200),
    // FILE IO ERRORS 3xx
    FILE_CREATE_FAILED(300),
    FILE_OPEN_FAILED(301),
    // SYSTEM ERRROS 4xx
    SYSTEM_ENCODING_NOTSUPPORTED(400),
    // USER ERRORS 5xx
    USER_NOTFOUND(500),
    USER_PASSWORD_INVALID(501)
    ;
    //------------------------------------------
    private static final String MESSAGE_KEY_PREFIX = "ejb.error.";
    private int code;
    private String messageKey;

    private PekErrorCode(int code) {
        this(code, null);
    }

    private PekErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    /**
     * Gets the numerical code for the error.
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets the message key for this error code.
     *
     * If no message key specified the name of the errorcode is used. There is
     * always a prefix: ejb.error
     *
     * The underscores (_) will be replaced by dots (.) in the name.
     *
     * So for the DATABASE_CREATE_FAILED errorcode the message key will be
     * "ejb.error.database.create.failed".
     */
    public String getMessageKey() {
        String key = messageKey;
        if (StringUtils.isBlank(key)) {
            key = this.name().toLowerCase().replace('_', '.');
        }
        return MESSAGE_KEY_PREFIX.concat(key);
    }
}
