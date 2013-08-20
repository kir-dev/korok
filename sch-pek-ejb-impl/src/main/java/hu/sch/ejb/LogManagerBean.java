package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.logging.EventType;
import hu.sch.domain.user.User;
import hu.sch.domain.logging.Log;
import hu.sch.services.LogManagerLocal;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author aldaris
 */
@Stateless
public class LogManagerBean implements LogManagerLocal {

    @PersistenceContext
    EntityManager em;

    @Override
    public void createLogEntry(final Group group, final User user, final EventType event) {
        Log log = new Log();
        log.setGroup(group);
        log.setUser(user);
        log.setEvent(event);
        log.setEventDate(new Date());
        em.persist(log);
    }
}
