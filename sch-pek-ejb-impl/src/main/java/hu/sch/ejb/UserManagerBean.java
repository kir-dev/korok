/**
 * Copyright (c) 2008-2010, Peter Major All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Peter Major nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission. * All advertising
 * materials mentioning features or use of this software must display the
 * following acknowledgement: This product includes software developed by the
 * Kir-Dev Team, Hungary and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Peter Major BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.ejb;

import hu.sch.domain.*;
import hu.sch.domain.logging.Event;
import hu.sch.domain.logging.EventType;
import hu.sch.domain.profile.Person;
import hu.sch.services.*;
import hu.sch.services.exceptions.MembershipAlreadyExistsException;
import hu.sch.services.exceptions.PersonNotFoundException;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author hege
 * @author messo
 */
@Stateless
@SuppressWarnings("unchecked")
public class UserManagerBean implements UserManagerLocal {

    @PersistenceContext
    EntityManager em;
    @EJB(name = "LogManagerBean")
    LogManagerLocal logManager;
    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;
    @EJB(name = "PostManagerBean")
    PostManagerLocal postManager;
    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;
    private static Logger log = Logger.getLogger(UserManagerBean.class);
    private static Event DELETEMEMBERSHIP_EVENT;
    private static Event CREATEMEMBERSHIP_EVENT;

    @PostConstruct
    protected void initialize() {
        if (DELETEMEMBERSHIP_EVENT == null) {
            Query q = em.createNamedQuery(Event.getEventForEventType);
            q.setParameter("evt", EventType.TAGSAGTORLES);
            DELETEMEMBERSHIP_EVENT = (Event) q.getSingleResult();
            q.setParameter("evt", EventType.JELENTKEZES);
            CREATEMEMBERSHIP_EVENT = (Event) q.getSingleResult();
        }
    }

    @Override
    public void updateUserAttributes(User user) {
        User f = em.find(User.class, user.getId());
        boolean changed = false;

        if (user.getEmailAddress() != null && !user.getEmailAddress().equals(f.getEmailAddress())) {
            f.setEmailAddress(user.getEmailAddress());
            changed = true;
        }
        if (user.getNickName() != null && !user.getNickName().equals(f.getNickName())) {
            f.setNickName(user.getNickName());
            changed = true;
        }
        if (user.getNeptunCode() != null && !user.getNeptunCode().equals(f.getNeptunCode())) {
            f.setNeptunCode(user.getNeptunCode());
            changed = true;
        }
        if (user.getLastName() != null && !user.getLastName().equals(f.getLastName())) {
            f.setLastName(user.getLastName());
            changed = true;
        }
        if (user.getFirstName() != null && !user.getFirstName().equals(f.getFirstName())) {
            f.setFirstName(user.getFirstName());
            changed = true;
        }

        if (changed) {
            em.merge(f);
            em.flush();
        }
    }

