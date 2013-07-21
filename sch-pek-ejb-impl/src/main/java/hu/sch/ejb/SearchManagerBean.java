package hu.sch.ejb;

import hu.sch.domain.user.User;
import hu.sch.services.SearchManagerLocal;
import hu.sch.services.exceptions.NotImplementedException;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author tomi
 */
@Stateless
public class SearchManagerBean implements SearchManagerLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> searchUsers(String keyword) {
        throw new NotImplementedException();
    }

    @Override
    public List<User> searchBirthdayUsers(Date date) {
        throw new NotImplementedException();
    }
}
