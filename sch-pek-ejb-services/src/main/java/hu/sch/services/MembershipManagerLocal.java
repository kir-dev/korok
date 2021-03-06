package hu.sch.services;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.user.User;
import hu.sch.services.exceptions.MembershipAlreadyExistsException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 * Memebership management.
 *
 * @author tomi
 */
@Local
public interface MembershipManagerLocal {

    /**
     * Creates a new membership: basically a user joins a group as a member.
     *
     * @param group the group the user wants to join to.
     * @param user the user which wants to join.
     * @param membershipStart start of the membership
     * @param membershipEnd end of the membership
     * @param isAuthorized true and the user will get an automatic "tag" post in
     * the group.
     */
    void joinGroup(Group group, User user, Date membershipStart, Date membershipEnd, boolean isAuthorized)
            throws MembershipAlreadyExistsException;

    /**
     * Deletes a membership.
     *
     * @param membership the membership to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    void deleteMembership(Membership membership);

    /**
     * Gets a membership for the given user-group pair.
     *
     * @param groupId
     * @param userId
     * @return the membership or null if there is no match
     */
    Membership findMembership(Long groupId, Long userId);

    /**
     * Get a membership with the specified id.
     *
     * @param id the id of the membership
     * @return the memerbship object or null if the record was not found
     */
    Membership findMembership(Long id);

    /**
     * Activates the membership.
     *
     * Turns an "öregtag" into an active member.
     *
     * @param membership
     */
    void activateMembership(Membership membership);

    /**
     * Inactivates the membership.
     *
     * Turns an active member into an "öregtag".
     *
     * @param membership
     */
    void inactivateMembership(Membership membership);

    /**
     * Fetches and sets the memberships for the given group.
     *
     * @param group the group which memberships collection will be populated.
     * @return the same group with the memberships included
     */
    public Group fetchMembershipsFor(Group group);

    /**
     * Gets the active memberships for user.
     *
     * It includes groups and posts as well.
     *
     * NOTE: probably it is a temporary solution.
     *
     * @param user
     * @return
     */
    List<Membership> findMembershipsForUser(User user);

    /**
     * Gets all the memberships (including inactives as well) for a user.
     *
     * It includes groups and posts as well.
     *
     * @param user
     * @return
     */
    List<Membership> findAllMembershipForUser(User user);

    /**
     * Determines whether a user is a group leader in a group or not.
     *
     * @param userId
     * @param group
     * @return
     */
    boolean isGroupLeader(Long userId, Group group);

    /**
     * Determins wheater a user has any group leaderships or not.
     *
     * @param userId
     * @return
     */
    boolean hasGroupLeadership(Long userId);
}
