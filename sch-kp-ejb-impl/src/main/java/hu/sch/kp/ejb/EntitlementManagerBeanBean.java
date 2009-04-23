/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.ejb;

import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.EntitlementManagerBeanLocal;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author aldaris
 */
@Stateless
public class EntitlementManagerBeanBean implements EntitlementManagerBeanLocal {

    @PersistenceContext
    EntityManager em;

    public Felhasznalo createUserEntry(Felhasznalo user) {
        throw new UnsupportedOperationException();
    }
}
