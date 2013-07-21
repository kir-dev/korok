package hu.sch.services;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import java.util.List;
import javax.ejb.Local;

/**
 * Group management.
 *
 * @author tomi
 */
@Local
public interface GroupManagerLocal {

    /**
     * Gets all of the groups in flat list.
     */
    List<Group> getAllGroups();

    /**
     * Gets all the groups in a flat list.
     *
     * @param includeMemberCount if true it includes the primary SVIE member count for each group.
     * @return
     */
    List<Group> getAllGroups(boolean includeMemberCount);

    /**
     * Gets all of the groups in a hierarchical tree.
     * @return
     */
    List<Group> getGroupTree();

    /**
     * Gets a group with the given id.
     *
     * Does not preloads the relationships!
     *
     * @param id the id to look for.
     * @return the group or null if the it was not found
     */
    Group findGroupById(Long id);

    /**
     * Gets a group with the given id.
     *
     * @param id the id to look for.
     * @param fetchMemberships if true, eager loads the memberships.
     * @return the group or null if the it was not found
     */
    Group findGroupById(Long id, boolean fetchMemberships);

    /**
     * Gets the group which name matches exactly the given name.
     *
     * @param name name of the group to lookup.
     * @return the group or null if the it was not found
     */
    Group findGroupByName(String name);

    /**
     * Gets all groups which name contains the given fragment.
     *
     * @param nameFragment the name fragment to look for.
     * @return list of groups or an empty list if no group matches the criteria
     */
    List<Group> findGroupsByName(String nameFragment);

    /**
     * Gets the users for a group with a specific post.
     *
     * @param groupId
     * @param post
     */
    List<User> findMembersByGroupAndPost(Long groupId, String post);

    /**
     * Gets all the users for a group whose primary SVIE membership
     * is the given group.
     *
     * @param groupId
     * @return
     */
    List<User> findMembersWithPrimaryMembership(Long groupId);

    /**
     * Find active members for the give group.
     *
     * @param groupId
     * @return
     */
    public List<User> findActiveMembers(long groupId);

    /**
     * Updates the group.
     *
     * @param group group to update
     */
    void updateGroup(Group group);

    /**
     * Gets the leader for a group.
     *
     * @param groupId
     * @return the user or null if the group does not exist.
     */
    User findLeaderForGroup(Long groupId);

    /**
     * Create a new group and makes the user the leader of the group.
     *
     * @param group the group to create
     * @param leader the initial leader of the group
     */
    void createGroup(Group group, User leader);

    /**
     * Gets the sub groups for the group identified by the given id.
     *
     * @param groupId
     * @return
     */
    List<Group> getSubGroups(Long groupId);
}
