/**
 * Copyright (c) 2009-2010, Peter Major
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

import hu.sch.domain.logging.Log;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "groups")
@NamedQueries({
    @NamedQuery(name = "findAllGroup", query = "SELECT g FROM Group g "
    + "WHERE g.status='akt' ORDER BY g.name"),
    @NamedQuery(name = "groupHierarchy", query =
    "SELECT g FROM Group g LEFT JOIN FETCH g.parent "
    + "WHERE g.status='akt' ORDER BY g.name"),
    @NamedQuery(name = "findGroupWithMemberships", query = "SELECT g FROM "
    + "Group g LEFT JOIN FETCH g.memberships WHERE g.id = :id")
})
@SequenceGenerator(name = "groups_seq", sequenceName = "groups_grp_id_seq")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Group implements Serializable, Comparable<Group> {

    private static final long serialVersionUID = 1L;
    public static final String findAll = "findAllGroup";
    public static final String findWithMemberships = "findGroupWithMemberships";
    public static final String groupHierarchy = "groupHierarchy";

    /*
    grp_id               | integer               | not null default nextval('groups_grp_id_seq'::regclass)
    grp_name             | text                  | not null
    grp_type             | character varying(20) | not null
    grp_parent           | integer               |
    grp_state            | character(3)          | default 'akt'::bpchar
    grp_description      | text                  |
    grp_webpage          | character varying(64) |
    grp_maillist         | character varying(64) |
    grp_head             | character varying(48) |
    grp_founded          | integer               |
    grp_issvie           | boolean               | not null default false
    grp_svie_delegate_nr | integer               |
     */
    /**
     * Group azonosító id
     */
    private Long id;
    /**
     * Group neve
     */
    private String name;
    /**
     * Típus
     */
    private String type;
    /**
     * Szülő csoport
     */
    private Group parent;
    /**
     * Státusz (aktiv / öreg)
     */
    private GroupStatus status;
    /**
     * Kör bemutatkozása
     */
    private String introduction;
    /**
     * Publikus weboldal címe
     */
    private String webPage;
    /**
     * Levelezési lista címe
     */
    private String mailingList;
    /**
     * A kör vezetőjének egyéni titulusa
     */
    @XmlTransient
    private String head;
    /**
     * Alapítás éve
     */
    @XmlTransient
    private Integer founded;
    /**
     * Az adott kör tagja-e a SVIE-nek
     */
    private Boolean isSvie;
    /**
     * Az adott kör hány tagot küldhet küldött gyülésre
     */
    @XmlTransient
    private Integer delegateNumber;
    /**
     * Alcsoportok
     */
    @XmlTransient
    private List<Group> subGroups;
    /**
     * Az elsődleges körtagok száma
     */
    @XmlTransient
    private Long numberOfPrimaryMembers;
    /**
     * Csoporttagságok
     */
    @XmlTransient
    private List<Membership> memberships;
    /**
     * Cache-elt mező
     */
    @XmlTransient
    private List<User> members;
    /**
     * Aktív tagságok
     */
    @XmlTransient
    private List<Membership> activeMembers;
    /**
     * Öregtagok
     */
    @XmlTransient
    private List<Membership> inactiveMembers;
    @XmlTransient
    private List<Log> logs;

    public Group() {
    }

    public Group(Group group, Long pMs) {
        id = group.getId();
        numberOfPrimaryMembers = pMs;
        name = group.getName();
        delegateNumber = group.getDelegateNumber();
        isSvie = group.getIsSvie();
    }

    @Id
    @GeneratedValue(generator = "groups_seq")
    @Column(name = "grp_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "grp_name", length = 255, columnDefinition = "text")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "grp_type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "grp_parent")
    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }

    @Transient
    public User getGroupLeader() {
        for (Membership ms : activeMembers) {
            for (Post post : ms.getPosts()) {
                if (post.getPostType().getPostName().equals(PostType.KORVEZETO)) {
                    return ms.getUser();
                }
            }
        }
        System.out.println("Unable to find GroupLeader for group: " + getId());
        return null;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "grp_state")
    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    @Column(name = "grp_description", columnDefinition = "text")
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Column(name = "grp_webpage", length = 64)
    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }

    @Column(name = "grp_maillist", length = 64)
    public String getMailingList() {
        return mailingList;
    }

    public void setMailingList(String mailingList) {
        this.mailingList = mailingList;
    }

    @Column(name = "grp_head", length = 48)
    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    @Column(name = "grp_founded")
    public Integer getFounded() {
        return founded;
    }

    public void setFounded(Integer founded) {
        this.founded = founded;
    }

    @Column(name = "grp_issvie")
    public Boolean getIsSvie() {
        return isSvie;
    }

    public void setIsSvie(Boolean isSvie) {
        this.isSvie = isSvie;
    }

    @Column(name = "grp_svie_delegate_nr")
    public Integer getDelegateNumber() {
        return delegateNumber;
    }

    public void setDelegateNumber(Integer delegateNumber) {
        this.delegateNumber = delegateNumber;
    }

    @Transient
    public Long getNumberOfPrimaryMembers() {
        return numberOfPrimaryMembers;
    }

    public void setNumberOfPrimaryMembers(Long numberOfPrimaryMembers) {
        this.numberOfPrimaryMembers = numberOfPrimaryMembers;
    }

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }

    @Transient
    public List<Group> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<Group> subGroups) {
        this.subGroups = subGroups;
    }

    @Transient
    public List<User> getMembers() {
        if (members == null) {
            loadMembers();
        }
        return members;
    }

    private void loadMembers() {
        sortMemberships();
        members = new ArrayList<User>();
        activeMembers = new ArrayList<Membership>();
        inactiveMembers = new ArrayList<Membership>();
        for (Membership cst : getMemberships()) {
            members.add(cst.getUser());
            if (cst.getEnd() == null) {
                activeMembers.add(cst);
            } else {
                inactiveMembers.add(cst);
            }
        }
    }

    @Transient
    public List<Membership> getActiveMemberships() {
        if (members == null) {
            loadMembers();
        }
        return activeMembers;
    }

    @Transient
    public List<Membership> getInactiveMemberships() {
        if (members == null) {
            loadMembers();
        }
        return inactiveMembers;
    }

    public void sortMemberships() {
        if (getMemberships() != null) {
            Collections.sort(getMemberships(),
                    new Comparator<Membership>() {

                        public int compare(Membership o1, Membership o2) {
                            if (o1.getEnd() == null ^ o2.getEnd() == null) {
                                return o1.getEnd() == null ? -1 : 1;
                            }
                            return o1.getUser().compareTo(o2.getUser());
                        }
                    });
        }
    }

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Group o) {
        Collator huCollator = Collator.getInstance(new Locale("hu"));
        return huCollator.compare(getName(), o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Group other = (Group) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 31 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 31 * hash + (this.webPage != null ? this.webPage.hashCode() : 0);
        hash = 31 * hash + (this.introduction != null ? this.introduction.hashCode() : 0);
        hash = 31 * hash + (this.mailingList != null ? this.mailingList.hashCode() : 0);
        hash = 31 * hash + (this.founded != null ? this.founded.hashCode() : 0);
        hash = 31 * hash + (this.status != null ? this.status.hashCode() : 0);
        return hash;
    }
}
