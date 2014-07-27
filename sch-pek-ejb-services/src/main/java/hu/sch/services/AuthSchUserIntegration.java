package hu.sch.services;

import hu.sch.services.dto.OAuthUserInfo;
import javax.ejb.Local;

@Local
public interface AuthSchUserIntegration {

    /**
     * Update the user with information from auth.sch.
     *
     * @param userId
     * @param userInfo
     */
    void updateUser(Long userId, OAuthUserInfo userInfo);
}
