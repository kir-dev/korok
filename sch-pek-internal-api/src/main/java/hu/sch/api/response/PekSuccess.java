package hu.sch.api.response;

/**
 * Basic successful response object.
 *
 * @author tomi
 */
public class PekSuccess implements PekResponse {

    private final Object data;

    public PekSuccess(Object data) {
        this.data = data;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    public Object getData() {
        return data;
    }
}
