package hu.sch.domain.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author konvergal
 */
public class Person implements Serializable {

    public static final String SORT_BY_UID = "uid";
    public static final String SORT_BY_NAME = "fullName";
    public static final String SORT_BY_NEPTUN = "neptun";
    public static final String SORT_BY_NICKNAME = "nickName";
    public static final String SORT_BY_MAIL = "mail";
    public static final String SORT_BY_ROOMNUMBER = "roomNumber";
    public static final String NEPTUN_PREFIX =
            "urn:mace:terena.org:schac:personalUniqueCode:hu:BME-NEPTUN:";
    public static final String STUDENTSTATUS_PREFIX =
            "urn:mace:terena.org:schac:status:sch.hu:student_status:";
    public static final String VIRID_PREFIX =
            "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:";
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
     * Címtár megfelelő: cn
     * A felhasználó teljes neve.
     */
    private String fullName;
    /**
     * Címtár megfelelő: schacUserPresenceID
     * Az IM címeket tartalmazó lista.
     */
    private List<IMAccount> IMAccounts;
    /**
     * Címtár megfelelő: schacPersonalUniqueID
     * A felhasználó VIRID-jét tartalmazó sztring.
     */
    private String personalUniqueID;
    /**
     * Származtatott érték, a régi VIR adatbázisban lévő user id-ja.
     * A schacPersonalUniqueId ldap attribútum virID része.
     */
    private Long virId;
    /**
     * Címtár megfelelő: schacPersonalUniqueCode
     * A felhasználó neptun kódját tartalmazó sztring.
     */
    private String personalUniqueCode;
    /**
     * Származtatott érték, a felhasználó neptun kódja.
     * A schacPersonalUniqueCode ldap attribútum neptun kód része.
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
     * Címtár megfelelő: nincs!
     * Kényelmes editáló form készítéséhez szükséges.
     * Ügyelni kell a konzisztenciájára!
     */
    private String dormitory;
    /**
     * Szobaszám.
     * Címtár megfelelő: nincs!
     * Kényelmes editáló form készítéséhez szükséges.
     * Ügyelni kell a konzisztenciájára!
     */
    private String rNumber;
    /**
     * Címtár megfelelő: roomNumber
     * Nem csak a szobaszámot tárolja, hanem a kollégiumot is!
     * Formátum: <Kollégium> <Szobaszám>
     */
    private String roomNumber;
    /**
     * Címtár megfelelő: homePostalAddress
     * A felhasználó lakcíme.
     */
    private String homePostalAddress;
    /**
     * Címtár megfelelő: labeledURI
     * A felhasználó weboldala.
     */
    private String webpage;
    /**
     * Címtár megfelelő: schacGender
     * A felhasználó neme.
     * ISO-5218 szerint: 0-nem ismert, 1-férfi, 2-nő, 9-nem specifikált.
     */
    private String gender;
    /**
     * Címtár megfelelő: schacDateOfBirth
     * A felhasználó születési dátuma.
     * RFC-3339 "YYYYMMDD" formátumban.
     */
    private String dateOfBirth;
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
     * Címtár megfelelő: schacUserStatus
     * A felhasználó hallgatói státusza. Ezek lehetnek:
     * urn:mace:terena.org:schac:status:sch.hu:student_status:active
     * urn:mace:terena.org:schac:status:sch.hu:student_status:newbie
     * urn:mace:terena.org:schac:status:sch.hu:student_status:other
     * urn:mace:terena.org:schac:status:sch.hu:student_status:graduated
     */
    private String studentUserStatus;
    /**
     * Címtár megfelelő: nincs!
     * Származtatott érték, a hallgatói státuszt tárolja. Lehetnek:
     * active
     * newbie
     * other
     * graduated
     */
    private String studentStatus;
    /**
     * Címtár megfelelő: inetUserStatus
     * Az SSO felhasználó státusza.
     * Active / Inactive
     */
    private String status;
    /**
     * Címtár megfelelő: schacUserPrivateAttribute
     * A felhasználó privát attribútumai sztringtömbben.
     */
    private String[] schacPrivateAttribute = {};
    /**
     * Címtár megfelelő: nincs!
     * A felhasználó privát attribútumai sztringlistában.
     */
    private List<String> privateAttributes = new ArrayList<String>();
    /**
     * Címtár megfelelő: jpegPhoto
     * A felhasználói kép byte-tömbben.
     */
    private byte[] photo;
    /**
     * Címtár megfelelő: nincs!
     * A felhasználó által megadott adatokból van származtatva.
     */
    private String confirmationCode;

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
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPersonalUniqueCode(String personalUniqueCode) {
        this.personalUniqueCode = personalUniqueCode;
    }

