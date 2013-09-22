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
    @NamedQuery(name = "getUserDelegatedPost",
    query = "SELECT p FROM Post p WHERE p.postType.delegatedPost = true "
    + "AND p.membership.group = :group "
    + "AND p.membership.user = :user"),
    @NamedQuery(name = "getByTypeAndGroup", query = "SELECT p FROM Post p "
    + "WHERE p.postType = :pt AND p.membership.group = :group"),
    @NamedQuery(name = "getPostTypeByName", query = "SELECT p FROM PostType p "
    + "WHERE p.postName = :pn")
})
@SequenceGenerator(name = "poszt_seq", sequenceName = "poszt_seq", allocationSize = 1)
public class Post implements Serializable {

    private static final long serialVersionUID = 1l;
    public static final String currentPostsForGroup = "currentPostsForGroup";
    public static final String getUserDelegatedPost = "getUserDelegatedPost";
    public static final String getByTypeAndGroup = "getByTypeAndGroup";
    public static final String getPostTypeByName = "getPostTypeByName";
    //----------------------------------------------------
    @Id
    @GeneratedValue(generator = "poszt_seq")
    @Column(name = "id")
    private Long id;
    //----------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "grp_member_id", insertable = true, updatable = true)
    private Membership membership;
    //----------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "pttip_id", insertable = true, updatable = true)
    private PostType postType;

    public Post() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }
}
