package hu.sch.ejb.ldap;

import com.unboundid.ldap.sdk.SearchResultEntry;
import hu.sch.domain.profile.IMAccount;
import hu.sch.domain.profile.IMProtocol;
import hu.sch.domain.profile.Person;
import hu.sch.domain.util.PatternHolder;
import java.util.ArrayList;
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

    private SearchResultEntry entry;

    public LdapPersonEntryMapper(SearchResultEntry entry) {
        this.entry = entry;
    }

    public Person toPerson() {
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
    
    private String getAttribute(SearchResultEntry entry, LdapAttributeNames attr) {
        return entry.getAttributeValue(attr.getName());
    }
    
    private String[] getAttributes(SearchResultEntry entry, LdapAttributeNames attr) {
        return entry.getAttributeValues(attr.getName());
    }
}
