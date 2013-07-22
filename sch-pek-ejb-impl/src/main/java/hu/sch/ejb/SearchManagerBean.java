package hu.sch.ejb;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.services.SearchManagerLocal;
import hu.sch.services.exceptions.NotImplementedException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.time.DateUtils;

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
    public List<User> searchUsers(String keyword) {
        throw new NotImplementedException();
    }

    @Override
    public List<User> searchBirthdayUsers(Date date) {
        Query q = em.createQuery("SELECT u FROM User u JOIN u.privateAttributes pa "
                + "WHERE MONTH(u.dateOfBirth) = :month AND DAY(u.dateOfBirth) = :day "
                + "AND pa.attrName = :attr AND pa.visible = true");

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        q.setParameter("month", c.get(Calendar.MONTH) + 1);
        q.setParameter("day", c.get(Calendar.DAY_OF_MONTH));
        q.setParameter("attr", UserAttributeName.DATE_OF_BIRTH);

        return q.getResultList();
    }
}
