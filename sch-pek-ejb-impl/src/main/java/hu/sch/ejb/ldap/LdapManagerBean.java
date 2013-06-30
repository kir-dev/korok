package hu.sch.ejb.ldap;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedRequest;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedResult;
import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.LdapConfig;
import hu.sch.domain.profile.IMAccount;
import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.exceptions.InvalidPasswordException;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.services.exceptions.LdapDeleteEntryFailedException;
import hu.sch.services.MailManagerLocal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Az Ldap-ban levo emberekhez tartozo bejegyzeseket kezelo Session Bean.
 *
 * @author aldaris
 * @author tomi
 */
@Singleton
@DeclareRoles("ADMIN")
@SuppressWarnings("unchecked")
public class LdapManagerBean implements LdapManagerLocal {

    /**
     * A logolashoz szukseges objektum.
     */
    private static final Logger logger = LoggerFactory.getLogger(LdapManagerBean.class);

    private static final String[] objectClasses = new String[]{
        "top", "schacLinkageIdentifiers", "sunAMAuthAccountLockout", "schacContactLocation",
        "person", "schacPersonalCharacteristics", "inetUser", "inetorgperson",
        "schacLinkageIdentifiers", "organizationalPerson", "schacEmployeeInfo", "sch-vir",
        "sunFMSAML2NameIdentifier", "top", "schacEntryConfidentiality", "eduPerson",
        "schacEntryMetadata", "schacUserEntitlements"
    };

    private static final int INITIAL_CONNECTIONS = 5;
    private static final int MAX_CONNECTIONS = 20;
    private static final String BASEDN = "ou=people,ou=sch,o=bme,c=hu";
    private static final String ACTIVE_STATUS = "Active";
    private static final String INACTIVE_STATUS = "Inactive";

    /**
     * Levélküldéshez szükséges EJB referencia
     */
    @EJB(name = "MailManagerBean")
    private MailManagerLocal mailManager;

    private LDAPConnectionPool ldapConnPool;

    /**
     * Deklaratív jogkezeléshez
     */
    @Resource
    private SessionContext ctx;

    /**
     * Initialize bean, opens LDAP connection.
     */
    @PostConstruct
    private void init() {
        LdapConfig config = Configuration.getLdapConfig();

        try {
            LDAPConnection conn = new LDAPConnection(config.getHost(), config.getPort(), config.getUser(), config.getPassword());
            ldapConnPool = new LDAPConnectionPool(conn, INITIAL_CONNECTIONS, MAX_CONNECTIONS);
        } catch (LDAPException ex) {
            logger.error("LDAP authentication failed.", ex);
        }
    }

    // <editor-fold desc="LdapManagerLocal members">

    @RolesAllowed("ADMIN")
    @Override
    public void deletePersonByUid(String uid) throws PersonNotFoundException, LdapDeleteEntryFailedException {
        // Tenyleg letezik-e a user.
        getPersonByUid(uid);

        // User torlese.
        String dn = buildDN(uid);
        try {
            ldapConnPool.delete(dn);
        } catch (LDAPException ex) {
            String msg = "Could not delete user with uid" + uid;
            logger.error(msg, ex);
            throw new LdapDeleteEntryFailedException(msg, ex);
        }
    }

    @Override
    public Person getPersonByUid(String uid) throws PersonNotFoundException {
        try {
            SearchResultEntry e = ldapConnPool.getEntry(buildDN(uid));
            if (e == null) {
                throw new PersonNotFoundException(uid);
            }

            return createPersonFromLDAPEntry(e);
        } catch (LDAPException ex) {
            logger.error("Could not reach LDAP server.", ex);
        }
        return null;
    }

    @Override
    public Person getPersonByVirId(String virId) throws PersonNotFoundException {
        return getPersonBy(LdapAttributeNames.VIRID, buildVirId(virId));
    }

    @Override
    public Person getPersonByNeptun(String neptun) throws PersonNotFoundException {
        return getPersonBy(LdapAttributeNames.NEPTUN, buildNeptun(neptun));
    }

    @Override
    public void update(Person p) {
        try {
            final String dn = buildDN(p.getUid());

            SearchResultEntry entry = ldapConnPool.getEntry(dn);
            List<Modification> mods = createModifications(p, entry);
            ModifyRequest req = new ModifyRequest(dn, mods);

            ldapConnPool.modify(req);
        } catch (LDAPException ex) {
            logger.error(String.format("Modification of user (%s) failed", p.getUid()), ex);
        }
    }

