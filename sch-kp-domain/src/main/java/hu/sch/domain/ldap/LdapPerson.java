/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.domain.ldap;

import hu.sch.util.LazyList;

import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author aldaris
 */
public class LdapPerson {

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
     * Cimtar megfelelo: schacPersonalUniqueID.
     */
    private String personalUniqueID;
    /**
     * Cimtar megfelelo: nincs!
     * A regi VIR adatbazisban levo user id-ja.
     * Az schacPersonalUniqueId ldap attributum virID resze.
     */
    private String virId;
    /**
     * Cimtar megfelelo: schacPersonalUniqueCode.
     */
    private String personalUniqueCode;
    /**
     * Cimtar megfelelo: nincs!
     * Az schacPersonalUniqueCode ldap attributum neptun kod resze.
     */
    private String neptun;
    private List<String> eduPersonEntitlement = new LazyList<String>();
//    private List<Entitlement> entitlements = new LazyList<Entitlement>();
    private List<LdapMembership> activeMemberships = new LazyList<LdapMembership>();
    private List<LdapMembership> inactiveMemberships = new LazyList<LdapMembership>();

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

    public List<String> getEduPersonEntitlement() {
        return eduPersonEntitlement;
    }

    public void setEduPersonEntitlement(List<String> eduPersonEntitlement) {
        this.eduPersonEntitlement = eduPersonEntitlement;
    }

    public List<LdapMembership> getActiveMemberships() {
        return activeMemberships;
    }

    public void setActiveMemberships(List<LdapMembership> activeMemberships) {
        this.activeMemberships = activeMemberships;
    }

    public List<LdapMembership> getInactiveMemberships() {
        return inactiveMemberships;
    }

    public void setInactiveMemberships(List<LdapMembership> inactiveMemberships) {
        this.inactiveMemberships = inactiveMemberships;
    }

    public void loadMemberships(List<String> strMemberships) {
        //csoporttagsag string: Csoportnév|Státusz|kezdés|vég

        for (String s : strMemberships) {
            StringTokenizer tok = new StringTokenizer(s, "|");
            String csopnev = tok.nextToken();
            String tagStatusz = tok.nextToken();
            LdapMembership m = new LdapMembership(csopnev, tagStatusz);
            if ("Öregtag".equals(tagStatusz)) {
                getInactiveMemberships().add(m);
            } else {
                getActiveMemberships().add(m);
            }
        }
    }
    public void setToUse() {

        if (personalUniqueCode != null) {
            String[] personalUniqueCodeArray = personalUniqueCode.split(":");
            neptun = personalUniqueCodeArray[personalUniqueCodeArray.length - 1];
        }

        if (personalUniqueID != null) {
            String[] personalUniqueIDArray = personalUniqueID.split(":");
            virId = personalUniqueIDArray[personalUniqueIDArray.length - 1];
        }
    }

    public void setToSave() {

        if (neptun != null) {
            personalUniqueCode = "urn:mace:terena.org:schac:personalUniqueCode:hu:BME-NEPTUN:" + neptun;
        } else {
            personalUniqueCode = null;
        }

        if (virId != null) {
            personalUniqueID = "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:" + virId;
        } else {
            personalUniqueID = null;
        }

        //TODO: sch-vir-csoporttagsagokat elokesziteni
    }

//    public List<Entitlement> getEntitlements() {
//        for (String entitle : eduPersonEntitlement) {
//            String[] entitleArray = entitle.split(":");
//            entitlements.add(new Entitlement(EntitlementType.get(entitleArray[4]), LDAPGroupManager.getInstance().getGroupByCN(entitleArray[5])));
//        }
//
//        return entitlements;
//    }
//
//    public List<Entitlement> loadEntitlements() {
//        return getEntitlements();
//    }
//
//    public void addEntitlement(Entitlement entitlement) {
//        this.entitlements.add(entitlement);
//
//        this.eduPersonEntitlement.add(new String("urn:mace:sch.hu:entitlement:" + entitlement.getEntitlementType().getLdapName() + ":" + entitlement.getGroup().getGroupName()));
//    }
//
//    public boolean hasEntitlementType(EntitlementType entitlementType) {
//        for (Entitlement e : entitlements) {
//            if (e.entitlementType.equals(entitlementType)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public boolean hasEntitlement(Entitlement entitlement) {
//        for (Entitlement e : entitlements) {
//            if (e.equals(entitlement)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    public boolean entitled(Entitlement entitlement) {
//        return hasEntitlement(entitlement);
//    }
//
//    public List<Entitlement> getEntitlementsByEntitlementType(EntitlementType entitlementType) {
//        List<Entitlement> entList = new LazyList<Entitlement>();
//
//        for (Entitlement e : entitlements) {
//            if (e.entitlementType.equals(entitlementType)) {
//                entList.add(e);
//            }
//        }
//
//        return entList;
//    }
//
//    public List<Group> getGroupsByEntitlementType(EntitlementType entitlementType) {
//        List<Group> groupList = new LazyList<Group>();
//
//        for (Entitlement e : entitlements) {
//            if (e.entitlementType.equals(entitlementType)) {
//                groupList.add(e.getGroup());
//            }
//        }
//
//        return groupList;
//    }
}
