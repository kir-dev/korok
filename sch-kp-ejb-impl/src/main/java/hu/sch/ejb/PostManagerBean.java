/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import hu.sch.services.PostManagerLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author aldaris
 */
@Stateless(mappedName = "PostManager")
@SuppressWarnings("unchecked")
public class PostManagerBean implements PostManagerLocal {

    @PersistenceContext
    private EntityManager em;
    private static Logger log = Logger.getLogger(PostManagerBean.class);

    public List<PostType> getAvailablePostTypesForGroup(Group group) {
        Query q = em.createNamedQuery(PostType.availablePostsQuery);
        q.setParameter("id", group.getId());
        return q.getResultList();
    }

    public List<Post> getCurrentPostsForGroup(Membership ms) {
        Query q = em.createNamedQuery(Post.currentPostsForGroup);
        q.setParameter("id", ms.getId());
        return q.getResultList();
    }

    public void setPostsForMembership(Membership ms, List<Post> removable, List<PostType> creatable) {
        System.out.println("törlendő:");
        for (Post post : removable) {
            System.out.println(post.getPostType().toString());
            Post temp = em.find(Post.class, post.getId());
            em.remove(temp);
        }
        System.out.println("létrehozandók");
        for (PostType postType : creatable) {
            System.out.println(postType.toString());
            Post temp = new Post();
            temp.setMembership(ms);
            temp.setPostType(postType);
            em.persist(temp);
        }
        em.flush();
    }

    public void changeGroupLeader(Membership membership, PostType groupLeaderType) {
        User user = membership.getUser();
        if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG) &&
                user.getSvieStatus().equals(SvieStatus.ELFOGADVA) &&
                user.getSviePrimaryMembership().equals(membership)) {
            Query q = em.createQuery("SELECT p FROM Post p " +
                    "WHERE p.postType = :pt AND p.membership.group = :group");
            q.setParameter("pt", groupLeaderType);
            q.setParameter("group", membership.getGroup());
            Post post = (Post) q.getSingleResult();
            post.getMembership().getUser().setDelegated(false);
            Query q2 = em.createQuery("SELECT p FROM PostType p " +
                    "WHERE p.postName = :pn");
            q2.setParameter("pn", "volt körvezető");
            PostType postType = (PostType) q2.getSingleResult();
            post.setPostType(postType);

            List<PostType> temp = new ArrayList<PostType>(1);
            temp.add(groupLeaderType);
            membership.getUser().setDelegated(true);
            em.merge(membership.getUser());
            em.flush();
            setPostsForMembership(membership, new ArrayList<Post>(0), temp);
        } else {
            throw new IllegalStateException("Ennek a felhasználónak nem adhatod át a körvezetőséget, " +
                    "nem teljesíti a SVIE feltételeket.");
        }
    }

    public boolean createPostType(String postName, Group group, Boolean isDelegatedPost) {
        try {
            Query q = em.createQuery("SELECT p FROM PostType p " +
                    "WHERE (p.postName = :pn AND p.group IS NULL) OR " +
                    "p.postName = :pn AND p.group = :group");
            q.setParameter("pn", postName);
            q.setParameter("group", group);
            PostType result = (PostType) q.getSingleResult();
        } catch (NoResultException nre) {
            PostType temp = new PostType();
            temp.setGroup(group);
            temp.setPostName(postName);
            temp.setDelegatedPost(isDelegatedPost);
            em.persist(temp);
            return true;
        }
        return false;
    }

    public boolean hasUserDelegatedPostInGroup(Group group, User user) {
        try {
            Query q = em.createNamedQuery(Post.getUserDelegatedPost);
            q.setParameter("group", group);
            q.setParameter("user", user);
            q.getResultList();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }
}
