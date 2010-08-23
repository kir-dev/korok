/**
 * Copyright (c) 2009-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

    @Override
    public boolean getNewbieTime() {
        boolean ret;
        try {
            ret = Boolean.parseBoolean(getAttributeValue(SystemAttribute.NEWBIE_TIME));
        } catch (NoSuchAttributeException nsae) {
            ret = false;
        }

        return ret;
    }

    @Override
    public void setNewbieTime(boolean newbieTime) {
        setAttributeValue(SystemAttribute.NEWBIE_TIME, Boolean.toString(newbieTime));
    }
}
