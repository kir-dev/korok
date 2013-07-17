/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.ejb.test.util;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author tomi
 */
public final class Queries {

    private Queries() { }

    public static Long count(EntityManager em, Class<?> klass) {
        Query q = em.createQuery("SELECT COUNT(o) FROM " + klass.getName() + " o");
        return (Long)q.getSingleResult();
    }

    public static <T> T first(EntityManager em, Class<T> klass) {
        TypedQuery<T> q = em.createQuery("SELECT o FROM " + klass.getName() + " o", klass)
                .setFirstResult(0)
                .setMaxResults(1);
        return q.getSingleResult();
    }



}