    @Override
    public List<Person> searchMyUid(String mail) {
        Filter filter = Filter.createANDFilter(
                Filter.createEqualityFilter(LdapAttributeNames.MAIL.getName(), mail),
                Filter.createEqualityFilter(LdapAttributeNames.STATUS.getName(), ACTIVE_STATUS));
        return searchForPeople(filter);
    }

    @Override
    public List<Person> search(String keyWord) {
        /*
         * (cn = "*almafa*") or
         * (displayName = "*almafa*") or
         * (mail = "*almafa*" and schacUserPrivateAttribute != "mail") or
         * (roomNumber = "*almafa*" and schacUserPrivateAttribute != "roomNumber")
         */

//        AndFilter andFilter = new AndFilter();
//
//        for (String word : keyWord.split(" ")) {
//            OrFilter orFilter = new OrFilter();
//            orFilter.or(new I18nFilter("cn", "*" + word + "*"));
//            orFilter.or(new I18nFilter("displayName", "*" + word + "*"));
//            orFilter.or(new AndFilter().and(new NotFilter(
//                    new EqualsFilter("schacUserPrivateAttribute", "mail"))).
//                    and(new EqualsFilter("mail", word)));
//            orFilter.or(new AndFilter().and(new NotFilter(
//                    new EqualsFilter("schacUserPrivateAttribute", "roomNumber"))).
//                    and(new I18nFilter("roomNumber", "*" + word + "*")));
//            andFilter.and(orFilter);
//        }
//        andFilter.and(new EqualsFilter("objectclass", "person"));
//
//        List<Filter> fiterComponents = new ArrayList<Filter>();
//
//        for (String term : keyWord.split(" ")) {
//        }
//
//        Filter filter = Filter.createANDFilter(fiterComponents);
//
//        AndFilter andFilter = setUpAndFilter(keyWord);
//        if (!ctx.isCallerInRole("ADMIN")) {
//            andFilter.and(new EqualsFilter("inetUserStatus", "active"));
//        }
//        return ldapTemplate.search("",
//                andFilter.encode(), getSearchContextMapper());

        return Collections.emptyList();
    }

    @Override
    public List<Person> getPersonsWhoHasBirthday(String searchDate) {
        Filter filter = Filter.createANDFilter(
                Filter.createNOTFilter(
                Filter.createEqualityFilter(
                LdapAttributeNames.PRIVATE.getName(),
                LdapAttributeNames.DATE_OF_BIRHT.getName())),
                Filter.createApproximateMatchFilter(LdapAttributeNames.DATE_OF_BIRHT.getName(), searchDate));

        return searchForPeople(filter);
    }

    @Override
    public List<Person> searchInactives() {
        Filter filter = Filter.createEqualityFilter(
                LdapAttributeNames.STATUS.getName(), INACTIVE_STATUS);
        return searchForPeople(filter);
    }

    @Override
    public void changePassword(String uid, String oldPassword, String newPassword)
            throws InvalidPasswordException {

        // FIXME: lehet, hogy a password valami custom cucc és nem LDAP szabvány?
        PasswordModifyExtendedRequest req = new PasswordModifyExtendedRequest(buildDN(uid), oldPassword, newPassword);
        PasswordModifyExtendedResult result = null;
        try {
            result = (PasswordModifyExtendedResult) ldapConnPool.processExtendedOperation(req);
        } catch (LDAPException ex) {
            logger.error("An error occured while sending the request.", ex);
            return;
        }

        if (result.getResultCode() != ResultCode.SUCCESS) {
            throw new InvalidPasswordException();
        }
    }

    @Override
    public void registerPerson(Person p, String password) {
        register(p, password, false);
    }

    @Override
    public void registerNewbie(Person p, String password) {
        register(p, password, true);
    }

    // </editor-fold>

