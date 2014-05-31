package hu.sch.ejb;

import hu.sch.domain.enums.ValuationPeriod;
import hu.sch.domain.SystemAttribute;
import hu.sch.domain.Semester;
import hu.sch.services.config.Configuration;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hege
 */
@Stateless
public class SystemManagerBean implements SystemManagerLocal {

    private static final Logger logger = LoggerFactory.getLogger(SystemManagerBean.class);
    //
    @Inject
    private Configuration config;
    private String showUserLink;
    private String baseLink;
    private String valuationLink;
    private String considerLink;

    //
    @PersistenceContext
    EntityManager em;
    @Inject
    MailManagerBean mailManager;

    @PostConstruct
    void init() {
        showUserLink = "https://" + config.getProfileDomain() + "/profile/show/virid/";
        baseLink = "https://" + config.getKorokDomain() + "/korok/";
        valuationLink = baseLink + "valuation";
        considerLink = baseLink + "consider";
    }

    @Override
    public String getAttributeValue(String attributeName) throws NoSuchAttributeException {
        try {
            SystemAttribute attr = getAttribute(attributeName);

            return attr.getAttributeValue();
        } catch (NoResultException nre) {
            throw new NoSuchAttributeException(attributeName);
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
        final String lastLog = getAttributeValue(SystemAttribute.LAST_LOG);
        return Long.parseLong(lastLog);
    }

    @Override
    public void setLastLogId(long id) {
        setAttributeValue(SystemAttribute.LAST_LOG, String.valueOf(id));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendExceptionReportMail(final Map<SystemManagerLocal.EXC_REPORT_KEYS, String> params) {
        final String subject =
                MailManagerBean.getMailString(MailManagerBean.MAIL_SYSTEM_EXCEPTIONREPORT_SUBJECT);

        final String body =
                MailManagerBean.getMailString(MailManagerBean.MAIL_SYSTEM_EXCEPTIONREPORT_BODY);

        final Object[] args = new Object[]{
            params.get(SystemManagerLocal.EXC_REPORT_KEYS.PAGE_NAME),
            params.get(SystemManagerLocal.EXC_REPORT_KEYS.PAGE_PATH),
            params.get(SystemManagerLocal.EXC_REPORT_KEYS.PAGE_PARAMS),
            params.get(SystemManagerLocal.EXC_REPORT_KEYS.REMOTE_USER),
            params.get(SystemManagerLocal.EXC_REPORT_KEYS.REMOTE_ADDRESS),
            params.get(SystemManagerLocal.EXC_REPORT_KEYS.EMAIL),
            params.get(SystemManagerLocal.EXC_REPORT_KEYS.VIRID),
            params.get(SystemManagerLocal.EXC_REPORT_KEYS.EXCEPTION)
        };

        mailManager.sendEmail("jee-dev@sch.bme.hu", subject, String.format(body, args));
    }

    @Override
    public String getShowUserLink() {
        return showUserLink;
    }

    @Override
    public String getBaseLink() {
        return baseLink;
    }

    @Override
    public String getValuationLink() {
        return valuationLink;
    }

    @Override
    public String getConsiderLink() {
        return considerLink;
    }

}
