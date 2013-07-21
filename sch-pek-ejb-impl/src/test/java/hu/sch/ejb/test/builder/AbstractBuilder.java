package hu.sch.ejb.test.builder;

import javax.persistence.EntityManager;

/**
 *
 * @author tomi
 */
public abstract class AbstractBuilder<T> {

    /**
     * Builds the domain class.
     * @return
     */
    public abstract T build();

    /**
     * Builds and saves the entity to the database.
     *
     * @return the managed entity
     */
    public T create(EntityManager em) {
        T entity = build();
        em.persist(entity);
        return entity;
    }

}
