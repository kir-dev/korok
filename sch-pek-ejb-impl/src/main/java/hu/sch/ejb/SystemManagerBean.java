/**
 * Copyright (c) 2008-2010, Peter Major
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
import hu.sch.domain.logging.Log;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.log4j.Logger;

/**
 *
 * @author hege
 */
@Stateless
public class SystemManagerBean implements SystemManagerLocal {

    @PersistenceContext
    EntityManager em;
    private static final Logger logger = Logger.getLogger(SystemManagerBean.class);

    @Override
    public String getAttributeValue(String attributeName) throws NoSuchAttributeException {
        try {
            SystemAttribute attr = getAttribute(attributeName);

            return attr.getAttributeValue();
        } catch (NoResultException nre) {
            throw new NoSuchAttributeException();
        }
    }

    private SystemAttribute getAttribute(String attributeName) {
        TypedQuery<SystemAttribute> q = em.createNamedQuery(SystemAttribute.findByAttributeName, SystemAttribute.class);
        q.setParameter("attributeName", attributeName);

        return q.getSingleResult();
    }

    @Override
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

    @Override
    public Semester getSzemeszter() {
        String s;
        Semester szemeszter = new Semester();
        s = getAttributeValue(SystemAttribute.SEMESTER);
        szemeszter.setId(s);

        return szemeszter;
    }

    @Override
    public void setSzemeszter(Semester szemeszter) {
        setAttributeValue(SystemAttribute.SEMESTER, szemeszter.getId());
    }

    @Override
    public ValuationPeriod getErtekelesIdoszak() {
        try {
            return ValuationPeriod.valueOf(getAttributeValue(SystemAttribute.VALUATION_PERIOD));
        } catch (Exception any) {
            return ValuationPeriod.NINCSERTEKELES;
        }
    }

    @Override
    public void setErtekelesIdoszak(ValuationPeriod idoszak) {
        setAttributeValue(SystemAttribute.VALUATION_PERIOD, idoszak.toString());
    }

    @Override
    public long getLastLogId() {
        long id;
        final String lastLog = getAttributeValue(SystemAttribute.LAST_LOG);
        try {
            id = Long.parseLong(lastLog);
        } catch (NumberFormatException ex) {
            // TODO - későbbi releasenél (> 2.4) ezt el lehet távolítani, ha biztos,
            // hogy már átállt a DB-ben a dátum mező ID-re.
            // nem szám van ott lehet, hogy egy évszám?
            try {
                logger.info("A DB-ben a(z) " + SystemAttribute.LAST_LOG + " rendszerattribútum még dátumot tartalmaz logID helyett.");
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(lastLog);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DAY_OF_YEAR, -1); // vonjunk ki belőle egyet
                // lekérdezzük az adott napon a legfrissebb ID-t.
                Query q = em.createNamedQuery(Log.getLastLogIdByDate);
                q.setParameter("date", c.getTime());
                q.setMaxResults(1);
                // ezzel az előző napi utolsó logID-t kapjuk meg, ami pont jó,
                // mert akkor feldolgozásra igazából az ezutáni kerül
                id = (Long) q.getSingleResult();
            } catch (ParseException ex1) {
                // se nem szám, se nem évszám, akkor itt gubanc van.
                throw new RuntimeException("A(z) " + SystemAttribute.LAST_LOG + " rendszerattribútum se nem szám, se nem dátum, hogy lehetett ez?");
            }
        }
        return id;
    }

    @Override
    public void setLastLogId(long id) {
        setAttributeValue(SystemAttribute.LAST_LOG, String.valueOf(id));
    }
}
