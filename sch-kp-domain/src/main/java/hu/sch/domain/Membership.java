/*
 * Member.java
 *
 * Created on April 23, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Csoporttagságot reprezentáló entity
 * @author hege
 */
@Entity
@Table(name = "grp_members")
public class Membership implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String find = "findMembership";
    private MembershipPK id;
    private User user;
    private Group group;
    /**
     * A csoporttagság idejének kezdete - kötelező
     */
    private Date start;
    /**
     * A csoporttagság vége - nem kötelező
     */
    private Date end;
    /**
     * Jogok tárolására bitmaszk
     */
    private Long rights;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grp_id", insertable = false, updatable = false)
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", insertable = false, updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "userId", column = @Column(name = "usr_id")),
        @AttributeOverride(name = "groupId", column = @Column(name = "grp_id"))
    })
    public MembershipPK getId() {
        return id;
    }

    public void setId(MembershipPK id) {
        this.id = id;
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

    @Column(name = "member_rights", nullable = false, columnDefinition = "INTEGER")
    public Long getRights() {
        return rights;
    }

    @Transient
    public MembershipType[] getRightsAsString() {
        if (end != null) {
            MembershipType[] ret = new MembershipType[1];
            ret[0] = MembershipType.OREGTAG;
            return ret;
        }
        return MembershipType.getMembershipTypeFromRights(rights);
    }

    public void setRights(Long rights) {
        this.rights = rights;
    }
}
