package hu.sch.api.response;

/**
 * Basic successful response object.
 *
 * @author tomi
 */
public class PekSuccess<T> implements PekResponse {

    private final T data;

    public PekSuccess(T data) {
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
