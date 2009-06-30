/*
 * Group.java
 *
 * Created on April 23, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package hu.sch.domain;

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

/**
 *
 * @author hege
 */
@Entity
@Table(name = "groups")
@NamedQueries({
    @NamedQuery(name = "findAllGroup", query = "SELECT g FROM Group g " +
    "WHERE g.status='akt' ORDER BY g.name"),
    @NamedQuery(name = "groupHierarchy", query =
    "SELECT g FROM Group g LEFT JOIN FETCH g.parent " +
    "WHERE g.status='akt' ORDER BY g.name"),
    @NamedQuery(name = "findGroupWithMemberships", query = "SELECT g FROM " +
    "Group g LEFT JOIN FETCH g.memberships WHERE g.id = :id")
})
@SequenceGenerator(name = "groups_seq", sequenceName = "groups_grp_id_seq")
public class Group implements Serializable, Comparable<Group> {

    private static final long serialVersionUID = 1L;
    public static final String findAll = "findAllGroup";
    public static final String findWithMemberships = "findGroupWithMemberships";
//    public static final String

    /*
    grp_id          | integer                | not null
    grp_name        | text                   | not null
    grp_type        | character varying(16)  | not null
    grp_parent      | integer                | 
    grp_state       | character(3)           | default 'akt'::bpchar
    grp_description | text                   | 
    grp_webpage     | character varying(64)  | 
    grp_maillist    | character varying(64)  | 
    grp_head        | character varying(48)  | 
    grp_founded     | integer                | 
    grp_flags       | integer                | 
    grp_acc_cards   | integer                | 
    grp_acc_points  | integer                | 
    is_del          | boolean                | default false
    grp_shortname   | character varying(128) | 
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
     * Publikus weboldal címe
     */
    private String webPage;
    /**
     * Kör bemutatkozása
     */
    private String introduction;
    /**
     * Levelezési lista címe
     */
    private String mailingList;
    /**
     * Alapítás éve
     */
    private Integer founded;
    /**
     * Alcsoportok
     */
    private List<Group> subGroups;
    /**
     * Státusz (aktiv / öreg)
     */
    private GroupStatus status;
    /**
     * Oszthat-e pontot/belépőt
     */
    private Integer flags;
    /**
     * Csoporttagságok
     */
    private List<Membership> memberships;
    /**
     * Cache-elt mező
     */
    private List<User> members;
    /**
     * Aktív tagságok
     */
    private List<Membership> activeMembers;
    /**
     * Öregtagok
     */
    private List<Membership> inactiveMembers;

    /** Creates a new instance of Group */
    public Group() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(generator = "groups_seq")
    @Column(name = "grp_id")
    public Long getId() {
        return id;
    }

    @Column(name = "grp_name", length = 255, columnDefinition = "text")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }

    @Column(name = "grp_flags")
    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "grp_state")
    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "grp_parent")
    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }

    @Column(name = "grp_founded")
    public Integer getFounded() {
        return founded;
    }

    public void setFounded(Integer founded) {
        this.founded = founded;
    }

    @Column(name = "grp_description", columnDefinition = "text")
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Column(name = "grp_maillist", length = 64)
    public String getMailingList() {
        return mailingList;
    }

    public void setMailingList(String mailingList) {
        this.mailingList = mailingList;
    }

    @Column(name = "grp_webpage", length = 64)
    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }

    @Transient
    public List<Group> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<Group> subGroups) {
        this.subGroups = subGroups;
    }

    @Column(name = "grp_type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Transient
    public List<User> getMembers() {
        if (members == null) {
            loadMembers();
        }
        return members;
    }

    private void loadMembers() {
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
        if (this.getMemberships() != null) {
            Collections.sort(this.getMemberships(),
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

    @Override
    public String toString() {
        return getName();
    }

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
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.webPage == null) ? (other.webPage != null) : !this.webPage.equals(other.webPage)) {
            return false;
        }
        if ((this.introduction == null) ? (other.introduction != null) : !this.introduction.equals(other.introduction)) {
            return false;
        }
        if ((this.mailingList == null) ? (other.mailingList != null) : !this.mailingList.equals(other.mailingList)) {
            return false;
        }
        if (this.founded != other.founded &&
                (this.founded == null ||
                !this.founded.equals(other.founded))) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        if (this.flags != other.flags &&
                (this.flags == null || !this.flags.equals(other.flags))) {
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
        hash =
                31 * hash +
                (this.mailingList != null ? this.mailingList.hashCode() : 0);
        hash =
                31 * hash +
                (this.founded != null ? this.founded.hashCode() : 0);
        hash = 31 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 31 * hash + (this.flags != null ? this.flags.hashCode() : 0);
        return hash;
    }
}
