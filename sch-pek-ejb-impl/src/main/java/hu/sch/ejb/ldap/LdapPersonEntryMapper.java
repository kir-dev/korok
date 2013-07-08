package hu.sch.ejb.ldap;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import hu.sch.domain.user.Gender;
import hu.sch.domain.user.IMAccount;
import hu.sch.domain.user.IMProtocol;
import hu.sch.domain.profile.Person;
import hu.sch.domain.profile.UserStatus;
import hu.sch.domain.util.PatternHolder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
public class LdapPersonEntryMapper {

    private static final Logger logger = LoggerFactory.getLogger(LdapPersonEntryMapper.class);
    private static final String[] objectClasses = new String[]{
        "top", "schacLinkageIdentifiers", "sunAMAuthAccountLockout", "schacContactLocation",
        "person", "schacPersonalCharacteristics", "inetUser", "inetorgperson",
        "schacLinkageIdentifiers", "organizationalPerson", "schacEmployeeInfo", "sch-vir",
        "sunFMSAML2NameIdentifier", "top", "schacEntryConfidentiality", "eduPerson",
        "schacEntryMetadata", "schacUserEntitlements"
    };

    public LdapPersonEntryMapper() {
    }

    public Person toPerson(SearchResultEntry entry) throws ParseException {
        String[] privateAttr = getAttributes(entry, LdapAttributeNames.PRIVATE);

        Person person = null;
        if (privateAttr == null) {
            person = new Person();
        } else {
            person = new Person(Arrays.asList(privateAttr));
        }

        person.setUid(getAttribute(entry, LdapAttributeNames.UID));
        person.setLastName(getAttribute(entry, LdapAttributeNames.LASTNAME));
        person.setFirstName(getAttribute(entry, LdapAttributeNames.FIRSTNAME));
        person.setNickName(getAttribute(entry, LdapAttributeNames.NICKNAME));
        LdapUtil.parseAndSetRoomNumber(getAttribute(entry, LdapAttributeNames.ROOMNUMBER), person);
        person.setDateOfBirth(LdapUtil.parseDateOfBirth(getAttribute(entry, LdapAttributeNames.DATE_OF_BIRTH)));
        person.setMail(getAttribute(entry, LdapAttributeNames.MAIL));
        person.setMobile(getAttribute(entry, LdapAttributeNames.MOBILE));
        person.setHomePhone(getAttribute(entry, LdapAttributeNames.HOMEPHONE));
        person.setHomePostalAddress(getAttribute(entry, LdapAttributeNames.POSTALADDRESS));
        person.setWebpage(getAttribute(entry, LdapAttributeNames.WEBPAGE));
        person.setGender(Gender.fromString(getAttribute(entry, LdapAttributeNames.GENDER)));
        person.setMothersName(getAttribute(entry, LdapAttributeNames.MOTHERSNAME));
        person.setEstimatedGraduationYear(getAttribute(entry, LdapAttributeNames.ESTIMATED_GRAD_YEAR));
        person.setStatus(UserStatus.fromString(getAttribute(entry, LdapAttributeNames.STATUS)));
        person.setPhoto(entry.getAttributeValueBytes(LdapAttributeNames.PHOTO.getName()));
        person.setConfirmationCode(getAttribute(entry, LdapAttributeNames.CONFIRMATION_CODE));

        // im lista összeállítása
        List<IMAccount> ims = new ArrayList<IMAccount>();
        String[] im = getAttributes(entry, LdapAttributeNames.IM);
        if (im != null) {
            for (String presenceid : im) {
                Matcher m = PatternHolder.IM_PATTERN.matcher(presenceid);
                if (m.matches()) {
                    try {
                        // throws illegalargumentexception
                        ims.add(new IMAccount(IMProtocol.valueOf(m.group(1)), m.group(2)));
                    } catch (IllegalArgumentException e) {
                        logger.warn("Error while decoding schacUserPresenceID", e);
                    }
                } else {
                    logger.warn("schacUserPresenceID in invalid format!");
                }
            }
        }
        person.setIMAccounts(ims);

        // Admin altal valtoztathato attributumok.
        person.setNeptun(LdapUtil.getNeptun(getAttribute(entry, LdapAttributeNames.NEPTUN)));
        person.setVirId(LdapUtil.getVirId(getAttribute(entry, LdapAttributeNames.VIRID)));
        person.setStudentStatus(LdapUtil.getStudentStatus(getAttribute(entry, LdapAttributeNames.STUDENTSTATUS)));

        return person;
    }

