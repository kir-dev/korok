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
    query = "SELECT p.membership.user FROM Post p "
    + "WHERE p.postType.postName = 'körvezető' AND p.membership.group.id = :id"),
    @NamedQuery(name = "getUserDelegatedPost",
    query = "SELECT p FROM Post p WHERE p.postType.delegatedPost = true "
    + "AND p.membership.group = :group "
    + "AND p.membership.user = :user"),
    @NamedQuery(name = "getByTypeAndGroup", query = "SELECT p FROM Post p "
    + "WHERE p.postType = :pt AND p.membership.group = :group"),
    @NamedQuery(name = "getPostTypeByName", query = "SELECT p FROM PostType p "
    + "WHERE p.postName = :pn")
})
@SequenceGenerator(name = "poszt_seq", sequenceName = "poszt_seq")
public class Post implements Serializable {

    private static final long serialVersionUID = 1l;
    public static final String currentPostsForGroup = "currentPostsForGroup";
    public static final String getGroupLeaderForGroup = "findGroupLeader";
    public static final String getUserDelegatedPost = "getUserDelegatedPost";
    public static final String getByTypeAndGroup = "getByTypeAndGroup";
    public static final String getPostTypeByName = "getPostTypeByName";
    
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