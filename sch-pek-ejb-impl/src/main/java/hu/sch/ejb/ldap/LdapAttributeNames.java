package hu.sch.ejb.ldap;

/**
 *
 * @author tomi
 */
public enum LdapAttributeNames {

    /**
     * User's chosen username.
     */
    SCREENNAME("uid"),
    /**
     * Full name.
     *
     * In normal (Hungarian) order.
     */
    FULLNAME("cn"),
    EMAIL("mail"),
    /**
     * User status: active or inactive.
     */
    STATUS("inetUserStatus"),
    PASSWORD("userPassword"),
    /**
     * LDAP specific stuff.
     */
    OBJECTCLASS("objectClass");

    private String name;

    private LdapAttributeNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
