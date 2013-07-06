package hu.sch.ejb.ldap;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
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
    private static final int INITIAL_CONNECTIONS = 5;
    private static final int MAX_CONNECTIONS = 20;
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
        String dn = LdapUtil.buildDN(uid);
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
            SearchResultEntry e = ldapConnPool.getEntry(LdapUtil.buildDN(uid));
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
        return getPersonBy(LdapAttributeNames.VIRID, LdapUtil.buildVirId(virId));
    }

    @Override
    public Person getPersonByNeptun(String neptun) throws PersonNotFoundException {
        return getPersonBy(LdapAttributeNames.NEPTUN, LdapUtil.buildNeptun(neptun));
    }

    @Override
    public void update(Person p) {
        try {
            final String dn = LdapUtil.buildDN(p.getUid());

            SearchResultEntry entry = ldapConnPool.getEntry(dn);
            ModifyRequest req = new LdapPersonEntryMapper().buildModifyRequest(p, entry);

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

        List<Filter> conditions = new ArrayList<Filter>();

        for (String word : keyWord.split(" ")) {
            List<Filter> filters = new ArrayList<Filter>();

            // fullname (cn)
            filters.add(createI18NFilter(LdapAttributeNames.FULLNAME, word));

            // nickname
            filters.add(createI18NFilter(LdapAttributeNames.NICKNAME, word));

            // mail if not private
            filters.add(Filter.createANDFilter(
                    Filter.createNOTFilter(Filter.createEqualityFilter(LdapAttributeNames.PRIVATE.getName(), LdapAttributeNames.MAIL.getName())),
                    Filter.createEqualityFilter(LdapAttributeNames.MAIL.getName(), word)));

            // roomnumber if not private
            filters.add(Filter.createANDFilter(
                    Filter.createNOTFilter(Filter.createEqualityFilter(LdapAttributeNames.PRIVATE.getName(), LdapAttributeNames.ROOMNUMBER.getName())),
                    createI18NFilter(LdapAttributeNames.ROOMNUMBER, word)));

            conditions.add(Filter.createORFilter(filters));
        }

        if (!ctx.isCallerInRole("ADMIN")) {
            conditions.add(Filter.createEqualityFilter(LdapAttributeNames.STATUS.getName(), ACTIVE_STATUS));
        }

        return searchForPeople(Filter.createANDFilter(conditions));
    }

    @Override
    public List<Person> getPersonsWhoHasBirthday(String searchDate) {
        Filter filter = Filter.createANDFilter(
                Filter.createNOTFilter(
                Filter.createEqualityFilter(
                LdapAttributeNames.PRIVATE.getName(),
                LdapAttributeNames.DATE_OF_BIRTH.getName())),
                Filter.createSubstringFilter(LdapAttributeNames.DATE_OF_BIRTH.getName(), null, new String[]{searchDate}, null));

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

        PasswordModifyExtendedRequest req = new PasswordModifyExtendedRequest(LdapUtil.buildDN(uid), oldPassword, newPassword);
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
    //<editor-fold defaultstate="collapsed" desc="private methods">
    /**
     * Creates a filter which is insensitive to accents and acts as a 'like'
     * filter.
     *
     * @param attr attribute to query
     * @param assertValue value to match against
     * @return Filter containing the correct filter string
     */
    private Filter createI18NFilter(LdapAttributeNames attr, String assertValue) {
        return Filter.createExtensibleMatchFilter(attr.getName(), "1.3.6.1.4.1.42.2.27.9.4.88.1.6", false, "*" + assertValue + "*");
    }

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
        sb.append("/confirmationcode/").append(p.getConfirmationCode()).append("\n\n");

        if (sendPass) {
            sb.append("Felhasználói név: ").append(p.getUid()).append('\n');
            sb.append("Jelszó: ").append(password).append("\n\n");
        }

        sb.append("Üdvözlettel:\n");
        sb.append("Kir-Dev");

        mailManager.sendEmail(p.getMail(), "VIR Regisztráció", sb.toString());
    }

    private void storePerson(Person p, String password) {
        Entry entry = new LdapPersonEntryMapper().toEntry(p, password);

        try {
            ldapConnPool.add(entry);
        } catch (LDAPException ex) {
            logger.error("Failed to save new user.", ex);
            throw new RuntimeException("Failed to save new user.", ex);
        }
    }

    private Person createPersonFromLDAPEntry(SearchResultEntry e) {
        try {
            return new LdapPersonEntryMapper().toPerson(e);
        } catch (ParseException ex) {
            // TODO: exception handling in web layer.
            logger.error("Failed to parse date of birth.", ex);
        }

        return new Person();
    }

    private Person getPersonBy(LdapAttributeNames attr, String value) throws PersonNotFoundException {
        SearchRequest req = new SearchRequest(
                LdapUtil.BASEDN,
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
        SearchRequest req = new SearchRequest(LdapUtil.BASEDN, SearchScope.SUB, filter);

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
    //</editor-fold>
}