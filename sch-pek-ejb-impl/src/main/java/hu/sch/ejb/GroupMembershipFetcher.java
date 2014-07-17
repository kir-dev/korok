package hu.sch.ejb;

import hu.sch.domain.Membership;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class GroupMembershipFetcher {

    private final EntityManager em;
    private final Long groupId;

    public GroupMembershipFetcher(EntityManager em, Long groupId) {
        this.em = em;
        this.groupId = groupId;
    }

    public List<Membership> findActive() {
        return buildQuery(true).getResultList();
    }

    public List<Membership> findInactive() {
        return buildQuery(false).getResultList();
    }

    private TypedQuery<Membership> buildQuery(boolean isActive) {
        TypedQuery<Membership> q = em.createQuery(buildQueryString(isActive), Membership.class);
        q.setParameter("groupId", groupId);
        return q;
    }

    private String buildQueryString(boolean isActive) {
        StringBuilder builder = new StringBuilder("SELECT ms FROM Membership ms ")
                .append("LEFT JOIN FETCH ms.posts post ")
                .append("LEFT JOIN FETCH post.postType ")
                .append("JOIN FETCH ms.user ")
                .append("WHERE ms.groupId = :groupId ")
                .append("AND ms.end ");

        if (isActive) {
            builder.append("IS NULL ");
        } else {
            builder.append("IS NOT NULL ");
        }

        builder.append("ORDER BY ms.user.lastName, ms.user.firstName");
        return builder.toString();
    }
}
