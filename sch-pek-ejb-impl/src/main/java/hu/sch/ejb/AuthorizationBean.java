package hu.sch.ejb;

import hu.sch.domain.Membership;
import hu.sch.domain.user.User;
import hu.sch.services.Authorization;
import hu.sch.services.Role;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class AuthorizationBean implements Authorization {

    // TODO: remove magic values: github/#86
    private static final String SVIE_ADMIN = "adminisztrátor";
    private static final Long SVIE_ID = 369L;

    private static final String PEK_ADMIN = "PéK admin";
    private static final Long KIRDEV_ID = 106L;

    private static final Long JETI_ID = 156L;

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean hasRole(Long userId, Role role) {
        switch(role) {
            case ADMIN:
                return hasSpecificPostInGroup(userId, KIRDEV_ID, PEK_ADMIN);
            case SVIE:
                return hasSpecificPostInGroup(userId, SVIE_ID, SVIE_ADMIN);
            case JETI:
                return hasJetiRole(userId);
            default:
                throw new AssertionError(role.name());
        }
    }

    @Override
    public boolean hasRole(User user, Role role) {
        return hasRole(user.getId(), role);
    }

    private boolean hasSpecificPostInGroup(Long userId, Long groupId, String post) {
        Long count = em.createNamedQuery(Membership.countPosts, Long.class)
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .setParameter("post", post)
                .getSingleResult();

        return count > 0;
    }

    private boolean hasJetiRole(Long userId) {
        TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(ms) FROM Membership ms WHERE ms.userId = :userId AND ms.groupId = :groupId AND ms.end IS NULL",
                Long.class);

        q.setParameter("userId", userId);
        q.setParameter("groupId", JETI_ID);
        return q.getSingleResult() > 0;
    }

}