    @Override
    public User findUserById(Long userId) {
        try {
            return em.find(User.class, userId);
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addUserToGroup(User user, Group group, Date start, Date veg, boolean isAuthorized)
            throws MembershipAlreadyExistsException {
        Membership ms = new Membership();
        User _user = em.find(User.class, user.getId());
        Group _group = em.find(Group.class, group.getId());

        ms.setUser(_user);
        ms.setGroup(_group);
        ms.setStart(start);
        ms.setEnd(null);

        if (!isAuthorized) {
            Post post = new Post();
            post.setMembership(ms);

            Query q = em.createNamedQuery(PostType.searchForPostType);
            q.setParameter("pn", "feldolgozás alatt");
            PostType postType = (PostType) q.getSingleResult();

            post.setPostType(postType);
            List<Post> posts = new ArrayList<Post>();
            posts.add(post);
            ms.setPosts(posts);

            em.persist(post);
        }

        _user.getMemberships().add(ms);
        _group.getMemberships().add(ms);

        em.persist(ms);
        em.merge(_user);
        em.merge(_group);
        try {
            em.flush();
        } catch (PersistenceException ex) {
            if (ex.getCause().getClass().equals(ConstraintViolationException.class)) {
                // már van ilyen tagság
                throw new MembershipAlreadyExistsException(group, user);
            } else {
                // valami egyéb rondaság, dobjuk vissza :)
                throw ex;
            }
        }
        logManager.createLogEntry(group, user, CREATEMEMBERSHIP_EVENT);
    }

    @Override
    public void createNewGroupWithLeader(Group group, User user) {
        em.persist(group);
        Membership ms = new Membership();
        ms.setGroup(group);
        ms.setUser(user);
        ms.setStart(new Date());
        em.persist(ms);
        Post post = new Post();
        post.setMembership(ms);
        PostType leader = (PostType) em.createNamedQuery(PostType.searchForPostType).setParameter("pn", "körvezető").getSingleResult();
        post.setPostType(leader);
        em.persist(post);
    }

    @Override
    public List<Group> getAllGroups() {
        Query q = em.createNamedQuery(Group.findAll);

        return q.getResultList();
    }

    @Override
    public List<String> getEveryGroupName() {
        Query q =
                em.createQuery("SELECT g.name FROM Group g "
                + "ORDER BY g.name");

        return q.getResultList();
    }

    @Override
    public List<Group> findGroupByName(String name) {
        Query q =
                em.createQuery("SELECT g FROM Group g "
                + "WHERE UPPER(g.name) LIKE UPPER(:groupName) "
                + "ORDER BY g.name");
        q.setParameter("groupName", name);

        return q.getResultList();
    }

    @Override
    public Group getGroupByName(String name) {
        Query q = em.createQuery("SELECT g FROM Group g "
                + "WHERE g.name=:groupName");
        q.setParameter("groupName", name);
        try {
            Group csoport = (Group) q.getSingleResult();
            return csoport;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Group findGroupById(Long id) {
        return em.find(Group.class, id);
    }

    @Override
    public void deleteMembership(Membership ms) {
        Membership temp = em.find(Membership.class, ms.getId());
        User user = ms.getUser();
        boolean userChanged = false;

        em.remove(temp);
        em.flush();
        if (user.getSvieMembershipType().equals(SvieMembershipType.RENDESTAG)
                && user.getSvieStatus().equals(SvieStatus.ELFOGADVA)
                && ms.getGroup().getIsSvie()) {
            try {
                Query q = em.createQuery("SELECT ms.user FROM Membership ms "
                        + "WHERE ms.user = :user AND ms.group.isSvie = true");
                q.setParameter("user", user);
                q.getSingleResult();
            } catch (NoResultException nre) {
                user.setSvieMembershipType(SvieMembershipType.PARTOLOTAG);
                userChanged = true;
            }
        }
        if (user.getSviePrimaryMembership() != null && ms.getId().equals(user.getSviePrimaryMembership().getId())) {
            user.setSviePrimaryMembership(null);
            userChanged = true;
        }
        if (userChanged) {
            em.merge(user);
        }
        logManager.createLogEntry(ms.getGroup(), ms.getUser(), DELETEMEMBERSHIP_EVENT);
    }

    @Override
    public List<User> getCsoporttagokWithoutOregtagok(Long csoportId) {
        Query q =
                em.createQuery("SELECT ms.user FROM Membership ms JOIN "
                + "ms.user "
                + "WHERE ms.group.id=:groupId AND ms.end=NULL "
                + "ORDER BY ms.user.lastName ASC, ms.user.firstName ASC");

        q.setParameter("groupId", csoportId);

        return q.getResultList();
    }

    @Override
    public List<User> getUsersWithPrimaryMembership(Long groupId) {
        Query q = em.createQuery("SELECT ms.user FROM Membership ms "
                + "WHERE ms.group.id=:groupId AND ms.user.sviePrimaryMembership = ms "
                + "AND ms.user.svieStatus = :svieStatus "
                + "ORDER BY ms.user.lastName, ms.user.firstName");
        q.setParameter("groupId", groupId);
        q.setParameter("svieStatus", SvieStatus.ELFOGADVA);
        return q.getResultList();
    }

    @Override
    public List<User> getMembersForGroup(Long csoportId) {
        //Group cs = em.find(Group.class, csoportId);
        Query q =
                em.createQuery("SELECT ms.user FROM Membership ms JOIN "
                + "ms.user "
                + "WHERE ms.group.id=:groupId "
                + "ORDER BY ms.user.lastName ASC, ms.user.firstName ASC");

        q.setParameter("groupId", csoportId);

        return q.getResultList();
    }

    @Override
    public List<EntrantRequest> getBelepoIgenyekForUser(User felhasznalo) {
        Query q = em.createQuery("SELECT e FROM EntrantRequest e "
                + "WHERE e.user=:user "
                + "ORDER BY e.valuation.semester DESC, e.entrantType ASC");
        q.setParameter("user", felhasznalo);

        return q.getResultList();
    }

    @Override
    public List<PointRequest> getPontIgenyekForUser(User felhasznalo) {
        Query q = em.createQuery("SELECT p FROM PointRequest p "
                + "WHERE p.user=:user "
                + "ORDER BY p.valuation.semester DESC, p.valuation.group.name ASC");
        q.setParameter("user", felhasznalo);

        return q.getResultList();
    }

    @Override
    public List<Group> getGroupHierarchy() {
        Query q = em.createNamedQuery(Group.groupHierarchy);
        List<Group> csoportok = q.getResultList();
        List<Group> rootCsoportok = new ArrayList<Group>();

        for (Group cs : csoportok) {
            if (cs.getParent() != null) {
                if (cs.getParent().getSubGroups() == null) {
                    cs.getParent().setSubGroups(new ArrayList<Group>());
                }
                cs.getParent().getSubGroups().add(cs);
            } else {
                rootCsoportok.add(cs);
            }
        }

        return rootCsoportok;
    }

    @Override
    public User findUserWithMembershipsById(Long userId) {
        if (userId.equals(0L)) {
            // ha nincs használható userId, akkor ne menjünk el a DB-hez.
            return null;
        }

        TypedQuery<User> q = em.createNamedQuery(User.findWithMemberships, User.class);
        q.setParameter("id", userId);

        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            log.warn("Can't find user with memberships for this id: " + userId);
            return null;
        }
    }

    @Override
    public Group findGroupWithMembershipsById(Long id) {
        TypedQuery<Group> q = em.createNamedQuery(Group.findWithMemberships, Group.class);
        q.setParameter("id", id);

        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            log.warn("Can't find group with memberships", ex);
            return null;
        }
    }

    @Override
    public void loadMemberships(Group g) {
        TypedQuery<Membership> q = em.createNamedQuery(Membership.findMembershipsForGroup, Membership.class);
        q.setParameter("id", g.getId());

        try {
            g.setMemberships(q.getResultList());
        } catch (Exception ex) {
            log.warn("Can't find group with memberships", ex);
        }
    }

    @Override
    public void groupInfoUpdate(Group cs) {
        Group csoport = em.find(Group.class, cs.getId());
        csoport.setFounded(cs.getFounded());
        csoport.setName(cs.getName());
        csoport.setWebPage(cs.getWebPage());
        csoport.setIntroduction(cs.getIntroduction());
        csoport.setMailingList(cs.getMailingList());
        csoport.setUsersCanApply(cs.getUsersCanApply());
    }

    @Override
    public Membership getMembership(Long memberId) {
        return em.find(Membership.class, memberId);
    }

    @Override
    public Membership getMembership(final Long groupId, final Long userId) {
        Query q = em.createQuery("SELECT ms FROM Membership ms WHERE ms.user.id = :userId "
                + "AND ms.group.id = :groupId");
        q.setParameter("groupId", groupId);
        q.setParameter("userId", userId);

        try {
            return (Membership) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public void setMemberToOldBoy(Membership ms) {
        ms.setEnd(new Date());
        em.merge(ms);
    }

    @Override
    public void setUserDelegateStatus(User user, boolean isDelegated) {
        user.setDelegated(isDelegated);
        em.merge(user);
    }

    @Override
    public void setOldBoyToActive(Membership ms) {
        ms.setEnd(null);
        em.merge(ms);
    }

    @Override
    public void updateUser(User user) {
        em.merge(user);
    }

    @Override
    public void updateGroup(Group group) {
        em.merge(group);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getGroupLeaderForGroup(Long groupId) {
        Query q = em.createNamedQuery(Post.getGroupLeaderForGroup);
        q.setParameter("id", groupId);
        try {
            User ret = (User) q.getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            log.error("Nem találtam meg ennek a körnek a körvezetőjét: " + groupId);
            return null;
        }
    }

    @Override
    public List<Group> getAllGroupsWithCount() {
        Query q = em.createQuery("SELECT new hu.sch.domain.Group(g, "
                + "(SELECT COUNT(*) FROM Membership ms WHERE ms.user.sviePrimaryMembership = ms "
                + "AND ms.group.id = g.id AND ms.user.svieStatus = 'ELFOGADVA' "
                + "AND ms.user.svieMembershipType = 'RENDESTAG')) "
                + "FROM Group g WHERE g.status='akt'");
        return q.getResultList();
    }

    @Override
    public List<User> searchForUserByName(String name) {
        Query q = em.createQuery("SELECT u FROM User u WHERE UPPER(concat(concat(u.lastName, ' '), "
                + "u.firstName)) LIKE UPPER(:name) "
                + "ORDER BY u.lastName ASC, u.firstName ASC");
        q.setParameter("name", "%" + name + "%");

        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Semester> getAllValuatedSemesterForUser(User user) {
        return em.createNamedQuery(User.getAllValuatedSemesterForUser).setParameter("user", user).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSemesterPointForUser(User user, Semester semester) {
        // Beszerezzük a pontokat
        List<PointRequest> pontigenyek = getPontIgenyekForUser(user);

        // Ebbe a Map-be lesz tárolva, hogy melyik körtől hány pontot kapott
        Map<Group, Integer> points = new HashMap<Group, Integer>();
        for (PointRequest pr : pontigenyek) {
            Valuation v = pr.getValuation();
            // Csak ha az adott értékelés a legfrisebb verzió ÉS a pont elfogadott
            if (!v.isObsolete() && v.getPointStatus().equals(ValuationStatus.ELFOGADVA)) {
                // Csak akkor, ha a vizsgált vagy az előző félévre vonatkozik
                if (v.getSemester().equals(semester) || v.getSemester().equals(semester.getPrevious())) {
                    if (points.containsKey(v.getGroup()) == false) {
                        points.put(v.getGroup(), 0);
                    }
                    points.put(v.getGroup(), points.get(v.getGroup()) + pr.getPoint());
                }
            }
        }
        // Az összeg
        int sum = 0;
        // Négyzetösszeget számolunk
        for (Integer pointFromGroup : points.values()) {
            sum += pointFromGroup * pointFromGroup;
        }

        return (int) Math.min(Math.sqrt(sum), 100);
    }

    @Override
    public Group getParentGroups(Long id) {
        List<Group> groups = em.createNamedQuery(Group.groupHierarchy).getResultList();
        for (Group group : groups) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        throw new IllegalArgumentException("No such group");
    }

    /**
     * Tagja-e a körnek a felhasználó?
     *
     * @param group kör
     * @param user felhasználó
     * @return
     */
    private boolean isMember(Group group, User user) {
        Query q = em.createNamedQuery(Membership.getMembershipForUserAndGroup);
        q.setParameter("groupId", group.getId());
        q.setParameter("userId", user.getId());

        return !q.getResultList().isEmpty();
    }

    @Override
    public List<User> getMembersForGroupAndPost(Long groupId, String post) {
        Query q = em.createNamedQuery(User.findUsersForGroupAndPost);
        q.setParameter("groupId", groupId);
        q.setParameter("post", post);

        return q.getResultList();
    }

    @Override
    public List<Group> getChildGroups(Long id) {
        return em.createQuery("SELECT g FROM Group g WHERE g.parent.id =:id").setParameter("id", id).getResultList();

    }

    @Override
    public SpotImage getSpotImage(User user) {
        TypedQuery<SpotImage> q = em.createNamedQuery(SpotImage.findByNeptun, SpotImage.class);
        q.setParameter("neptunCode", user.getNeptunCode());
        try {
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public boolean acceptRecommendedPhoto(String userUid) {
        Person p;
        try {
            p = ldapManager.getPersonByUid(userUid);
        } catch (PersonNotFoundException ex) {
            return false;
        }

        TypedQuery<SpotImage> q = em.createNamedQuery(SpotImage.findByNeptun, SpotImage.class);
        q.setParameter("neptunCode", p.getNeptun());
        try {
            SpotImage si = q.getSingleResult();
            p.setPhoto(si.getImage());
            ldapManager.update(p);
            em.remove(si); // töröljük a fotót a DB-ből
            return true;
        } catch (NoResultException ex) {
            return false;
        }
    }

    @Override
    public void declineRecommendedPhoto(User user) {
        Query q = em.createNamedQuery(SpotImage.deleteByNeptun);
        q.setParameter("neptunCode", user.getNeptunCode());
        q.executeUpdate();
    }
}
