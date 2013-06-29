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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
@Stateless(mappedName = "PostManager")
@SuppressWarnings("unchecked")
public class PostManagerBean implements PostManagerLocal {

    @PersistenceContext
    private EntityManager em;
    private static Logger log = LoggerFactory.getLogger(PostManagerBean.class);

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
        log.info("törlendő:");
        for (Post post : removable) {
            log.info(post.getPostType().toString());
            Post temp = em.find(Post.class, post.getId());
            // mi van, ha időközben már töröltük? Akkor ne akarjuk mégegyszer!
            if(temp != null)
                em.remove(temp);
        }
        log.info("létrehozandók");
        for (PostType postType : creatable) {
            log.info(postType.toString());
            Post temp = new Post();
            temp.setMembership(ms);
            temp.setPostType(postType);
            em.persist(temp);
        }
        em.flush();
    }

    public void changeGroupLeader(Membership membership, PostType groupLeaderType) {
        User user = membership.getUser();
        Query q = em.createNamedQuery(Post.getByTypeAndGroup);
        q.setParameter("pt", groupLeaderType);
        q.setParameter("group", membership.getGroup());
        Post post = (Post) q.getSingleResult();
        Query q2 = em.createNamedQuery(Post.getPostTypeByName);
        q2.setParameter("pn", "volt körvezető");
        PostType postType = (PostType) q2.getSingleResult();
        post.setPostType(postType);
        List<PostType> temp = new ArrayList<PostType>(1);
        temp.add(groupLeaderType);
        log.warn("Körvezetőt szeretnének váltani: " + post.getMembership().getUser().getId() + "->" + membership.getUser().getId());
        if (membership.getGroup().getIsSvie()) {
            if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)
                    && user.getSvieStatus().equals(SvieStatus.ELFOGADVA)
                    && user.getSvieMembershipType() != null && user.getSviePrimaryMembership().equals(membership)) {
                post.getMembership().getUser().setDelegated(false);
                membership.getUser().setDelegated(true);
            } else {
                throw new IllegalStateException("Ennek a felhasználónak nem adhatod át a körvezetőséget, "
                        + "nem teljesíti a SVIE feltételeket.");
            }
        }
        em.merge(membership.getUser());
        em.flush();
        setPostsForMembership(membership, new ArrayList<Post>(0), temp);
    }

    public boolean createPostType(String postName, Group group, Boolean isDelegatedPost) {
        try {
            Query q = em.createNamedQuery(PostType.getByNameAndGroup);
            q.setParameter("pn", postName);
            q.setParameter("group", group);
            q.getSingleResult();
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
            List result = q.getResultList();
            if (!result.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (NoResultException nre) {
            return false;
        }
    }
}
