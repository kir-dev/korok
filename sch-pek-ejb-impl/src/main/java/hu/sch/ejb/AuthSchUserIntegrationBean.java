package hu.sch.ejb;

import hu.sch.domain.user.User;
import hu.sch.services.AuthSchUserIntegration;
import hu.sch.services.dto.OAuthUserInfo;
import java.util.Date;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class AuthSchUserIntegrationBean implements AuthSchUserIntegration {

    private static final Logger logger = LoggerFactory.getLogger(AuthSchUserIntegrationBean.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Asynchronous
    public void updateUser(Long userId, OAuthUserInfo userInfo) {
        User user = em.find(User.class, userId);

        user.setAuthSchId(userInfo.getAuthSchInternalId());
        user.setLastLoginDate(new Date());
        user.setBmeId(userInfo.getBmeId());
        updateRoomNumber(user, userInfo);
    }

    private void updateRoomNumber(User user, OAuthUserInfo userInfo) {
        if (userInfo.getDormitory() != null) {
            user.setRoom(userInfo.getDormitory().getRoom());
            user.setDormitory(userInfo.getDormitory().getBuildingName());
        }
    }

}
