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

package hu.sch.domain;

import hu.sch.domain.util.DateInterval;
import hu.sch.domain.interfaces.MembershipTableEntry;
import java.util.Date;
import java.util.List;
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
 * @author hege
 */
@Entity
@Table(name = "grp_membership")
@NamedQueries(value = {
    @NamedQuery(name = "getMembership",
    query = "SELECT ms FROM Membership ms WHERE ms.user = :user AND ms.group.isSvie = true"),
    @NamedQuery(name = "getMembers",
    query = "SELECT u FROM User u WHERE u.svieMembershipType <> :msType"),
    @NamedQuery(name = "getDelegatedMemberForGroup",
    query = "SELECT ms.user FROM Membership ms "
    + "WHERE ms.group.id=:groupId AND ms.user.sviePrimaryMembership = ms AND ms.user.delegated = true"),
    @NamedQuery(name = "getAllDelegated",
    query = "SELECT u FROM User u WHERE u.delegated = true "
    + "ORDER BY u.lastName, u.firstName"),
    @NamedQuery(name = Membership.findMembershipsForGroup, query =
    "SELECT ms FROM Membership ms "
    + "WHERE ms.groupId = :id"),
    @NamedQuery(name = Membership.getMembershipForUserAndGroup, query =
    "SELECT ms FROM Membership ms WHERE ms.groupId = :groupId AND ms.userId = :userId")
})
@SequenceGenerator(name = "grp_members_seq", sequenceName = "grp_members_seq")
public class Membership implements MembershipTableEntry {

    public static final String SORT_BY_GROUP = "group";
    public static final String SORT_BY_POSTS = "postsAsString";
    public static final String SORT_BY_INTERVAL = "interval";
    private static final long serialVersionUID = 1L;
    public static final String getMembership = "getMembership";
    public static final String getMembers = "getMembers";
    public static final String getDelegatedMemberForGroup = "getDelegatedMemberForGroup";
    public static final String getAllDelegated = "getAllDelegated";
    public static final String findMembershipsForGroup = "findMembershipsForGroup";
    public static final String getMembershipForUserAndGroup = "getMembershipForUserAndGroup";

    /*
    id               | integer | not null default nextval('grp_members_seq'::regclass)
    grp_id           | integer |
    usr_id           | integer |
    membership_start | date    | default now()
    membership_end   | date    |
     */
    /**
     * Egy csoporttagság egyéni azonosítója
     */
    private Long id;
    /**
     * Melyik csoport tagja
     */
    private Group group;
    private Long groupId;
    /**
     * Ki a tagja a csoportnak
     */
    private User user;
    private Long userId;
    /**
     * A csoporttagság idejének kezdete - kötelező
     */
    private Date start;
    /**
     * A csoporttagság vége - nem kötelező
     */
    private Date end;
    private DateInterval interval;
    /**
     * A csoportban betöltött posztok
     */
    private List<Post> posts;
    private String postsAsString;

    @Id
    @GeneratedValue(generator = "grp_members_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "grp_id", insertable = true, updatable = true)
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Column(name = "grp_id", insertable = false, updatable = false)
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "usr_id", insertable = false, updatable = false)
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "membership_start", nullable = false, columnDefinition = "date")
    @Temporal(TemporalType.DATE)
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    @Column(name = "membership_end", nullable = true, columnDefinition = "date")
    @Temporal(TemporalType.DATE)
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Transient
    public DateInterval getInterval() {
        if (interval == null) {
            interval = new DateInterval(start, end);
        }
        return interval;
    }

    @OneToMany(mappedBy = "membership", fetch = FetchType.EAGER)
    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
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
     * Ez csak azért kell, hogy általánosítani lehessen a MembershipTable logikáját.
     */
    @Transient
    @Override
    public Membership getMembership() {
        return this;
    }

    @Transient
    public String getPostsAsString() {
        if (postsAsString == null) {
            StringBuilder sb = new StringBuilder(posts.size() * 16 + 3);
            if (end != null) {
                sb.append("öregtag");
            }

            for (Post post : posts) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(post.getPostType().toString());
            }

            if (sb.length() == 0) {
                sb.append("tag");
            }
            postsAsString = sb.toString();
        }

        return postsAsString;
    }
}