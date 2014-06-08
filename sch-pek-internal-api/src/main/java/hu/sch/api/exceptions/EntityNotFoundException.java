package hu.sch.api.exceptions;

import hu.sch.api.response.PekError;
import hu.sch.util.exceptions.PekErrorCode;

/**
 *
 * @author tomi
 */
public class EntityNotFoundException extends PekWebException {

    private static final String MESSAGE  = " cannot be found";

    public EntityNotFoundException(Class<?> clazz) {
        super(new PekError(PekErrorCode.ENTITY_NOT_FOUND, makeMessage(clazz)), 404);
    }

    private static String makeMessage(Class<?> clazz) {
        return clazz.getSimpleName().concat(MESSAGE);
    }
}