    public String getPersonalUniqueCode() {
        return personalUniqueCode;
    }

    public void setPersonalUniqueID(String personalUniqueID) {
        this.personalUniqueID = personalUniqueID;
    }

    public String getPersonalUniqueID() {
        return personalUniqueID;
    }

    public void setVirId(Long virId) {
        this.virId = virId;
    }

    public Long getVirId() {
        return virId;
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

    public String getRNumber() {
        return rNumber;
    }

    public void setRNumber(String rNumber) {
        this.rNumber = rNumber;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getStudentUserStatus() {
        return studentUserStatus;
    }

    public void setStudentUserStatus(String studentUserStatus) {
        this.studentUserStatus = studentUserStatus;
    }

    public String getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

    public List<IMAccount> getIMAccounts() {
        return IMAccounts;
    }

    public void setIMAccounts(List<IMAccount> IMAccounts) {
        this.IMAccounts = IMAccounts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    public String[] getSchacPrivateAttribute() {
        return schacPrivateAttribute;
    }

    public void setSchacPrivateAttribute(String[] schacPrivateAttribute) {
        this.schacPrivateAttribute = schacPrivateAttribute;
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

    /**
     * Ez a függvény az eddigi privát adatot publikussá, a publikusat pedig
     * privát adattá teszi.
     * @param attribute Melyik attribútumot szeretnénk priváttá / publikussá
     * tenni.
     */
    public void inversePrivateAttribute(String attribute) {
        if (isPrivateAttribute(attribute)) {
            privateAttributes.remove(attribute);
        } else {
            privateAttributes.add(attribute);
        }
    }

    /**
     * A felhasználó objektumnak a származtatott értékeit ez a függvény határozza
     * meg. A felhasználó objektum létrehozásakor hívódik meg a ContextMapper által.
     */
    public void setToUse() {
        //a privát attribútomok tömbjének listává alakítása
        privateAttributes.addAll(Arrays.asList(schacPrivateAttribute));

        //TODO: szebbé tenni
        if (personalUniqueCode != null) {
            String[] personalUniqueCodeArray = personalUniqueCode.split(":");
            neptun =
                    personalUniqueCodeArray[personalUniqueCodeArray.length - 1];
        }

        //TODO: szebbé tenni
        if (personalUniqueID != null) {
            String[] personalUniqueIDArray = personalUniqueID.split(":");
            try {
                virId = Long.parseLong(
                        personalUniqueIDArray[personalUniqueIDArray.length - 1]);
            } catch (NumberFormatException ex) {
                //TODO log
            }
        }

        //TODO: szebbé tenni
        if (studentUserStatus != null) {
            String[] studentUserStatusArray = studentUserStatus.split(":");
            this.studentStatus =
                    studentUserStatusArray[studentUserStatusArray.length - 1];
        }

        if (roomNumber != null) {
            Pattern p = Pattern.compile("^(.*)\\s([a-zA-Z]{0,1}[0-9]+)$");
            Matcher m = p.matcher(roomNumber);

            if (m.matches()) {
                dormitory = m.group(1);
                rNumber = m.group(2);
            }
        }
    }

    /**
     * A címtárba történő visszamentés előtt szükség van a származtatott értékek
     * változásának visszakövetésére a ContextMapper mapToContext függvényében
     * hívódik meg.
     */
    public void setToSave() {
        schacPrivateAttribute = privateAttributes.toArray(new String[privateAttributes.size()]);

        if (lastName != null && firstName != null) {
            fullName = lastName + " " + firstName;
        }

        if (dormitory != null && rNumber != null) {
            roomNumber = dormitory + " " + rNumber;
        } else {
            roomNumber = null;
        }

        if (neptun != null) {
            personalUniqueCode = NEPTUN_PREFIX + neptun;
        } else {
            personalUniqueCode = null;
        }

        if (virId != null) {
            personalUniqueID = VIRID_PREFIX + virId;
        } else {
            personalUniqueID = null;
        }

        if (studentStatus != null) {
            studentUserStatus = STUDENTSTATUS_PREFIX + studentStatus;
        } else {
            studentUserStatus = null;
        }
    }
}
