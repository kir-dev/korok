/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

    private final String NEPTUN_PREFIX =
            "urn:mace:terena.org:schac:personalUniqueCode:hu:BME-NEPTUN:";
    private final String STUDENTSTATUS_PREFIX =
            "urn:mace:terena.org:schac:status:sch.hu:student_status:";
    private final String VIRID_PREFIX =
            "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:";
    /**
     * Cimtar megfelelo: uid.
     */
    private String uid;
    /**
     * Cimtar megfelelo displayName:
     */
    private String nickName;
    /**
     * Cimtar megfelelo: givenName.
     */
    private String firstName;
    /**
     * Cimtar megfelelo: sn.
     */
    private String lastName;
    /**
     * Cimtar megfelelo: cn.
     */
    private String fullName;
    /**
     * Cimtar megfelelo: schacUserPresenceID.
     */
    private List<IMAccount> IMAccounts;
    /**
     * Cimtar megfelelo: schacPersonalUniqueID.
     */
    private String personalUniqueID;
    /**
     * Cimtar megfelelo: nincs!
     * A regi VIR adatbazisban levo user id-ja.
     * Az schacPersonalUniqueId ldap attributum virID resze.
     */
    private Long virId;
    /**
     * Cimtar megfelelo: schacPersonalUniqueCode.
     */
    private String personalUniqueCode;
    /**
     * Cimtar megfelelo: nincs!
     * Az schacPersonalUniqueCode ldap attributum neptun kod resze.
     */
    private String neptun;
    /**
     * Cimtar megfelelo: mail.
     */
    private String mail;
    /**
     * Cimtar megfelelo: mobile.
     */
    private String mobile;
    /**
     * Cimtar megfelelo: homePhone.
     */
    private String homePhone;
    /**
     * Cimtar megfelelo: nincs!
     * Kenyelmes editalo form keszitesehez szukseges.
     * Ugyelni kell a konzisztenciajara!
     */
    private String dormitory;
    /**
     * Szobaszam.
     * Cimtar megfelelo: nincs!
     * Kenyelmes editalo form keszitesehez szukseges.
     * Ugyelni kell a konzisztenciajara!
     */
    private String rNumber;
    /**
     * Cimtar megfelelo: roomNumber.
     * Nem csak a szobaszamot tarolja, hanem a kollegiumot is!
     * Formatum: <Kollegium> <Szobaszam>
     */
    private String roomNumber;
    /**
     * Cimtar megfelelo: homePostalAddress.
     */
    private String homePostalAddress;
    /**
     * Cimtar megfelelo: labeledURI.
     */
    private String webpage;
    /**
     * Cimtar megfelelo: schacGender.
     * ISO-5218 szerint: 0-nem ismert, 1-ferfi, 2-no, 9-nem specifikalt.
     */
    private String gender;
    /**
     * Cimtar megfelelo: schacDateOfBirth.
     * RFC-3339 "YYYYMMDD" formatumban.
     */
    private String dateOfBirth;
    /**
     * Cimtar megfelelo: schacUserStatus.
     * 'akt': urn:mace:terena.org:schac:status:sch.hu:student_status:active
     * 'egy': urn:mace:terena.org:schac:status:sch.hu:student_status:other
     * 'veg': urn:mace:terena.org:schac:status:sch.hu:student_status:graduated 
     */
    private String studentUserStatus;
    /**
     * Cimtar megfelelo: nincs!
     * Az schacUserStatus veget tarolja.
     */
    private String studentStatus;
    /**
     * Cimtar megfelelo: inetUserStatus.
     * active / inactive
     */
    private String status;
    private String[] schacPrivateAttribute = {};
    private List<String> privateAttributes = new ArrayList<String>();
    private byte[] photo;
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

    public Object[] getSchacPrivateAttribute() {
        return schacPrivateAttribute;
    }

    public void setSchacPrivateAttribute(String[] schacPrivateAttribute) {
        this.schacPrivateAttribute = schacPrivateAttribute;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    /*    public Object[] getPrivateAttributes() {
    return (Object[]) privateAttributes.toArray();
    }
    
    public void setPrivateAttributes(String[] privateAttributes) {
    this.privateAttributes.addAll(new ArrayList<String>(Arrays.asList(privateAttributes)));
    }*/
    public Boolean isPrivateAttribute(String attribute) {
        return privateAttributes.contains(attribute);
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void inversePrivateAttribute(String attribute) {
        if (isPrivateAttribute(attribute)) {
            privateAttributes.remove(attribute);
        } else {
            privateAttributes.add(attribute);
        }
    }

    public void setToUse() {
        this.privateAttributes.addAll(new ArrayList<String>(Arrays.asList(schacPrivateAttribute)));

        if (personalUniqueCode != null) {
            String[] personalUniqueCodeArray = personalUniqueCode.split(":");
            this.neptun =
                    personalUniqueCodeArray[personalUniqueCodeArray.length - 1];
        }

        if (personalUniqueID != null) {
            String[] personalUniqueIDArray = personalUniqueID.split(":");
            try {
                this.virId = Long.parseLong(
                        personalUniqueIDArray[personalUniqueIDArray.length - 1]);
            } catch (NumberFormatException ex) {
                //TODO log
            }
        }

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

    public void setToSave() {
        this.schacPrivateAttribute = privateAttributes.toArray(new String[privateAttributes.size()]);

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