    /**
     * Creates an LDAP entry from the given person object.
     *
     * It also generates a new confirmation code for the person.
     *
     * @param person the person object to save
     * @param password the password for the object
     * @return the entry that can be saved in LDAP store
     */
    public Entry toEntry(Person person, String password) {
        Entry entry = new Entry(LdapUtil.buildDN(person.getUid()));

        entry.addAttribute(LdapAttributeNames.OBJECTCLASS.getName(), objectClasses);

        addAttributeIfNotEmpty(entry, LdapAttributeNames.UID, person.getUid());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.LASTNAME, person.getLastName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.FIRSTNAME, person.getFirstName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.FULLNAME, person.getFullName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.MAIL, person.getMail());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.STUDENTSTATUS, person.getStudentStatus().getStatus());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.STATUS, person.getStatus().getStatus());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.DATE_OF_BIRTH, LdapUtil.dateOfBirthToString(person.getDateOfBirth()));
        addAttributeIfNotEmpty(entry, LdapAttributeNames.GENDER, person.getGender().getValueString());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.NEPTUN, LdapUtil.buildNeptun(person.getNeptun()));
        addAttributeIfNotEmpty(entry, LdapAttributeNames.NICKNAME, person.getNickName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.VIRID, LdapUtil.buildVirId(person.getVirId()));
        addAttributeIfNotEmpty(entry, LdapAttributeNames.PASSWORD, password);

        // NOTE: ez nem biztos hogy kell...
        person.generateAndSetConfirmationCode();
        addAttributeIfNotEmpty(entry, LdapAttributeNames.CONFIRMATION_CODE, person.getConfirmationCode());

        return entry;
    }

    public ModifyRequest buildModifyRequest(Person person, Entry entry) {
        List<Modification> mods = createModifications(person, entry);
        return new ModifyRequest(entry.getDN(), mods);
    }

    private void addAttributeIfNotEmpty(Entry entry, LdapAttributeNames attrname, String value) {
        if (value != null && !value.trim().isEmpty()) {
            entry.addAttribute(attrname.getName(), value);
        }
    }

    private String getAttribute(SearchResultEntry entry, LdapAttributeNames attr) {
        return entry.getAttributeValue(attr.getName());
    }

    private String[] getAttributes(SearchResultEntry entry, LdapAttributeNames attr) {
        return entry.getAttributeValues(attr.getName());
    }

    private List<Modification> createModifications(Person p, Entry e) {
        List<Modification> mods = new ArrayList<Modification>();

        mods.add(buildModification(LdapAttributeNames.LASTNAME, p.getLastName()));
        mods.add(buildModification(LdapAttributeNames.FIRSTNAME, p.getFirstName()));
        mods.add(buildModification(LdapAttributeNames.NICKNAME, p.getNickName()));
        mods.add(buildModification(LdapAttributeNames.FULLNAME, p.getFullName()));
        mods.add(buildModification(LdapAttributeNames.MAIL, p.getMail()));
        mods.add(buildModification(LdapAttributeNames.MOBILE, p.getMobile()));
        mods.add(buildModification(LdapAttributeNames.HOMEPHONE, p.getHomePhone()));
        mods.add(buildModification(LdapAttributeNames.ROOMNUMBER, p.getRoomNumber()));
        mods.add(buildModification(LdapAttributeNames.POSTALADDRESS, p.getHomePostalAddress()));
        mods.add(buildModification(LdapAttributeNames.WEBPAGE, p.getWebpage()));
        mods.add(buildModification(LdapAttributeNames.DATE_OF_BIRTH, LdapUtil.dateOfBirthToString(p.getDateOfBirth())));
        mods.add(buildModification(LdapAttributeNames.GENDER, p.getGender().getValueString()));
        mods.add(buildModification(LdapAttributeNames.MOTHERSNAME, p.getMothersName()));
        mods.add(buildModification(LdapAttributeNames.ESTIMATED_GRAD_YEAR, p.getEstimatedGraduationYear()));
        mods.add(buildModification(LdapAttributeNames.STATUS, p.getStatus().getStatus()));
        mods.add(buildModification(LdapAttributeNames.CONFIRMATION_CODE, p.getConfirmationCode()));

        mods.add(buildModification(LdapAttributeNames.PRIVATE, p.getPrivateAttributes()));

        // add missing objectClasses
        List<String> attrs = Arrays.asList(e.getAttributeValues(LdapAttributeNames.OBJECTCLASS.getName()));
        if (!attrs.contains("schacEntryConfidentiality")) {
            mods.add(new Modification(ModificationType.ADD, LdapAttributeNames.OBJECTCLASS.getName(), "schacEntryConfidentiality"));
        }
        if (!attrs.contains("sch-vir")) {
            mods.add(new Modification(ModificationType.ADD, LdapAttributeNames.OBJECTCLASS.getName(), "sch-vir"));
        }

        List<String> ims = new ArrayList<String>();
        for (IMAccount im : p.getIMAccounts()) {
            if (im.getPresenceID() == null) {
                continue;
            }

            ims.add(im.toString());
        }

        mods.add(buildModification(LdapAttributeNames.IM, ims.toArray(new String[ims.size()])));

        // Admin altal valtoztathato attributumok.
        mods.add(buildModification(LdapAttributeNames.NEPTUN, LdapUtil.buildNeptun(p.getNeptun())));
        mods.add(buildModification(LdapAttributeNames.VIRID, LdapUtil.buildVirId(p.getVirId())));
        mods.add(buildModification(LdapAttributeNames.STUDENTSTATUS, LdapUtil.buildStudentStatus(p.getStudentStatus())));

        return mods;
    }

    /**
     * Builds a modification for replacing the specified attribute's value.
     *
     * @param attrname the attribute to replace
     * @param value the new value
     */
    private Modification buildModification(LdapAttributeNames attrname, String value) {
        if (value == null) {
            return new Modification(ModificationType.REPLACE, attrname.getName());
        }
        return new Modification(ModificationType.REPLACE, attrname.getName(), value);
    }

    /**
     * Builds a modification for replacing the specified attribute's value.
     *
     * @param attrname the attribute to replace
     * @param values the new values
     */
    private Modification buildModification(LdapAttributeNames attrname, String[] values) {
        return new Modification(ModificationType.REPLACE, attrname.getName(), values);
    }
}
