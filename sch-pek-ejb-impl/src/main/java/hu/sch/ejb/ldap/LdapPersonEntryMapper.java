package hu.sch.ejb.ldap;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import hu.sch.domain.profile.IMAccount;
import hu.sch.domain.profile.IMProtocol;
import hu.sch.domain.profile.Person;
import hu.sch.domain.util.PatternHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tomi
 */
public class LdapPersonEntryMapper {

    private static final Logger logger = LoggerFactory.getLogger(LdapPersonEntryMapper.class);

    public LdapPersonEntryMapper() {
    }

    public Person toPerson(SearchResultEntry entry) {
        Person person = new Person();

        person.setUid(getAttribute(entry, LdapAttributeNames.UID));
        person.setLastName(getAttribute(entry, LdapAttributeNames.LASTNAME));
        person.setFirstName(getAttribute(entry, LdapAttributeNames.FIRSTNAME));
        person.setFullName(getAttribute(entry, LdapAttributeNames.FULLNAME));
        person.setNickName(getAttribute(entry, LdapAttributeNames.NICKNAME));
        person.setRoomNumber(getAttribute(entry, LdapAttributeNames.ROOMNUMBER));
        person.setDateOfBirth(getAttribute(entry, LdapAttributeNames.DATE_OF_BIRHT));
        person.setMail(getAttribute(entry, LdapAttributeNames.MAIL));
        person.setMobile(getAttribute(entry, LdapAttributeNames.MOBILE));
        person.setHomePhone(getAttribute(entry, LdapAttributeNames.HOMEPHONE));
        person.setHomePostalAddress(getAttribute(entry, LdapAttributeNames.POSTALADDRESS));
        person.setWebpage(getAttribute(entry, LdapAttributeNames.WEBPAGE));
        person.setGender(getAttribute(entry, LdapAttributeNames.GENDER));
        person.setMothersName(getAttribute(entry, LdapAttributeNames.MOTHERSNAME));
        person.setEstimatedGraduationYear(getAttribute(entry, LdapAttributeNames.ESTIMATED_GRAD_YEAR));
        person.setStatus(getAttribute(entry, LdapAttributeNames.STATUS));
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

        // A tobbi sima attributumnal nem kell vizsgalni a null-t, itt viszont igen.
        String[] privateAttr = getAttributes(entry, LdapAttributeNames.PRIVATE);
        if (privateAttr != null) {
            person.setSchacPrivateAttribute(privateAttr);
        }

        // Admin altal valtoztathato attributumok.
        person.setPersonalUniqueCode(getAttribute(entry, LdapAttributeNames.NEPTUN));
        person.setPersonalUniqueID(getAttribute(entry, LdapAttributeNames.VIRID));
        person.setStudentUserStatus(getAttribute(entry, LdapAttributeNames.USERSTATUS));

        person.setToUse();
        return person;
    }

    public ModifyRequest buildModifyRequest(Person person, Entry entry) {
        List<Modification> mods = createModifications(person, entry);
        return new ModifyRequest(entry.getDN(), mods);
    }

    private String getAttribute(SearchResultEntry entry, LdapAttributeNames attr) {
        return entry.getAttributeValue(attr.getName());
    }

    private String[] getAttributes(SearchResultEntry entry, LdapAttributeNames attr) {
        return entry.getAttributeValues(attr.getName());
    }

        private List<Modification> createModifications(Person p, Entry e) {
        List<Modification> mods = new ArrayList<Modification>();

        p.setToSave();

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
        mods.add(buildModification(LdapAttributeNames.DATE_OF_BIRHT, p.getDateOfBirth()));
        mods.add(buildModification(LdapAttributeNames.GENDER, p.getGender()));
        mods.add(buildModification(LdapAttributeNames.MOTHERSNAME, p.getMothersName()));
        mods.add(buildModification(LdapAttributeNames.ESTIMATED_GRAD_YEAR, p.getEstimatedGraduationYear()));
        mods.add(buildModification(LdapAttributeNames.STATUS, p.getStatus()));
        mods.add(buildModification(LdapAttributeNames.CONFIRMATION_CODE, p.getConfirmationCode()));

        mods.add(buildModification(LdapAttributeNames.PRIVATE, p.getSchacPrivateAttribute()));

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
        mods.add(buildModification(LdapAttributeNames.NEPTUN, p.getPersonalUniqueCode()));
        mods.add(buildModification(LdapAttributeNames.VIRID, p.getPersonalUniqueID()));
        mods.add(buildModification(LdapAttributeNames.USERSTATUS, p.getStudentUserStatus()));

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
