/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author aldaris
 */
@Entity
@Table(name = "poszt")
@NamedQueries({
    @NamedQuery(name = "currentPostsForGroup",
    query = "SELECT p FROM Post p WHERE p.membership.id = :id"),
    @NamedQuery(name = "findGroupLeader",
    query = "SELECT p.membership.user FROM Post p " +
    "WHERE p.postType.postName = 'körvezető' AND p.membership.group.id = :id")
})
@SequenceGenerator(name = "poszt_seq", sequenceName = "poszt_seq")
public class Post implements Serializable {

    private static final long serialVersionUID = 1l;
    public static final String currentPostsForGroup = "currentPostsForGroup";
    public static final String getGroupLeaderForGroup = "findGroupLeader";
    /*
    id            | integer | not null default nextval('poszt_seq'::regclass)
    grp_member_id | integer |
    pttip_id      | integer |
     */
    private Long id;
    private Membership membership;
    private PostType postType;

    public Post() {
    }

    @Id
    @GeneratedValue(generator = "poszt_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "grp_member_id", insertable = true, updatable = true)
    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    @ManyToOne
    @JoinColumn(name = "pttip_id", insertable = true, updatable = true)
    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }
}