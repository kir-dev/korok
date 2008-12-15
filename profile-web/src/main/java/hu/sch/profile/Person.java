/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author konvergal
 */
public class Person implements Serializable {
    /* 
     * Cimtar megfelelo: uid.
     */
    private String uid;
    
    /*
     * Cimtar megfelelo displayName:
     */
    private String nickName;

    /* 
     * Cimtar megfelelo: givenName.
     */
    private String firstName;

    /* 
     * Cimtar megfelelo: sn.
     */
    private String lastName;
    
    /*
     * Cimtar megfelelo: cn.
     */
    private String fullName;

    /* 
     * Cimtar megfelelo: schacUserPresenceID.
     */
    private String IM;
    
    /*
     * Cimtar megfelelo: schacPersonalUniqueID.
     */
    private String personalUniqueID;

    /*
     * Cimtar megfelelo: nincs!
     * A regi VIR adatbazisban levo user id-ja.
     * Az schacPersonalUniqueId ldap attributum virID resze.
     */ 
    private String virId;
    
    /* 
     * Cimtar megfelelo: schacPersonalUniqueCode.
     */
    private String personalUniqueCode;

    /* 
     * Cimtar megfelelo: nincs!
     * Az schacPersonalUniqueCode ldap attributum neptun kod resze.
     */
    private String neptun;

    /* 
     * Cimtar megfelelo: mail.
     */
    private String mail;

    /* 
     * Cimtar megfelelo: mobile.
     */
    private String mobile;

    /* 
     * Cimtar megfelelo: homePhone.
     */
    private String homePhone;

    /*
     * Cimtar megfelelo: nincs!
     * Kenyelmes editalo form keszitesehez szukseges.
     * Ugyelni kell a konzisztenciajara!
     */
    private String dormitory;
    
    /*
     * Szobaszam.
     * Cimtar megfelelo: nincs!
     * Kenyelmes editalo form keszitesehez szukseges.
     * Ugyelni kell a konzisztenciajara!
     */
    private String rNumber;
    
    /* 
     * Cimtar megfelelo: roomNumber.
     * Nem csak a szobaszamot tarolja, hanem a kollegiumot is!
     * Formatum: <Kollegium> <Szobaszam>
     */
    private String roomNumber;

    /* 
     * Cimtar megfelelo: homePostalAddress.
     */
    private String homePostalAddress;

    /* 
     * Cimtar megfelelo: labeledURI.
     */
    private String webpage;

    /* 
     * Cimtar megfelelo: schacGender.
     * ISO-5218 szerint: 0-nem ismert, 1-ferfi, 2-no, 9-nem specifikalt.
     */
    private String gender;

    /* 
     * Cimtar megfelelo: schacDateOfBirth.
     * RFC-3339 "YYYYMMDD" formatumban.
     */
    private String dateOfBirth;

    /* 
     * Cimtar megfelelo: schacUserStatus.
     * 'akt': urn:mace:terena.org:schac:status:sch.hu:student_status:active
     * 'egy': urn:mace:terena.org:schac:status:sch.hu:student_status:other
     * 'veg': urn:mace:terena.org:schac:status:sch.hu:student_status:graduated 
     */
    private String studentUserStatus;
    
    /*
     * Cimtar megfelelo: nincs!
     * Az schacUserStatus veget tarolja.
     */
    private String studentStatus;

    /* 
     * Cimtar megfelelo: inetUserStatus.
     * active / inactive
     */
    private String status;
    
    private String[] schacPrivateAttribute = {};
    
    private List<String> privateAttributes = new ArrayList<String>();
    
    private byte[] photo;
    
    private List<Membership> activeMemberships = new ArrayList<Membership>();
    
    private List<Membership> inactiveMemberships = new ArrayList<Membership>();
    
    private List<String> eduPersonEntitlement = new ArrayList<String>();
    
    private List<Entitlement> entitlements = new ArrayList<Entitlement>();

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
        
    public void setVirId(String virId) {
        this.virId = virId;
    }
    
