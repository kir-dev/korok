package hu.sch.services;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.logging.Event;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface LogManagerLocal {

    void createLogEntry(Group group, User user, Event event);
}
