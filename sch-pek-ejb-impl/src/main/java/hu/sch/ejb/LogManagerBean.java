package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.logging.Event;
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

    public void createLogEntry(Group group, User user, Event event) {
        Log log = new Log();
        log.setGroup(group);
        log.setUser(user);
        log.setEvent(event);
        log.setEventDate(new Date());
        em.persist(log);
    }
}
