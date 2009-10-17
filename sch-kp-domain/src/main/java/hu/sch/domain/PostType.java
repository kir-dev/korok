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
@Table(name = "poszttipus")
@NamedQueries({
    @NamedQuery(name = "availablePostsForGroup",
    query = "SELECT p FROM PostType p WHERE p.group IS NULL OR p.group.id = :id"),
    @NamedQuery(name = "searchForCertainPostType",
    query = "SELECT p FROM PostType p WHERE p.postName = :pn")
})
@SequenceGenerator(name = "poszttipus_seq", sequenceName = "poszttipus_seq")
public class PostType implements Serializable {

    private static final long serialVersionUID = 1l;
    public static final String availablePostsQuery = "availablePostsForGroup";
    public static final String searchForPostType = "searchForCertainPostType";
    public static final String KORVEZETO = "körvezető";
    /*
    pttip_id   | integer       | not null default nextval('poszttipus_seq'::regclass)
    grp_id     | integer       |
    pttip_name | character(30) | not null
     */
    Long id;
    Group group;
    String postName;
    Boolean delegatedPost;

    @Id
    @GeneratedValue(generator = "poszttipus_seq")
    @Column(name = "pttip_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "grp_id", insertable = true, updatable = true)
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Column(name = "pttip_name", length = 30)
    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    @Column(name = "delegated_post")
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PostType other = (PostType) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
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
