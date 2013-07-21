package hu.sch.ejb.ldap;

/**
 * Utility methods for LDAP conversion between attributes and Person objects.
 *
 * @author tomi
 */
public final class LdapUtil {

    public static final String BASEDN = "ou=people,ou=sch,o=bme,c=hu";

    private LdapUtil() {
    }

    public static String buildDN(String uid) {
        return String.format("uid=%s,%s", uid, BASEDN);
    }
}
