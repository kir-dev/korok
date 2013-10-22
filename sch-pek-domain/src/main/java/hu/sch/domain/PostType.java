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
@Table(name = "poszttipus")
@NamedQueries({
    @NamedQuery(name = PostType.availablePostsQuery,
    query = "SELECT p FROM PostType p WHERE p.group IS NULL OR p.group.id = :id"),
    @NamedQuery(name = PostType.searchForPostType,
    query = "SELECT p FROM PostType p WHERE p.postName = :pn"),
    @NamedQuery(name = PostType.getByNameAndGroup,
    query = "SELECT p FROM PostType p "
    + "WHERE (p.postName = :pn AND p.group IS NULL) OR "
    + "p.postName = :pn AND p.group = :group")
})
@SequenceGenerator(name = "poszttipus_seq", sequenceName = "poszttipus_seq", allocationSize = 1)
public class PostType implements Serializable {

    private static final long serialVersionUID = 1l;
    public static final String availablePostsQuery = "availablePostsForGroup";
    public static final String searchForPostType = "searchForCertainPostType";
    public static final String getByNameAndGroup = "getByNameAndGroup";
    public static final String KORVEZETO = "körvezető";
    //----------------------------------------------------
    @Id
    @GeneratedValue(generator = "poszttipus_seq")
    @Column(name = "pttip_id")
    Long id;
    //----------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "grp_id", insertable = true, updatable = true)
    Group group;
    //----------------------------------------------------
    @Column(name = "pttip_name", length = 30)
    String postName;
    //----------------------------------------------------
    @Column(name = "delegated_post")
    Boolean delegatedPost;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public Boolean getDelegatedPost() {
        return delegatedPost;
    }

    public void setDelegatedPost(Boolean delegatedPost) {
        this.delegatedPost = delegatedPost;
    }

    @Override
    public String toString() {
        return postName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final PostType other = (PostType) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
