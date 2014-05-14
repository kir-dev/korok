package hu.sch.api.response;

/**
 * Basic successful response object.
 *
 * @author tomi
 */
public class PekResponse<T> extends AbstractPekResponse {

    private final T data;

    public PekResponse(T data) {
        this.data = data;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    public T getData() {
        return data;
    }
}
