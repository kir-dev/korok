package hu.sch.api.response;

import hu.sch.api.serializers.PekErrorCodeSerializer;
import hu.sch.util.exceptions.PekErrorCode;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author tomi
 */
public class PekError extends AbstractPekResponse {

    private final PekErrorCode error;

    public PekError(PekErrorCode error) {
        this.error = error;
    }

    @JsonSerialize(using = PekErrorCodeSerializer.class)
    public PekErrorCode getError() {
        return error;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
