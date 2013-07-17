package hu.sch.domain.user;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import java.io.Serializable;
import java.text.Collator;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Felhasználót reprezentáló entitás.
 *
 * @author hege
 * @author tomi
 */
@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = User.findWithMemberships,
            query = "SELECT u FROM User u LEFT OUTER JOIN FETCH u.memberships WHERE u.id = :id"),
    @NamedQuery(name = User.findUserByNeptunCode,
            query = "SELECT u FROM User u WHERE u.neptunCode = :neptun"),
    @NamedQuery(name = User.findUser, query = "SELECT u FROM User u WHERE upper(u.neptunCode) = upper(:neptunkod) OR "
            + "upper(u.emailAddress) = upper(:emailcim)"),
    @NamedQuery(name = User.findUsersForGroupAndPost,
            query = "SELECT u FROM User u "
            + "LEFT JOIN u.memberships ms "
            + "LEFT JOIN ms.posts p "
            + "LEFT JOIN p.postType pt "
            + "WHERE ms.groupId = :groupId AND pt.postName = :post"),
    @NamedQuery(name = User.getAllValuatedSemesterForUser, query = "SELECT DISTINCT pr.valuation.semester FROM PointRequest pr WHERE pr.user = :user ORDER BY pr.valuation.semester DESC")
})
@SequenceGenerator(name = "users_seq", sequenceName = "users_usr_id_seq")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class User implements Serializable, Comparable<User> {

    private static final Collator huCollator = Collator.getInstance(new Locale("hu"));
    private static final long serialVersionUID = 1L;
    public static final String findWithMemberships = "findUserWithMemberships";
    public static final String findUserByNeptunCode = "findUserByNeptunCode";
    public static final String findUser = "findUser";
    public static final String findUsersForGroupAndPost = "getMembersForGroupAndPost";
    public static final String getAllValuatedSemesterForUser = "getAllValuatedSemesterForUser";
    /*
     usr_id                      | bigint                 | not null default nextval('users_usr_id_seq'::regclass)
     usr_email                   | character varying(64)  |
     usr_neptun                  | character(6)           |
     usr_firstname               | text                   | not null
     usr_lastname                | text                   | not null
     usr_nickname                | text                   |
     usr_svie_state              | character varying(255) | not null default 'NEMTAG'::character varying
     usr_svie_member_type        | character varying(255) | not null default 'NEMTAG'::character varying
     usr_svie_primary_membership | integer                |
     usr_delegated               | boolean                | not null default false
     usr_show_recommended_photo  | boolean                | not null default false
     usr_screen_name             | character varying(50)  | not null
     usr_date_of_birth           | date                   |
     usr_gender                  | character varying(50)  | not null
     usr_student_status          | character varying(50)  | not null
     usr_mother_name             | character varying(100) |
     usr_photo_path              | character varying(255) |
     usr_webpage                 | character varying(255) |
     usr_cell_phone              | character varying(15)  |
     usr_home_address            | character varying(255) |
     usr_est_grad                | character(9)           |
     usr_dormitory               | character varying(50)  |
     usr_room                    | character varying(10)  |
     usr_confirm                 | character(64)          |
     */
    private Long id;
    private String screenName;
    @XmlElement
    private String emailAddress;
    private String neptunCode;
    @XmlElement
    private String firstName;
    @XmlElement
    private String lastName;
    @XmlElement
    private String nickName;
    private Date dateOfBirth;
    private Gender gender;
    private StudentStatus studentStatus;
    private String mothersName;
    private String photoPath;
    private String webpage;
    private String cellPhone;
    private String homeAddress;
    private String estimatedGraduationYear;
    private String dormitory;
    private String room;
    private String confirmationCode;
    private SvieStatus svieStatus;
    private SvieMembershipType svieMembershipType;
    private Membership sviePrimaryMembership;
    private List<Membership> memberships;
    private boolean delegated;
    private List<Group> groups;
    private boolean showRecommendedPhoto;
    private List<IMAccount> imAccounts;
    private List<UserAttribute> privateAttributes;

    public User() {
        this.delegated = false;
        this.showRecommendedPhoto = false;
    }



    /**
     * Felhasználó azonosítója.
     *
     * @return A user virId-je
     */
    @Id
    @GeneratedValue(generator = "users_seq")
    @Column(name = "usr_id", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * E-mail cím
     */
    @Column(name = "usr_email", length = 64, columnDefinition = "varchar(64)")
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Neptun-kód
     */
    @Size(max = 6, min = 6)
    @Column(name = "usr_neptun", columnDefinition = "char(6)", length = 6,
            nullable = true, updatable = false)
    public String getNeptunCode() {
        return neptunCode;
    }

    public void setNeptunCode(String neptunCode) {
        this.neptunCode = neptunCode;
    }

    /**
     * Keresztnév
     */
    @NotNull
    @Column(name = "usr_firstname", nullable = false, columnDefinition = "text")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Vezetéknév
     */
    @NotNull
    @Column(name = "usr_lastname", nullable = false, columnDefinition = "text")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Becenév
     */
    @Column(name = "usr_nickname", nullable = true, columnDefinition = "text")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * SVIE tagság státusza
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "usr_svie_state", nullable = false)
    public SvieStatus getSvieStatus() {
        return svieStatus;
    }

    public void setSvieStatus(SvieStatus svieStatus) {
        this.svieStatus = svieStatus;
    }

    /**
     * Összeállítja egy felhasználóhoz a SVIE tagság állapotát stringként. A
     * compareToMs paraméter lehet null
     *
     * @param compareToMs ha nem null, akkor ehhez a tagsághoz képest adja
     * vissza a tagság státuszát (kiírja, hogy ha más az elsődleges köre)
     * @return
     */
    public String getSvieMemberText(final Membership compareToMs) {
        switch (svieStatus) {
            case ELFOGADASALATT:
                return SvieStatus.ELFOGADASALATT.toString();
            case FELDOLGOZASALATT:
                return SvieStatus.FELDOLGOZASALATT.toString();
            case ELFOGADVA:
                switch (svieMembershipType) {
                    case PARTOLOTAG:
                        return SvieMembershipType.PARTOLOTAG.toString();
                    default:
                        if (compareToMs == null) {
                            return SvieMembershipType.RENDESTAG.toString();
                        } else {
                            if (sviePrimaryMembership != null
                                    && sviePrimaryMembership.getGroupId().equals(compareToMs.getGroupId())) {

                                return SvieMembershipType.RENDESTAG.toString();
                            } else {
                                String primGroupText = getSviePrimaryMembershipText();
                                if (primGroupText.isEmpty()) {
                                    return "Nincs elsődleges köre";
                                } else {
                                    return "Más elsődleges kör: " + primGroupText;
                                }
                            }
                        }
                }
            default:
                return SvieStatus.NEMTAG.toString();
        }
    }

    /**
     * SVIE tagság típusa
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "usr_svie_member_type", nullable = false)
    public SvieMembershipType getSvieMembershipType() {
        return svieMembershipType;
    }

    public void setSvieMembershipType(SvieMembershipType svieMembershipType) {
        this.svieMembershipType = svieMembershipType;
    }

    /**
     * Az illető küldött-e az elsődleges körében
     */
    @NotNull
    @Column(name = "usr_delegated", nullable = false, columnDefinition = "boolean default false")
    public Boolean getDelegated() {
        return delegated;
    }

    public void setDelegated(Boolean newValue) {
        this.delegated = newValue;
    }

    /**
     * Megmutassuk-e neki, hogy van egy fotó, amit javaslunk
     */
    @NotNull
    @Column(name = "usr_show_recommended_photo", nullable = false, columnDefinition = "boolean default false")
    public Boolean isShowRecommendedPhoto() {
        return showRecommendedPhoto;
    }

    public void setShowRecommendedPhoto(boolean showRecommendedPhoto) {
        this.showRecommendedPhoto = showRecommendedPhoto;
    }

    /**
     * SVIE elsődleges kör.
     *
     * Rendes tagsága kell legyen a körben.
     */
    @ManyToOne
    @JoinColumn(name = "usr_svie_primary_membership", insertable = true, updatable = true)
    public Membership getSviePrimaryMembership() {
        return sviePrimaryMembership;
    }

    public void setSviePrimaryMembership(Membership sviePrimaryMembership) {
        this.sviePrimaryMembership = sviePrimaryMembership;
    }

    /**
     * Körtagságok.
     *
     * TODO: rendes fetch legyen?
     */
    @Transient
    public List<Group> getGroups() {
        if (groups == null) {
            loadGroups();
        }
        return groups;
    }

    /**
     * Csoporttagságok - tagsági idővel kiegészítve
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public List<Membership> getMemberships() {
        if (memberships == null) {
            memberships = new ArrayList<>();
        }
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }

    /**
     * IM elérhetőségek.
     */
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id")
    public List<IMAccount> getImAccounts() {
        if (imAccounts == null) {
            imAccounts = new ArrayList<IMAccount>();
        }
        return imAccounts;
    }

    public void setImAccounts(List<IMAccount> imAccounts) {
        this.imAccounts = imAccounts;
    }

    @Transient
    public String getFullName() {
        return String.format("%s %s", getLastName(), getFirstName());
    }

    public void sortMemberships() {
        if (this.getMemberships() != null) {
            Collections.sort(this.getMemberships(),
                    new Comparator<Membership>() {
                @Override
                public int compare(Membership o1, Membership o2) {
                    if (o1.getEnd() == null ^ o2.getEnd() == null) {
                        return o1.getEnd() == null ? -1 : 1;
                    }
                    return o1.getGroup().compareTo(o2.getGroup());
                }
            });
        }
    }

    /**
     * A felhasználó választott felhasználóneve.
     *
     * Ezzel jelentkezik be. OpenDJ-ben 'uid' volt az attribútum neve.
     */
    @NotNull
    @Size(max = 50)
    @Column(name = "usr_screen_name", nullable = false, length = 50)
    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     * Születési dátum.
     */
    @Column(name = "usr_date_of_birth")
    @Temporal(TemporalType.DATE)
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * A felhasználó neme.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "usr_gender", nullable = false)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Hallgatói státusz.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "usr_student_status", nullable = false)
    public StudentStatus getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(StudentStatus studentStatus) {
        this.studentStatus = studentStatus;
    }

    /**
     * Anyaja neve.
     */
    @Column(name = "usr_mother_name", length = 100)
    public String getMothersName() {
        return mothersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    /**
     * A profilkép elérési útja.
     */
    @Column(name = "usr_photo_path")
    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    /**
     * A hallgató weboldala.
     */
    @Column(name = "usr_webpage")
    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    /**
     * Mobil szám.
     */
    @Size(max = 15)
    @Column(name = "usr_cell_phone")
    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    /**
     * Otthoni cím.
     */
    @Column(name = "usr_home_address")
    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    /**
     * Végzés várható éve.
     *
     * Formátum: YYYYYYYY[12] Például: 201220132 a 2012/13-as év második féléve
     */
    @Size(max = 9, min = 9)
    @Column(name = "usr_est_grad")
    public String getEstimatedGraduationYear() {
        return estimatedGraduationYear;
    }

    public void setEstimatedGraduationYear(String estimatedGraduationYear) {
        this.estimatedGraduationYear = estimatedGraduationYear;
    }

    /**
     * Kollégium neve.
     */
    @Column(name = "usr_dormitory")
    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    /**
     * Szobaszám.
     *
     * String, hogy az esetlegesen betűt is tartamlazó szobákat.
     */
    @Column(name = "usr_room")
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Regisztrációhoz szükéges megerősítő kód.
     */
    @Column(name = "usr_confirm")
    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    /**
     * Rejtett attribútumok.
     *
     * Egy attribútum csak akkor látható, ha benne van a kollekcióban és a
     * 'visibile' mezője true értékű.
     */
    @ElementCollection
    @CollectionTable(name = "usr_private_attrs", joinColumns = {
        @JoinColumn(name = "usr_id")})
    @OrderColumn(name = "attr_name")
    public List<UserAttribute> getPrivateAttributes() {
        if (privateAttributes == null) {
            privateAttributes = new ArrayList<>();
        }
        return privateAttributes;
    }

    public void setPrivateAttributes(List<UserAttribute> privateAttributes) {
        this.privateAttributes = privateAttributes;
    }

    /**
     * Eldönti egy megadott attribútum típsuról, hogy az látható-e.
     *
     * @param attr az attritbútum neve
     * @return true ha látható, egyébként false.
     */
    public boolean isAttributeVisible(UserAttributeName attr) {
        UserAttribute userAttr = null;
        for (UserAttribute a : getPrivateAttributes()) {
            if (a.getAttributeName() == attr) {
                userAttr = a;
                break;
            }
        }

        // csak akkor lathato egy attributum, ha expicit meg van jelolve lathatokent
        return (userAttr != null ? userAttr.isVisible() : false);
    }

    /**
     * Az elsődleges kör szöveges reprezentációja
     *
     * @return az elsődleges kör neve, ha van, különben egy üres string.
     */
    @Transient
    public String getSviePrimaryMembershipText() {
        if (sviePrimaryMembership != null) {
            return sviePrimaryMembership.getGroup().getName();
        }

        return "";
    }

    @Override
    public String toString() {
        return String.format("User#%d name: %s, email: %s", getId(), getFullName(), getEmailAddress());
    }

    @Override
    public int compareTo(User o) {
        return huCollator.compare(getFullName(), o.getFullName());
    }

    public int compareToBySvieMemberText(final User u, final Membership compareToMs) {
        return huCollator.compare(getSvieMemberText(compareToMs), u.getSvieMemberText(compareToMs));
    }

    /**
     * Egyenlőség vizsgálat id alapján.
     *
     * Először referencia szerinti vizsgálatot végez.
     * Ha az id null, akkor csak önmagával referencia szitnen megegyező objektumra
     * ad vissza true-t.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }

        final User other = (User) obj;

        if (this.getId() != null) {
            return this.getId().equals(other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 61 * hash + (this.neptunCode != null ? this.neptunCode.hashCode() : 0);
        hash = 61 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 61 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        return hash;
    }

    private void loadGroups() {
        groups = new ArrayList<Group>();
        if (memberships != null) {
            for (Membership m : memberships) {
                groups.add(m.getGroup());
            }
        }
    }
}