    private void register(Person p, String password, boolean isNewbie) {
        boolean sendPass = (password == null);
        //az attribútumok formára hozása
        if (sendPass) {
            SecureRandom random = new SecureRandom();
            password = new BigInteger(45, random).toString(32);
        }

        storePerson(p, password);
        StringBuilder sb = new StringBuilder(300);
        if (isNewbie) {
            sb.append("Tisztelt leendő VIR felhasználó!\n\n");
            sb.append("Azért kapja ezt a levelet, mert Ön, vagy valaki a nevében regisztrált ");
            sb.append("a Villanykari Információs Rendszerbe.\n");
            sb.append("Ha nem Ön volt az, akkor ezt a levelet nyugodtan törölheti, ellenkező ");
            sb.append("esetben meg kell erősítenie a regisztrációját. Ehhez nem kell mást tennie, mint ");
            sb.append("egy böngészőbe beírni az alábbi URL-t: ");
        } else {
            sb.append("Kedves leendő VIR felhasználó!\n\n");
            sb.append("Azért kapod ezt a levelet, mert Te, vagy valaki a nevedben regisztrált ");
            sb.append("a Villanykari Információs Rendszerbe.\n");
            sb.append("Ha nem Te voltál az, akkor ezt a levelet nyugodtan törölheted, ellenkező ");
            sb.append("esetben meg kell erősítened a regisztrációdat. Ehhez nem kell mást tenned, mint ");
            sb.append("egy böngészőbe beírni az alábbi URL-t: ");
        }
        sb.append("https://korok.sch.bme.hu/korok/confirm/uid/").append(p.getUid());
        sb.append("/confirmationcode/").append(getConfirmationCode(p)).append("\n\n");

        if (sendPass) {
            sb.append("Felhasználói név: ").append(p.getUid()).append('\n');
            sb.append("Jelszó: ").append(password).append("\n\n");
        }

        sb.append("Üdvözlettel:\n");
        sb.append("Kir-Dev");

        mailManager.sendEmail(p.getMail(), "VIR Regisztráció", sb.toString());
    }

    private void storePerson(Person p, String password) {
        p.setToSave();
        Entry entry = new Entry(buildDN(p.getUid()));

        entry.addAttribute(LdapAttributeNames.OBJECTCLASS.getName(), objectClasses);

        addAttributeIfNotEmpty(entry, LdapAttributeNames.LASTNAME, p.getLastName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.FIRSTNAME, p.getFirstName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.FULLNAME, p.getFullName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.MAIL, p.getMail());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.USERSTATUS, p.getStudentUserStatus());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.STATUS, p.getStatus());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.DATE_OF_BIRHT, p.getDateOfBirth());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.GENDER, p.getGender());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.NEPTUN, p.getPersonalUniqueCode());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.NICKNAME, p.getNickName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.VIRID, p.getPersonalUniqueID());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.PASSWORD, password);
        try {
            ldapConnPool.add(entry);
        } catch (LDAPException ex) {
            logger.error("Failed to save new user.", ex);
            throw new RuntimeException("Failed to save new user.", ex);
        }
    }

    private void addAttributeIfNotEmpty(Entry entry, LdapAttributeNames attrname, String value) {
        if (value != null && !value.trim().isEmpty()) {
            entry.addAttribute(attrname.getName(), value);
        }
    }

    private String getConfirmationCode(Person p) {
        try {
            String confirmationString = p.getUid() + p.getMail() + p.getFullName();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(confirmationString.getBytes(), 0, confirmationString.length());
            String confirmationStringMD5 = new BigInteger(1, m.digest()).toString(16);

            return confirmationStringMD5;
        } catch (NoSuchAlgorithmException ex) {
        }

        return null;
    }

    private String buildDN(String uid) {
        return String.format("uid=%s,%s", uid, BASEDN);
    }

    private Person createPersonFromLDAPEntry(SearchResultEntry e) {
        return new LdapPersonEntryMapper(e).toPerson();
    }

    private String buildVirId(String virId) {
        return Person.VIRID_PREFIX + virId;
    }

    private String buildNeptun(String neptun) {
        return Person.NEPTUN_PREFIX + neptun;
    }

    private Person getPersonBy(LdapAttributeNames attr, String value) throws PersonNotFoundException {
        SearchRequest req = new SearchRequest(
                BASEDN,
                SearchScope.SUB,
                Filter.createEqualityFilter(attr.getName(), value));

        try {
            SearchResultEntry e = ldapConnPool.searchForEntry(req);

            if (e == null) {
                throw new PersonNotFoundException(value);
            }

            return createPersonFromLDAPEntry(e);
        } catch (LDAPSearchException ex) {
            logger.error("There was a problem with the search request to the LDAP server.", ex);
        }

        return null;
    }

    private List<Person> searchForPeople(Filter filter) {
        SearchRequest req = new SearchRequest(BASEDN, SearchScope.SUB, filter);

        SearchResult result;
        try {
            result = ldapConnPool.search(req);
        } catch (LDAPSearchException ex) {
            logger.error("An error occured during LDAP search.", ex);
            return Collections.emptyList();
        }

        List<Person> people = new ArrayList<Person>(result.getEntryCount());

        for (SearchResultEntry entry : result.getSearchEntries()) {
            people.add(createPersonFromLDAPEntry(entry));
        }

        return people;
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
