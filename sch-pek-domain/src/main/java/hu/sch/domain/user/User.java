package hu.sch.domain.user;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.enums.SvieStatus;
import hu.sch.util.HungarianStringComparator;
import java.io.Serializable;
import java.nio.file.Paths;
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
            query = "SELECT u FROM User u WHERE UPPER(u.neptunCode) = UPPER(:neptun)"),
    @NamedQuery(name = User.findByScreenName,
            query = "SELECT u FROM User u WHERE UPPER(u.screenName) = UPPER(:screenName)"),
    @NamedQuery(name = User.findUser, query = "SELECT u FROM User u WHERE upper(u.neptunCode) = upper(:neptunkod) OR "
            + "upper(u.emailAddress) = upper(:emailcim)"),
    @NamedQuery(name = User.getAllValuatedSemesterForUser, query = "SELECT DISTINCT pr.valuation.semester FROM PointRequest pr WHERE pr.user = :user ORDER BY pr.valuation.semester DESC")
})
@SequenceGenerator(name = "users_seq", sequenceName = "users_usr_id_seq",
        allocationSize = 1)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class User implements Serializable, Comparable<User> {

    private static final long serialVersionUID = 1L;
    public static final String findWithMemberships = "findUserWithMemberships";
    public static final String findUserByNeptunCode = "findUserByNeptunCode";
    public static final String findUser = "findUser";
    public static final String findByScreenName = "findByScreenName";
    public static final String getAllValuatedSemesterForUser = "getAllValuatedSemesterForUser";
    //----------------------------------------------------
    @Id
    @GeneratedValue(generator = "users_seq")
    @Column(name = "usr_id", nullable = false)
    private Long id;
    //----------------------------------------------------
    @NotNull
    @Size(max = 50)
    @Column(name = "usr_screen_name", nullable = false, length = 50)
    private String screenName;
    //----------------------------------------------------
    @XmlElement
    @Column(name = "usr_email", length = 64, columnDefinition = "varchar(64)")
    private String emailAddress;
    //----------------------------------------------------
    @Size(max = 6, min = 6)
    @Column(name = "usr_neptun", columnDefinition = "char(6)", length = 6, nullable = true)
    private String neptunCode;
    //----------------------------------------------------
    @XmlElement
    @NotNull
    @Column(name = "usr_firstname", nullable = false, columnDefinition = "text")
    private String firstName;
    //----------------------------------------------------
    @XmlElement
    @NotNull
    @Column(name = "usr_lastname", nullable = false, columnDefinition = "text")
    private String lastName;
    //----------------------------------------------------
    @XmlElement
    @Column(name = "usr_nickname", nullable = true, columnDefinition = "text")
    private String nickName;
    //----------------------------------------------------
    @Column(name = "usr_date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    //----------------------------------------------------
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "usr_gender", nullable = false)
    private Gender gender;
    //----------------------------------------------------
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "usr_student_status", nullable = false)
    private StudentStatus studentStatus;
    //----------------------------------------------------
    @Column(name = "usr_mother_name", length = 100)
    private String mothersName;
    //----------------------------------------------------
    @Column(name = "usr_photo_path")
    private String photoPath;
    //----------------------------------------------------
    @Column(name = "usr_webpage")
    private String webpage;
    //----------------------------------------------------
    @Size(max = 50)
    @Column(name = "usr_cell_phone")
    private String cellPhone;
    //----------------------------------------------------
    @Column(name = "usr_home_address")
    private String homeAddress;
    //----------------------------------------------------
    @Size(max = 10, min = 10)
    @Column(name = "usr_est_grad")
    private String estimatedGraduationYear;
    //----------------------------------------------------
    @Column(name = "usr_dormitory")
    private String dormitory;
    //----------------------------------------------------
    @Column(name = "usr_room")
    private String room;
    //----------------------------------------------------
    @Column(name = "usr_confirm", length = 64)
    @Size(max = 64)
    private String confirmationCode;
    //----------------------------------------------------
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "usr_svie_state", nullable = false)
    private SvieStatus svieStatus;
    //----------------------------------------------------
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "usr_svie_member_type", nullable = false)
    private SvieMembershipType svieMembershipType;
    //----------------------------------------------------
    @ManyToOne
    @JoinColumn(name = "usr_svie_primary_membership", insertable = true, updatable = true)
    private Membership sviePrimaryMembership;
    //----------------------------------------------------
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Membership> memberships;
    //----------------------------------------------------
    @NotNull
    @Column(name = "usr_delegated", nullable = false, columnDefinition = "boolean default false")
    private boolean delegated;
    //----------------------------------------------------
    @Transient
    private List<Group> groups;
    //----------------------------------------------------
    @NotNull
    @Column(name = "usr_show_recommended_photo", nullable = false, columnDefinition = "boolean default false")
    private boolean showRecommendedPhoto;
    //----------------------------------------------------
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usr_id", referencedColumnName = "usr_id", nullable = false)
    private Set<IMAccount> imAccounts;
    //----------------------------------------------------
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usr_id", referencedColumnName = "usr_id", nullable = false)
    private Set<UserAttribute> privateAttributes;
    //----------------------------------------------------
    @Column(name = "usr_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus userStatus;
    //----------------------------------------------------
    @Column(name = "usr_password")
    private String passwordDigest;
    //----------------------------------------------------
    @Column(name = "usr_salt")
    private String salt;

    public User() {
        this.delegated = false;
        this.showRecommendedPhoto = false;
    }

    /**
     * Felhasználó azonosítója.
     *
     * @return A user virId-je
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * E-mail cím
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Neptun-kód
     */
    public String getNeptunCode() {
        return neptunCode;
    }

    public void setNeptunCode(final String neptunCode) {
        if (neptunCode != null) {
            this.neptunCode = neptunCode.toUpperCase();
        } else {
            this.neptunCode = null;
        }
    }

    /**
     * Keresztnév
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Vezetéknév
     */
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Becenév
     */
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * SVIE tagság státusza
     */
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
    public SvieMembershipType getSvieMembershipType() {
        return svieMembershipType;
    }

    public void setSvieMembershipType(SvieMembershipType svieMembershipType) {
        this.svieMembershipType = svieMembershipType;
    }

    /**
     * Az illető küldött-e az elsődleges körében
     */
    public Boolean getDelegated() {
        return delegated;
    }

    public void setDelegated(Boolean newValue) {
        this.delegated = newValue;
    }

    /**
     * Megmutassuk-e neki, hogy van egy fotó, amit javaslunk
     */
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
    public List<Group> getGroups() {
        if (groups == null) {
            loadGroups();
        }
        return groups;
    }

    /**
     * Csoporttagságok - tagsági idővel kiegészítve
     */
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
    public Set<IMAccount> getImAccounts() {
        if (imAccounts == null) {
            imAccounts = new HashSet<>();
        }
        return imAccounts;
    }

    public void setImAccounts(Set<IMAccount> imAccounts) {
        this.imAccounts = imAccounts;
    }

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
    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    /**
     * Születési dátum.
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * A felhasználó neme.
     */
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Hallgatói státusz.
     */
    public StudentStatus getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(StudentStatus studentStatus) {
        this.studentStatus = studentStatus;
    }

    /**
     * Anyaja neve.
     */
    public String getMothersName() {
        return mothersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    /**
     * A profilkép elérési útja.
     *
     * Relatív a felöltött képeket tároló mappához. pl. pistike/profile.jpg
     */
    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    /**
     * A profilkép teljes elérési útja.
     *
     * @return
     */
    public String getPhotoFullPath(String basePath) {
        return Paths.get(basePath, getPhotoPath()).toString();
    }

    /**
     * Azt jelzi, hogy van-e profilképe a usernek.
     *
     * @return
     */
    public boolean hasPhoto() {
        return getPhotoPath() != null;
    }

    /**
     * A hallgató weboldala.
     */
    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    /**
     * Mobil szám.
     */
    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    /**
     * Otthoni cím.
     */
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
    public String getEstimatedGraduationYear() {
        return estimatedGraduationYear;
    }

    public void setEstimatedGraduationYear(String estimatedGraduationYear) {
        this.estimatedGraduationYear = estimatedGraduationYear;
    }

    /**
     * Kollégium neve.
     */
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
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Gets the combined dormitory and room number.
     *
     * Fromat [Dormitory] [Room]
     *
     * @return
     */
    public String getFullRoomNumber() {
        if (dormitory == null && room == null) {
            return "";
        }
        if (dormitory == null) {
            return room;
        }
        if (room == null) {
            return dormitory;
        }
        return String.format("%s %s", dormitory, room);
    }

    /**
     * Regisztrációhoz szükéges megerősítő kód.
     */
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
    public Set<UserAttribute> getPrivateAttributes() {
        if (privateAttributes == null) {
            privateAttributes = new HashSet<>();
        }
        return privateAttributes;
    }

    public void setPrivateAttributes(Set<UserAttribute> privateAttributes) {
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
    public String getSviePrimaryMembershipText() {
        if (sviePrimaryMembership != null) {
            return sviePrimaryMembership.getGroup().getName();
        }

        return "";
    }

    /**
     * A felhasználó SSO státusza.
     *
     * @return az felhasználó sso statusa vagy null, ha nincs kitöltve
     */
    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * A felhasználó jelszó hash-e.
     *
     * @return
     */
    public String getPasswordDigest() {
        return passwordDigest;
    }

    public void setPasswordDigest(String passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    /**
     * A jelszó hash generáláshoz használt salt.
     *
     * @return
     */
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return String.format("User#%d name: %s, email: %s", getId(), getFullName(), getEmailAddress());
    }

    @Override
    public int compareTo(User o) {
        return HungarianStringComparator.scompare(getFullName(), o.getFullName());
    }

    public int compareToBySvieMemberText(final User u, final Membership compareToMs) {
        return HungarianStringComparator.scompare(getSvieMemberText(compareToMs), u.getSvieMemberText(compareToMs));
    }

    /**
     * Egyenlőség vizsgálat id alapján.
     *
     * Először referencia szerinti vizsgálatot végez. Ha az id null, akkor csak
     * önmagával referencia szitnen megegyező objektumra ad vissza true-t.
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
        groups = new ArrayList<>();
        if (memberships != null) {
            for (Membership m : memberships) {
                groups.add(m.getGroup());
            }
        }
    }
}
