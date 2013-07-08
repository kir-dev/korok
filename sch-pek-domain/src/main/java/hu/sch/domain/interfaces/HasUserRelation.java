package hu.sch.domain.interfaces;

import hu.sch.domain.user.User;

/**
 * Azokat az entitások implementálják ezt, amelyeknek van kapcsolatuk felhasználóval,
 * illetve szeretnénk haszálni a {@link hu.sch.domain.util.MapUtils#createMapWithUserIdKey(java.util.List)}
 * metódust.
 *
 * @author  messo
 * @since   2.4
 */
public interface HasUserRelation {

    User getUser();

    void setUser(User u);

    Long getUserId();

    void setUserId(Long userId);
}
