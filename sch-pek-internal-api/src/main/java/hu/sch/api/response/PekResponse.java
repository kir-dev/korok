package hu.sch.api.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Basic response object.
 *
 * @author tomi
 */
public class PekResponse<T> {

    private boolean success;
    private PekError error;
    private T response;

    protected PekResponse(boolean success) {
        this.success = success;
        this.error = null;
        this.response = null;
    }

    public PekResponse(PekError error) {
        this(false);
        this.error = error;
    }

    public PekResponse(T response) {
        this(true);
        this.response = response;
    }

    public boolean isSuccess() {
        return success;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public PekError getError() {
        return error;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public T getResponse() {
        return response;
    }
}
