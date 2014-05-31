package hu.sch.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author tomi
 */
public abstract class AbstractEntityView<T> implements EntityView {

    private final Class<T> entityClass;
    protected final T entity;

    public AbstractEntityView(T entity, Class<T> clazz) {
        this.entityClass = clazz;
        this.entity = entity;
    }

    @Override
    public String getEntityName() {
        return entityClass.getSimpleName();
    }

    @Override
    public boolean hasEntity() {
        return entity != null;
    }

    @JsonIgnore
    public T getEntity() {
        return entity;
    }

}
