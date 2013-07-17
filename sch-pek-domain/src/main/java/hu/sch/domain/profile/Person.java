package hu.sch.domain.profile;

import hu.sch.domain.user.IMAccount;
import hu.sch.domain.user.Gender;
import hu.sch.domain.user.StudentStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author konvergal
 * @author tomi
 */
public class Person implements Serializable {

    public static final String SORT_BY_UID = "uid";
    public static final String SORT_BY_NAME = "fullName";
    public static final String SORT_BY_NEPTUN = "neptun";
    public static final String SORT_BY_NICKNAME = "nickName";
    public static final String SORT_BY_MAIL = "mail";
    public static final String SORT_BY_ROOMNUMBER = "roomNumber";

    /**
     * A felhasználó által feltölthető kép maximális mérete. Átméretezéshez kell
     */
    public static final int IMAGE_MAX_SIZE = 320;
    /**
     * Címtár megfelelő: uid
     * A felhasználó egyedi azonosítója a címtárban.
     */
    private String uid;
    /**
     * Címtár megfelelő: displayName
     * A felhasználó beceneve.
     */
    private String nickName;
    /**
     * Címtár megfelelő: givenName
     * A felhasználó keresztneve.
     */
    private String firstName;
    /**
     * Címtár megfelelő: sn
     * A felhasználó vezetékneve.
     */
    private String lastName;
    /**
     * Címtár megfelelő: schacUserPresenceID
     * Az IM címeket tartalmazó lista.
     */
    private List<IMAccount> IMAccounts;
    /**
     * A régi VIR adatbázisban lévő user id-ja.
     * A schacPersonalUniqueId ldap attribútum virID része.
     * Címtár megfelelő: schacPersonalUniqueID
     */
    private Long virId;
    /**
     * Neptun kód.
     * Címtár megfelelő: schacPersonalUniqueCode
     */
    private String neptun;
    /**
     * Címtár megfelelő: mail
     * A felhasználó e-mail címe.
     */
    private String mail;
    /**
     * Címtár megfelelő: mobile
     * A felhasználó mobilszáma.
     */
    private String mobile;
    /**
     * Címtár megfelelő: homePhone
     * A felhasználó vezetékes telefonszáma.
     */
    private String homePhone;
    /**
     * Kollégium.
     *
     * roomNumberrel párban adják a roomNumber címtár attribútumot.
     */
    private String dormitory;
    /**
     * Szobaszám.
     *
     * dormitory-val párban adják a roomNumber címtár attribútumot.
     */
    private String roomNumber;
    /**
     * A felhasználó lakcíme.
     * Címtár megfelelő: homePostalAddress
     */
    private String homePostalAddress;
    /**
     * Címtár megfelelő: labeledURI
     * A felhasználó weboldala.
     */
    private String webpage;
    /**
     * A felhasználó neme.
     * Címtár megfelelő: schacGender
     * ISO-5218 szerint: 0-nem ismert, 1-férfi, 2-nő, 9-nem specifikált.
     */
    private Gender gender;
    /**
     * A felhasználó születési dátuma.
     * Címtár megfelelő: schacDateOfBirth
     * RFC-3339 "YYYYMMDD" formátumban.
     */
    private Date dateOfBirth;
    /**
     * Címtár megfelelő: sch-vir-mothersName
     * A felhasználó édesanyjának neves.
     */
    private String mothersName;
    /**
     * Címtár megfelelő: sch-vir-
     * Az egyetem befejezésének várható ideje.
     * Formátum: YYYYYYYY/[12], pl: 20092010/1
     */
    private String estimatedGraduationYear;
    /**
     * A felhasználó hallgatói státusza.
     * Címtár megfelelő: schacUserStatus
     */
    private StudentStatus studentStatus;
    /**
     * Az SSO felhasználó státusza.
     * Címtár megfelelő: inetUserStatus
     */
    private UserStatus userStatus;

    /**
     * A felhasználó privát attribútumai sztringlistában.
     * Címtár megfelelő: schacUserPrivateAttribute
     */
    private List<String> privateAttributes;
    /**
     * Profilkép.
     * Címtár megfelelő: jpegPhoto
     */
    private byte[] photo;

    /**
     * Ellenőrző kód.
     *
     * NOTE: azért kell backing field, mert van olyan, hogy null, és erre
     * építünk is.
     */
    private String confirmationCode;

    public Person() {
        this(new ArrayList<String>());
    }

    public Person(List<String> privateAttributes) {
        this.privateAttributes = privateAttributes;
    }

    //<editor-fold defaultstate="collapsed" desc="getters and setters">
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return String.format("%s %s", lastName, firstName);
    }

    public Long getVirId() {
        return virId;
    }

    public void setVirId(Long virId) {
        this.virId = virId;
    }

    public String getNeptun() {
        return neptun;
    }

    public void setNeptun(String neptun) {
        this.neptun = neptun;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getHomePostalAddress() {
        return homePostalAddress;
    }

    public void setHomePostalAddress(String homePostalAddress) {
        this.homePostalAddress = homePostalAddress;
    }

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getMothersName() {
        return mothersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    public String getEstimatedGraduationYear() {
        return estimatedGraduationYear;
    }

    public void setEstimatedGraduationYear(String estimatedGraduationYear) {
        this.estimatedGraduationYear = estimatedGraduationYear;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public StudentStatus getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(StudentStatus studentStatus) {
        this.studentStatus = studentStatus;
    }

    public List<IMAccount> getIMAccounts() {
        return IMAccounts;
    }

    public void setIMAccounts(List<IMAccount> IMAccounts) {
        this.IMAccounts = IMAccounts;
    }

    public UserStatus getStatus() {
        return userStatus;
    }

    public void setStatus(UserStatus status) {
        this.userStatus = status;
    }

    public boolean isActive() {
        return UserStatus.ACTIVE == userStatus;
    }

    public String[] getPrivateAttributes() {
        return privateAttributes.toArray(new String[privateAttributes.size()]);
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public Boolean isPrivateAttribute(String attribute) {
        return privateAttributes.contains(attribute);
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getPhoto() {
        return photo;
    }
    //</editor-fold>

    /**
     * Ez a függvény az eddigi privát adatot publikussá, a publikusat pedig
     * privát adattá teszi.
     *
     * @param attribute Az attribútum, amit priváttá / publikussá szeretnénk tenni.
     */
    public void invertPrivateAttribute(String attribute) {
        if (isPrivateAttribute(attribute)) {
            privateAttributes.remove(attribute);
        } else {
            privateAttributes.add(attribute);
        }
    }

    public void generateAndSetConfirmationCode() {
        confirmationCode = RandomStringUtils.randomAlphanumeric(30);
    }
}
