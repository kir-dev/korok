/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.SystemAttribute;
import hu.sch.domain.Semester;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import javax.annotation.security.DeclareRoles;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author hege
 */
@Stateless
@DeclareRoles({"ADMIN", "JETI"})
public class SystemManagerBean implements SystemManagerLocal {

    @PersistenceContext
    EntityManager em;

    public String getAttributeValue(String attributeName) throws NoSuchAttributeException {
        try {
            SystemAttribute attr = getAttribute(attributeName);

            return attr.getAttributeValue();
        } catch (NoResultException nre) {
            throw new NoSuchAttributeException();
        }
    }

    private SystemAttribute getAttribute(String attributeName) {
        Query q = em.createNamedQuery(SystemAttribute.findByAttributeName);
        q.setParameter("attributeName", attributeName);

        return (SystemAttribute) q.getSingleResult();
    }

    public void setAttributeValue(String attributeName, String attributeValue) {
        SystemAttribute attr;
        try {
            attr = getAttribute(attributeName);
        } catch (Exception e) {
            attr = new SystemAttribute();
            attr.setAttributeName(attributeName);
        }

        attr.setAttributeValue(attributeValue);

        em.merge(attr);
    }

    public Semester getSzemeszter() {
        String s;
        Semester szemeszter = new Semester();
        s = getAttributeValue("szemeszter");
        szemeszter.setId(s);

        return szemeszter;
    }

    public void setSzemeszter(Semester szemeszter) {
        setAttributeValue("szemeszter", szemeszter.getId());
    }

    public ValuationPeriod getErtekelesIdoszak() {
        try {
            return ValuationPeriod.valueOf(getAttributeValue("ertekeles_idoszak"));
        } catch (Exception any) {
            return ValuationPeriod.NINCSERTEKELES;
        }
    }

    public void setErtekelesIdoszak(ValuationPeriod idoszak) {
        setAttributeValue("ertekeles_idoszak", idoszak.toString());
    }
}
