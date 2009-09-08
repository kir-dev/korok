/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.services;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.SvieMembershipType;
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

    void updateSvieGroupInfos(List<Group> groups);

    List<Membership> getSvieMembershipsForUser(User user);

    List<User> getSvieMembers();

    void updatePrimaryMembership(User user);

    void advocateToOrdinal(User user);

    void OrdinalToAdvocate(User user);

    void endMembership(User user);

    void applyToSvie(User user, SvieMembershipType msType);

    /**
     * Visszaadja a küldöttnek jelölt körtagokat, illetve a svie körben lévő körvezetőket is
     *
     * Mivel a Hibernate nem támogatja a UNION-t, ezért a két lekérdezést külön
     * kell végrehajtani.
     * @return A küldött gyűlésen résztvevő tagok listája
     */
    List<User> getDelegatedUsers();
}
