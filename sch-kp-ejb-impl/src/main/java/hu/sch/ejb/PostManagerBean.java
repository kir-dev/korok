/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import hu.sch.services.PostManagerLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author aldaris
 */
@Stateless(mappedName = "PostManager")
public class PostManagerBean implements PostManagerLocal {

    @PersistenceContext
    private EntityManager em;

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
        Query q = em.createQuery("SELECT p FROM Post p " +
                "WHERE p.postType = :pt AND p.membership.group = :group");
        q.setParameter("pt", groupLeaderType);
        q.setParameter("group", membership.getGroup());
        Post post = (Post) q.getSingleResult();
        Query q2 = em.createQuery("SELECT p FROM PostType p " +
                "WHERE p.postName = :pn");
        q2.setParameter("pn", "volt körvezető");
        PostType postType = (PostType) q2.getSingleResult();
        post.setPostType(postType);

        List<PostType> temp = new ArrayList<PostType>(1);
        temp.add(groupLeaderType);

        setPostsForMembership(membership, new ArrayList<Post>(0), temp);
    }

    public boolean createPostType(String postName, Group group) {
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
            em.persist(temp);
            return true;
        }
        return false;
    }
}
