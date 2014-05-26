package hu.sch.api.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.sch.api.serializers.PekErrorCodeSerializer;
import hu.sch.util.exceptions.PekErrorCode;
import hu.sch.util.exceptions.PekException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tomi
 */
public class PekError implements PekResponse {

    private static final String CAUSE_KEY = "cause";

    private final PekErrorCode error;
    private final Map<String, Object> extra;

    public PekError(PekErrorCode error) {
        this.error = error;
        this.extra = new HashMap<>();
    }

    public PekError(PekErrorCode error, String cause) {
        this(error);
        addCause(cause);
    }

    public PekError(PekException pex) {
        this(pex.getErrorCode());
        addCause(pex.getMessage());
        // TODO: later this should be extended to retrieve context specific information as well (github/#41)
    }

    public static PekError unspecified() {
        return new PekError(PekErrorCode.UNSPECIFIED);
    }

    public static PekError unspecified(String cause) {
        PekError pe = new PekError(PekErrorCode.UNSPECIFIED);
        pe.addCause(cause);
        return pe;
    }

    public static PekError notFound(String cause) {
        PekError pe = new PekError(PekErrorCode.RESOURCE_NOT_FOUND, cause);
        return pe;
    }

    @JsonUnwrapped
    @JsonSerialize(using = PekErrorCodeSerializer.class)
    public PekErrorCode getError() {
        return error;
    }

    public Map<String, Object> getExtra() {
        return Collections.unmodifiableMap(extra);
    }

    public void addCause(Object value) {
        addExtra(CAUSE_KEY, value);
    }

    public void addExtra(String key, Object value) {
        extra.put(key, value);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
