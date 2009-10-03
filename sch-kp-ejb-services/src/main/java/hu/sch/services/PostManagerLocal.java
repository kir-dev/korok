/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.services;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import hu.sch.domain.User;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface PostManagerLocal {

    List<PostType> getAvailablePostTypesForGroup(Group group);

    List<Post> getCurrentPostsForGroup(Membership ms);

    void setPostsForMembership(Membership ms, List<Post> removable, List<PostType> creatable);

    void changeGroupLeader(Membership membership, PostType groupLeaderType);

    boolean createPostType(String postName, Group group, Boolean isDelegatedPost);

    boolean hasUserDelegatedPostInGroup(Group group, User user);
}
