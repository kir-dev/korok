/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.services;

import hu.sch.domain.Membership;
import hu.sch.domain.User;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface SvieManagerLocal {

    void updateSvieInfos(List<User> users);

    List<Membership> getSvieMembershipsForUser(User user);

    List<User> getSvieMembers();

    void updatePrimaryMembership(User user);

    void advocateToOrdinal(User user);

    void OrdinalToAdvocate(User user);

    void endMembership(User user);
}
