package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class GroupLeadership {

    private final EntityManager em;


    public GroupLeadership(EntityManager em) {
        this.em = em;
    }

    public boolean isLeader(Group group, Long userId) {
        TypedQuery<Long> q = em.createNamedQuery(Membership.countGroupLeaderInGroup, Long.class);
        q.setParameter("group", group);
        q.setParameter("userId", userId);

        return q.getSingleResult() > 0;
    }

    public boolean hasAny(Long userId) {
        TypedQuery<Long> q = em.createNamedQuery(Membership.countGroupLeaderships, Long.class);
        q.setParameter("userId", userId);

        return q.getSingleResult() > 0;
    }


}
