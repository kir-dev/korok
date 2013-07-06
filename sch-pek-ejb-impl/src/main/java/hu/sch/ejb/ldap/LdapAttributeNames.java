/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.ejb.ldap;

/**
 *
 * @author tomi
 */
public enum LdapAttributeNames {

    UID("uid"),
    FULLNAME("cn"),
    LASTNAME("sn"),
    FIRSTNAME("givenName"),
    NICKNAME("displayName"),
    ROOMNUMBER("roomNumber"),
    DATE_OF_BIRTH("schacDateOfBirth"),
    VIRID("schacPersonalUniqueID"),
    NEPTUN("schacPersonalUniqueCode"),
    MAIL("mail"),

    /**
     * Active or inactive. (inetUserStatus)
     */
    STATUS("inetUserStatus"),
    /**
     * Student status.
     */
    STUDENTSTATUS("schacUserStatus"),

    MOBILE("mobile"),
    HOMEPHONE("homeNumber"),
    POSTALADDRESS("homePostalAddress"),
    WEBPAGE("labeledURI"),
    GENDER("schacGender"),
    MOTHERSNAME("sch-vir-mothersName"),
    ESTIMATED_GRAD_YEAR("sch-vir-estimatedGraduationYear"),
    PHOTO("jpegPhoto"),
    CONFIRMATION_CODE("sch-vir-confirmationCodes"),
    IM("schacUserPresenceID"),
    PASSWORD("userPassword"),

    /**
     * Hidden (from displaying) user attributes.
     */
    PRIVATE("schacUserPrivateAttribute"),

    OBJECTCLASS("objectClass");

    private String name;

    private LdapAttributeNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
