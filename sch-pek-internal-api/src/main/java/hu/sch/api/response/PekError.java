package hu.sch.api.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.sch.api.serializers.PekErrorCodeSerializer;
import hu.sch.util.exceptions.PekErrorCode;
import hu.sch.util.exceptions.PekErrorDetails;
import hu.sch.util.exceptions.PekException;

/**
 *
 * @author tomi
 */
public class PekError implements PekResponse {

    private final PekErrorCode error;
    private final Details details;

    public PekError(PekErrorCode error, String cause) {
        this.error = error;
        this.details = new Details(cause);
    }

    public PekError(PekException ex) {
        this.error = ex.getErrorCode();
        this.details = new Details(ex.getMessage(), ex.getDetails());
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @JsonSerialize(using = PekErrorCodeSerializer.class)
    @JsonUnwrapped
    public PekErrorCode getError() {
        return error;
    }

    public Details getDetails() {
        return details;
    }

    public static PekError internal(String cause) {
        return new PekError(PekErrorCode.INTERNAL_ERROR, cause);
    }

    public static class Details {

        private final String cause;
        private final PekErrorDetails errorDetails;

        public Details(String cause) {
            this(cause, null);
        }

        public Details(String cause, PekErrorDetails errorDetails) {
            this.cause = cause;
            this.errorDetails = errorDetails;
        }

        public String getCause() {
            return cause;
        }

        @JsonUnwrapped
        public PekErrorDetails getErrorDetails() {
            return errorDetails;
        }
    }
}
