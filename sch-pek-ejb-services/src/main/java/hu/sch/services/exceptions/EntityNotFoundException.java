
package hu.sch.services.exceptions;

import hu.sch.util.exceptions.PekErrorCode;
import hu.sch.util.exceptions.PekException;

/**
 *
 * @author Tamas Michelberger
 */
public class EntityNotFoundException extends PekException{

    private final Class<?> entityClass;

    public EntityNotFoundException(Class<?> entityClass, Object id) {
        super(PekErrorCode.ENTITY_NOT_FOUND,
                String.format("%s was not found for id: %s.", entityClass.getSimpleName(), id));

        this.entityClass = entityClass;
    }
}
