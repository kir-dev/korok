package hu.sch.ejb;

import hu.sch.domain.user.User;
import hu.sch.services.AuthSchUserIntegration;
import hu.sch.services.config.Configuration;
import hu.sch.services.dto.OAuthUserInfo;
import java.util.Date;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class AuthSchUserIntegrationBean implements AuthSchUserIntegration {

    private static final Logger logger = LoggerFactory.getLogger(AuthSchUserIntegrationBean.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private MailManagerBean mailManager;

    @Inject
    private Configuration config;

    @Override
    @Asynchronous
    public void updateUser(Long userId, OAuthUserInfo userInfo) {
        User user = em.find(User.class, userId);

        user.setAuthSchId(userInfo.getAuthSchInternalId());
        user.setLastLoginDate(new Date());
        user.setBmeId(userInfo.getBmeId());
        updateRoomNumber(user, userInfo);

        try {
            // force flush, so we can catch the exception.
            em.flush();
        } catch (PersistenceException ex) {
            logger.warn(String.format("Could not update user (%d) with data from auth.sch.", userId), ex);
            sendErrorReport(userId, ex);
        }
    }

    private void updateRoomNumber(User user, OAuthUserInfo userInfo) {
        if (userInfo.getDormitory() != null) {
            user.setRoom(userInfo.getDormitory().getRoom());
            user.setDormitory(userInfo.getDormitory().getBuildingName());
        }
    }

    private void sendErrorReport(Long userId, PersistenceException ex) {
        String subject = MailManagerBean.getMailString(MailManagerBean.MAIL_AUTHSCH_USER_UPDATE_SUBJECT);
        String body = String.format(
            MailManagerBean.getMailString(MailManagerBean.MAIL_AUTHSCH_USER_UPDATE_BODY),
            userId,
            ex.getMessage(),
            ExceptionUtils.getStackTrace(ex)
        );

        mailManager.sendEmail(config.getErrorReportingEmail(), subject, body);
    }

}
