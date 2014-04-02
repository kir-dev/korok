package hu.sch.domain;

import hu.sch.domain.enums.GroupStatus;
import hu.sch.domain.user.User;
import hu.sch.domain.logging.Log;
import hu.sch.util.HungarianStringComparator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
import org.hibernate.annotations.Index;

/**
 *
 * @author hege
 */
@Entity
@Table(name = "groups")
@NamedQueries({
    @NamedQuery(name = Group.findAll, query = "SELECT g FROM Group g "
            + "WHERE g.status='akt' ORDER BY g.name"),
    @NamedQuery(name = Group.groupHierarchy, query =
            "SELECT g FROM Group g LEFT JOIN FETCH g.parent "
            + "WHERE g.status='akt' ORDER BY g.name"),
    @NamedQuery(name = Group.findByName, query = "SELECT g FROM Group g WHERE g.name = :name"),
    @NamedQuery(name = Group.findMembersByGroupAndPost,
            query = "SELECT u FROM User u "
            + "LEFT JOIN u.memberships ms "
            + "LEFT JOIN ms.posts p "
            + "LEFT JOIN p.postType pt "
            + "WHERE ms.groupId = :groupId AND pt.postName = :post")
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Group implements Serializable, Comparable<Group> {

    public static final long SVIE = 369L;
    public static final long VALASZTMANY = 370L;
    public static final long SCH_QPA = 27L;
    /**
     * Jutalmazást Elbíráló Testület
     */
    public static final long JET = 156L;
    public static final long KIRDEV = 106L;
    private static final long serialVersionUID = 1L;
    public static final String findAll = "findAllGroup";
    public static final String groupHierarchy = "groupHierarchy";
    public static final String findByName = "findByName";
    public static final String findMembersByGroupAndPost = "findMembersByGroupAndPost";
    //----------------------------------------------------
    @Id    
    @SequenceGenerator(name = "groups_seq", sequenceName = "groups_grp_id_seq",
            allocationSize = 1, initialValue = 1)
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "groups_seq")
    @Index(name = "groups_grp_id_idx")
    @Column(name = "grp_id", unique = true)
    private Long id;
    //----------------------------------------------------
    @Index(name = "idx_groups_grp_name")
    @Column(name = "grp_name", length = 255, nullable = false, columnDefinition = "text")
    private String name;
    //----------------------------------------------------
    @Index(name = "idx_groups_grp_type")
    @Column(name = "grp_type", length = 20, nullable = false)
    private String type;
    //----------------------------------------------------
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "grp_parent")
    private Group parent;
    //----------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(name = "grp_state", columnDefinition = "varchar(255) default 'akt'::bpchar")
    private GroupStatus status = GroupStatus.akt;
    //----------------------------------------------------
    @Column(name = "grp_description", columnDefinition = "text")
    private String introduction;
    //----------------------------------------------------
    @Column(name = "grp_webpage", length = 64)
    private String webPage;
    //----------------------------------------------------
    @Column(name = "grp_maillist", length = 64)
    private String mailingList;
    //----------------------------------------------------
    @Column(name = "grp_users_can_apply", nullable = false, columnDefinition = "boolean default true")
    private boolean usersCanApply;
    //----------------------------------------------------
    @XmlTransient
    @Column(name = "grp_head", length = 48)
    private String head;
    //----------------------------------------------------
    @XmlTransient
    @Column(name = "grp_founded")
    private Integer founded;
    //----------------------------------------------------
    @Column(name = "grp_issvie", nullable = false, columnDefinition = "boolean default false")
    private Boolean isSvie = Boolean.FALSE;
    //----------------------------------------------------
    @XmlTransient
    @Column(name = "grp_svie_delegate_nr")
    private Integer delegateNumber;
    //----------------------------------------------------
    @XmlTransient
    @Transient
    private List<Group> subGroups;
    //----------------------------------------------------
    @XmlTransient
    @Transient
    private Long numberOfPrimaryMembers;
    //----------------------------------------------------
    @XmlTransient
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Membership> memberships;
    //----------------------------------------------------
    @XmlTransient
    @Transient
    private List<User> members;
    //----------------------------------------------------
    @XmlTransient
    @Transient
    private List<Membership> activeMemberships;
    //----------------------------------------------------
    @XmlTransient
    @Transient
    private List<Membership> inactiveMemberships;
    //----------------------------------------------------
    @XmlTransient
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Log> logs;
    //----------------------------------------------------
    @XmlTransient
    @Transient
    private List<User> activeUsers;

    public Group() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }

    /**
     * Státusz (aktiv / öreg)
     */
    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    /**
     * Kör bemutatkozása
     */
    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }

    public String getMailingList() {
        return mailingList;
    }

    public void setMailingList(String mailingList) {
        this.mailingList = mailingList;
    }

    /**
     * A kör vezetőjének egyéni titulusa
     */
    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Integer getFounded() {
        return founded;
    }

    public void setFounded(Integer founded) {
        this.founded = founded;
    }

    /**
     * Az adott kör tagja-e a SVIE-nek
     */
    public Boolean getIsSvie() {
        return isSvie;
    }

    public void setIsSvie(Boolean isSvie) {
        this.isSvie = isSvie;
    }

    /**
     * Az adott kör hány tagot küldhet küldött gyülésre
     */
    public Integer getDelegateNumber() {
        return delegateNumber;
    }

    public void setDelegateNumber(Integer delegateNumber) {
        this.delegateNumber = delegateNumber;
    }

    /**
     * Az elsődleges körtagok száma
     */
    public Long getNumberOfPrimaryMembers() {
        return numberOfPrimaryMembers;
    }

    public void setNumberOfPrimaryMembers(Long numberOfPrimaryMembers) {
        this.numberOfPrimaryMembers = numberOfPrimaryMembers;
    }

    /**
     * Csoporttagságok
     */
    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }

    /**
     * Alcsoportok
     */
    public List<Group> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<Group> subGroups) {
        this.subGroups = subGroups;
    }

    /**
     * Cache-elt mező
     */
    public List<User> getMembers() {
        if (members == null) {
            loadMembers();
        }
        return members;
    }

    public List<User> getActiveMembers() {
        if (members == null) {
            loadMembers();
        }
        return activeUsers;
    }

    private void loadMembers() {
        sortMemberships();
        List<Membership> list = getMemberships();

        members = new ArrayList<>(list.size());
        activeMemberships = new ArrayList<>();
        inactiveMemberships = new ArrayList<>();
        activeUsers = new ArrayList<>(list.size());
        for (Membership cst : list) {
            members.add(cst.getUser());
            if (cst.getEnd() == null) {
                activeUsers.add(cst.getUser());
                activeMemberships.add(cst);
            } else {
                inactiveMemberships.add(cst);
            }
        }
    }

    /**
     * Aktív tagságok
     */
    public List<Membership> getActiveMemberships() {
        if (members == null) {
            loadMembers();
        }
        return activeMemberships;
    }

    /**
     * Öregtagok
     */
    public List<Membership> getInactiveMemberships() {
        if (members == null) {
            loadMembers();
        }
        return inactiveMemberships;
    }

    public void sortMemberships() {
        if (getMemberships() != null) {
            Collections.sort(getMemberships(), new Comparator<Membership>() {
                @Override
                public int compare(Membership o1, Membership o2) {
                    if (o1.getEnd() == null ^ o2.getEnd() == null) {
                        return o1.getEnd() == null ? -1 : 1;
                    }
                    return o1.getUser().compareTo(o2.getUser());
                }
            });
        }
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    /**
     * Jelentkezhetnek-e új tagok a körbe
     */
    public boolean getUsersCanApply() {
        return usersCanApply;
    }

    public void setUsersCanApply(boolean canApply) {
        usersCanApply = canApply;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Group o) {
        return HungarianStringComparator.scompare(getName(), o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        //Részletekért lásd: http://brandon.fuller.name/archives/2009/03/17/16.37.41/
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Group other = (Group) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.getId()))) {
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
