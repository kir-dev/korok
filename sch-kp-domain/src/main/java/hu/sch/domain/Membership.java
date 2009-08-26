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
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Csoporttagságot reprezentáló entity
 * @author hege
 */
@Entity
@Table(name = "grp_membership")
@SequenceGenerator(name = "grp_members_seq", sequenceName = "grp_members_seq")
public class Membership implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String find = "findMembership";
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
    /**
     * Ki a tagja a csoportnak
     */
    private User user;
    /**
     * A csoporttagság idejének kezdete - kötelező
     */
    private Date start;
    /**
     * A csoporttagság vége - nem kötelező
     */
    private Date end;
    /**
     * A csoportban betöltött posztok
     */
    private List<Post> posts;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "usr_id", insertable = true, updatable = true)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @OneToMany(mappedBy = "membership", fetch = FetchType.EAGER)
    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
