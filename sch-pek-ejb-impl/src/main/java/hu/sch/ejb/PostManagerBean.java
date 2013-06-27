/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
