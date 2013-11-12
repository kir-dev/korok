package hu.sch.services;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.user.User;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface SvieManagerLocal {

    void updateSvieInfos(List<User> users);

    /**
     * Frissíti a paraméterben megkapott körök SVIE-s tulajdonságait, valamint
     * beállítja, hogy a SVIE-s kör körvezetője is SVIE-s legyen.
     * @param groups
     */
    void updateSvieGroupInfos(List<Group> groups);

    List<Membership> getActiveSvieMembershipsForUser(User user);

    List<User> getSvieMembers();

    void updatePrimaryMembership(User user);

    void advocateToOrdinal(User user);

    void ordinalToAdvocate(User user);

    void endMembership(User user);

    void applyToSvie(User user, SvieMembershipType msType);

    /**
     * Visszaadja a küldöttnek jelölt körtagokat, illetve a svie körben lévő körvezetőket is
     * @return A küldött gyűlésen résztvevő tagok listája
     */
    List<User> getDelegatedUsers();
}
