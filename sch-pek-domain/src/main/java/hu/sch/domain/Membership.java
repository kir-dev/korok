package hu.sch.domain;

import hu.sch.domain.user.User;
import hu.sch.domain.util.DateInterval;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Csoporttagságot reprezentáló entity
 *
 * @author hege
 */
@Entity
@Table(name = "grp_membership")
@NamedQueries(value = {
    @NamedQuery(name = Membership.getActiveSvieMemberships,
            query = "SELECT ms FROM Membership ms WHERE ms.user = :user AND ms.group.isSvie = true AND ms.end IS null"),
    @NamedQuery(name = Membership.getMembersWithSvieMembershipTypeNotEqual,
            query = "SELECT u FROM User u WHERE u.svieMembershipType <> :msType"),
    @NamedQuery(name = Membership.getDelegatedMemberForGroup,
            query = "SELECT ms.user FROM Membership ms WHERE ms.group.id=:groupId AND ms.user.sviePrimaryMembership = ms AND ms.user.delegated = true"),
    @NamedQuery(name = Membership.getAllDelegated,
            query = "SELECT u FROM User u WHERE u.delegated = true ORDER BY u.lastName, u.firstName"),
    @NamedQuery(name = Membership.findMembershipsForGroup, query =
            "SELECT ms FROM Membership ms WHERE ms.groupId = :id"),
    @NamedQuery(name = Membership.findMembershipForUserAndGroup, query =
            "SELECT ms FROM Membership ms WHERE ms.groupId = :groupId AND ms.userId = :userId")
})
@SequenceGenerator(name = "grp_members_seq", sequenceName = "grp_members_seq", allocationSize = 1)
public class Membership implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String getActiveSvieMemberships = "getActiveSvieMemberships";
    public static final String getMembersWithSvieMembershipTypeNotEqual = "getMembersWithSvieMembershipTypeNotEqual";
    public static final String getDelegatedMemberForGroup = "getDelegatedMemberForGroup";
    public static final String getAllDelegated = "getAllDelegated";
    public static final String findMembershipsForGroup = "findMembershipsForGroup";
    public static final String findMembershipForUserAndGroup = "getMembershipForUserAndGroup";
    public static final String INACTIVE_MEMBERSHIP_POST = "öregtag";
    public static final String ACTIVE_MEMBERSHIP_POST = "tag";
    @Id
    @GeneratedValue(generator = "grp_members_seq")
    @Column(name = "id")
    private Long id;
    //----------------------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "grp_id", insertable = true, updatable = true)
    private Group group;
    //----------------------------------------------------
    @Column(name = "grp_id", insertable = false, updatable = false)
    private Long groupId;
    //----------------------------------------------------
    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", insertable = true, updatable = true)
    private User user;
    //----------------------------------------------------
    @Column(name = "usr_id", insertable = false, updatable = false)
    private Long userId;
    //----------------------------------------------------
    @Column(name = "membership_start", nullable = false, columnDefinition = "date")
    @Temporal(TemporalType.DATE)
    private Date start;
    //----------------------------------------------------
    @Column(name = "membership_end", nullable = true, columnDefinition = "date")
    @Temporal(TemporalType.DATE)
    private Date end;
    //----------------------------------------------------
    @Transient
    private DateInterval interval;
    //----------------------------------------------------
    @OneToMany(mappedBy = "membership", fetch = FetchType.EAGER)
    private Set<Post> posts = new HashSet<>();

    /**
     * Egy csoporttagság egyedi azonosítója
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Melyik csoport tagja
     */
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * Ki a tagja a csoportnak
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * A csoporttagság idejének kezdete - kötelező
     */
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * A csoporttagság vége - nem kötelező
     */
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public DateInterval getInterval() {
        if (interval == null) {
            interval = new DateInterval(start, end);
        }
        return interval;
    }

    /**
     * A csoportban betöltött posztok
     */
    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Membership other = (Membership) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 29 * hash + (this.group != null ? this.group.hashCode() : 0);
        hash = 29 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 29 * hash + (this.start != null ? this.start.hashCode() : 0);
        hash = 29 * hash + (this.end != null ? this.end.hashCode() : 0);
        hash = 29 * hash + (this.posts != null ? this.posts.hashCode() : 0);
        return hash;
    }

    /**
     * Gets all the posts that a user have, including 'öregtag' and 'tag' posts.
     *
     * @return a list of posts
     */
    public List<String> getAllPosts() {
        List<String> postList = new ArrayList<>();
        for (Post post : getPosts()) {
            postList.add(post.getPostType().getPostName());
        }

        if (!isActive()) {
            postList.add(INACTIVE_MEMBERSHIP_POST);
        }

        if (postList.isEmpty()) {
            postList.add(ACTIVE_MEMBERSHIP_POST);
        }

        return Collections.unmodifiableList(postList);
    }

    public boolean isActive() {
        return getEnd() == null;
    }
}
