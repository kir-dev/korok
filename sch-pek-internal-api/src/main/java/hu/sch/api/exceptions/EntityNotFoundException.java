package hu.sch.api.exceptions;

import hu.sch.api.response.PekError;
import hu.sch.util.exceptions.PekErrorCode;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author tomi
 */
public class EntityNotFoundException extends PekWebException {

    private static final String MESSAGE  = " cannot be found";

    public EntityNotFoundException(Class<?> clazz) {
        super(new PekError(PekErrorCode.ENTITY_NOTFOUND, makeMessage(clazz)), 404);
    }

    private static String makeMessage(Class<?> clazz) {
        return clazz.getSimpleName().concat(MESSAGE);
    }
}