    public String getVirId() {
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

    public String getIM() {
        return IM;
    }

    public void setIM(String IM) {
        this.IM = IM;
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
    
    public List<String> getEduPersonEntitlement() {
        return eduPersonEntitlement;
    }
    
    public void setEduPersonEntitlement(List<String> eduPersonEntitlement) {
        this.eduPersonEntitlement = eduPersonEntitlement;
    }

    public void setStatus(String status) {
        this.status = status;
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
        }
        else {
            privateAttributes.add(attribute);
        }
    }

    public List<Membership> getActiveMemberships() {
        return activeMemberships;
    }

    public void setActiveMemberships(List<Membership> activeMemberships) {
        this.activeMemberships = activeMemberships;
    }

    public List<Membership> getInactiveMemberships() {
        return inactiveMemberships;
    }

    public void setInactiveMemberships(List<Membership> inactiveMemberships) {
        this.inactiveMemberships = inactiveMemberships;
    }
    
    public void loadMemberships(List<String> strMemberships) {
        //csoporttagsag string: Csoportnév|Státusz|kezdés|vég
        
        for (String s : strMemberships) {
            StringTokenizer tok = new StringTokenizer(s, "|");
            String csopnev = tok.nextToken();
            String tagStatusz = tok.nextToken();
            Membership m = new Membership(csopnev, tagStatusz);
            if ("Öregtag".equals(tagStatusz)) {
                getInactiveMemberships().add(m);
            } else {
                getActiveMemberships().add(m);
            }
        }
    }
    
    public void setToUse() {
        this.privateAttributes.addAll(new ArrayList<String>(Arrays.asList(schacPrivateAttribute)));
        
        if (personalUniqueCode != null) {
            String[] personalUniqueCodeArray = personalUniqueCode.split(":");
            this.neptun = personalUniqueCodeArray[personalUniqueCodeArray.length - 1];
        }
        
        if (personalUniqueID != null) {
            String[] personalUniqueIDArray = personalUniqueID.split(":");
            this.virId = personalUniqueIDArray[personalUniqueIDArray.length - 1];
        }
        
        if (studentUserStatus != null) {
            String[] studentUserStatusArray = studentUserStatus.split(":");
            this.studentStatus = studentUserStatusArray[studentUserStatusArray.length - 1];
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
        this.schacPrivateAttribute = privateAttributes.toArray(new String[0]);

        if (lastName != null && firstName != null) {
            fullName = lastName + " " + firstName;
        }
        
        if (dormitory != null && rNumber != null) {
            roomNumber = dormitory + " " + rNumber;
        }
        else {
            roomNumber = null;
        }
        
        if (neptun != null) {
            personalUniqueCode = "urn:mace:terena.org:schac:personalUniqueCode:hu:BME-NEPTUN:" + neptun;
        }
        else {
            personalUniqueCode = null;
        }
        
        if (virId != null) {
            personalUniqueID = "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:" + virId;
        }
        else {
            personalUniqueID = null;
        }
        
        if (studentStatus != null) {
            studentUserStatus = "urn:mace:terena.org:schac:status:sch.hu:student_status:" + studentStatus;
        }
        else {
            studentUserStatus = null;
        }
    }
    
    public List<Entitlement> getEntitlements() {
        for (String entitle : eduPersonEntitlement) {
            String[] entitleArray = entitle.split(":");
            entitlements.add(new Entitlement(EntitlementType.get(entitleArray[4]), LDAPGroupManager.getInstance().getGroupByCN(entitleArray[5])));
        }
        
        return entitlements;
    }
    
    public List<Entitlement> loadEntitlements() {
        return getEntitlements();
    }
    
    public void addEntitlement(Entitlement entitlement) {
        this.entitlements.add(entitlement);
        
        this.eduPersonEntitlement.add(new String("urn:mace:sch.hu:entitlement:"+entitlement.getEntitlementType().getLdapName()+":"+entitlement.getGroup().getGroupName()));
    }

    public boolean hasEntitlementType(EntitlementType entitlementType) {
        for (Entitlement e : entitlements) {
            if (e.entitlementType.equals(entitlementType)) {
                return true;
            }
        }
        
        return false;
    }

    public boolean hasEntitlement(Entitlement entitlement) {
        for (Entitlement e: entitlements) {
            if (e.equals(entitlement)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean entitled(Entitlement entitlement) {
        return hasEntitlement(entitlement);
    }
    
    public List<Entitlement> getEntitlementsByEntitlementType(EntitlementType entitlementType) {
        List<Entitlement> entList = new ArrayList<Entitlement>();
        
        for (Entitlement e: entitlements) {
            if (e.entitlementType.equals(entitlementType)) {
                entList.add(e);
            }
        }
        
        return entList;
    }
    
    public List<Group> getGroupsByEntitlementType(EntitlementType entitlementType) {
        List<Group> groupList = new ArrayList<Group>();
        
        for (Entitlement e: entitlements) {
            if (e.entitlementType.equals(entitlementType)) {
                groupList.add(e.getGroup());
            }
        }
        
        return groupList;
    }
}
