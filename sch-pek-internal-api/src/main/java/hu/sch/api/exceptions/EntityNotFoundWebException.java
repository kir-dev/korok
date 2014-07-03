package hu.sch.api.exceptions;

import hu.sch.api.response.PekError;
import hu.sch.services.exceptions.EntityNotFoundException;
import hu.sch.services.exceptions.PekErrorCode;

/**
 *
 * @author tomi
 */
public class EntityNotFoundWebException extends PekWebException {

    private static final String MESSAGE  = " cannot be found";

    public EntityNotFoundWebException(Class<?> clazz) {
        super(new PekError(PekErrorCode.ENTITY_NOT_FOUND, makeMessage(clazz)), 404);
    }

    public EntityNotFoundWebException(EntityNotFoundException ex) {
        super(new PekError(ex.getErrorCode(), ex.getMessage()), 404);
    }

    private static String makeMessage(Class<?> clazz) {
        return clazz.getSimpleName().concat(MESSAGE);
    }
}
