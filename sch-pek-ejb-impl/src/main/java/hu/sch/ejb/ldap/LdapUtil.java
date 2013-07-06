package hu.sch.ejb.ldap;

import hu.sch.domain.profile.Person;
import hu.sch.domain.profile.StudentStatus;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for LDAP conversion between attributes and Person objects.
 *
 * @author tomi
 */
public class LdapUtil {

    public static final String NEPTUN_PREFIX =
            "urn:mace:terena.org:schac:personalUniqueCode:hu:BME-NEPTUN:";
    public static final String STUDENTSTATUS_PREFIX =
            "urn:mace:terena.org:schac:status:sch.hu:student_status:";
    public static final String VIRID_PREFIX =
            "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:";
    public static final String BASEDN = "ou=people,ou=sch,o=bme,c=hu";
    private static final String LDAP_SCHEME_DATE_FORMAT = "yyyyMMdd";

    private LdapUtil() {
    }

    public static String buildDN(String uid) {
        return String.format("uid=%s,%s", uid, BASEDN);
    }

    public static String buildVirId(String virId) {
        return VIRID_PREFIX.concat(virId);
    }

    public static String buildVirId(long virId) {
        return VIRID_PREFIX.concat(String.valueOf(virId));
    }

    public static String buildNeptun(String neptun) {
        return NEPTUN_PREFIX.concat(neptun);
    }

    public static String buildStudentStatus(StudentStatus status) {
        if (status == null) {
            return null;
        }
        return STUDENTSTATUS_PREFIX.concat(status.getStatus());
    }

    public static Date parseDateOfBirth(String value) throws ParseException {
        if (value == null) {
            return null;
        }
        return new SimpleDateFormat(LDAP_SCHEME_DATE_FORMAT).parse(value);
    }

    public static String dateOfBirthToString(Date dateOfBirth) {
        if (dateOfBirth == null) {
            return null;
        }
        return new SimpleDateFormat(LDAP_SCHEME_DATE_FORMAT).format(dateOfBirth);
    }

    public static String getNeptun(String ldapNeptun) {
        return stripPrefix(NEPTUN_PREFIX, ldapNeptun);
    }

    public static Long getVirId(String ldapVirId) {
        if (ldapVirId == null) {
            return null;
        }
        return Long.parseLong(stripPrefix(VIRID_PREFIX, ldapVirId));
    }

    public static StudentStatus getStudentStatus(String ldapStatus) {
        if (ldapStatus == null) {
            return null;
        }
        return StudentStatus.fromString(stripPrefix(STUDENTSTATUS_PREFIX, ldapStatus));
    }

    /**
     * Tries to parse the given string in the format of <Dormitory> <RoomNumber>.
     *
     * If the 'value' is null, the person's dormitory and roomnumber attriubtes
     * will be set null.
     *
     * @param value value read from ldap
     * @param target the person to update
     */
    public static void parseAndSetRoomNumber(String value, Person target) {
        if (value == null) {
            // explicitly setting values to null, to be predictable based on the
            // method's name
            target.setDormitory(null);
            target.setRoomNumber(null);
            return;
        }
        Pattern p = Pattern.compile("^(.*)\\s([a-zA-Z]{0,1}[0-9]+)$");
        Matcher m = p.matcher(value);

        if (m.matches()) {
            target.setDormitory(m.group(1));
            target.setRoomNumber(m.group(2));
        }
    }

    private static String stripPrefix(String prefix, String value) {
        if (value == null) {
            return null;
        }
        return value.replace(prefix, "");
    }
}
