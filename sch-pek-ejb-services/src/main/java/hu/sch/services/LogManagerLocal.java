package hu.sch.services;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.domain.logging.EventType;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface LogManagerLocal {

    void createLogEntry(Group group, User user, EventType event);
}
