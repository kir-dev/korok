package hu.sch.ejb.search;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.services.SearchManagerLocal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author tomi
 */
@Stateless
public class SearchManagerBean implements SearchManagerLocal {

    @PersistenceContext
    private EntityManager em;

    public SearchManagerBean() {
    }

    /**
     * For testing purposes.
     *
     * @param em
     */
    public SearchManagerBean(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<User> searchUsers(String keyword, int page, int perPage) {
        TypedQuery<User> q = new SearchQueryBuilder(em, keyword).build();
        q.setFirstResult(perPage * page);
        q.setMaxResults(perPage);

        return q.getResultList();
    }

    @Override
    public List<User> searchBirthdayUsers(Date date) {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u "
                + "WHERE MONTH(u.dateOfBirth) = :month AND DAY(u.dateOfBirth) = :day", User.class);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        q.setParameter("month", c.get(Calendar.MONTH) + 1);
        q.setParameter("day", c.get(Calendar.DAY_OF_MONTH));

        return q.getResultList();
    }

    @Override
    public List<Group> searchGroups(String term, int page, int perPage) {
        TypedQuery<Group> q = em.createNamedQuery(Group.findByNameFragment, Group.class);
        q.setParameter("groupName", createLikeExpression(term));
        // pagination
        q.setMaxResults(perPage);
        q.setFirstResult(page * perPage);
        return q.getResultList();
    }

    @Override
    public long countUsers(String term) {
        return new SearchQueryBuilder(em, term).buildForCount().getSingleResult();
    }

    @Override
    public long countGroup(String term) {
        TypedQuery<Long> q = em.createNamedQuery(Group.countByNameFragment, Long.class);
        q.setParameter("groupName", createLikeExpression(term));
        return q.getSingleResult();
    }

    private String createLikeExpression(String expr) {
        return "%".concat(expr).concat("%");
    }
}
