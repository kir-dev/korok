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
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class SystemManagerBean implements SystemManagerLocal {

    @PersistenceContext
    EntityManager em;
    private static final String LAST_LOG = "utolso_log_kuldve";

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

    public Date getLastLogsDate() {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(getAttributeValue(LAST_LOG));
        } catch (Exception ex) {
            date = null;
        }
        return date;
    }

    public void setLastLogsDate() {
        setAttributeValue(LAST_LOG, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
}
