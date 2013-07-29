package hu.sch.ejb.ldap;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedRequest;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedResult;
import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.LdapConfig;
import hu.sch.domain.user.UserStatus;
import hu.sch.domain.user.User;
import hu.sch.services.exceptions.InvalidPasswordException;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.services.exceptions.PekErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Synchronizes relevant user modifications to directory service (DS) via LDAP.
 *
 * @author tomi
 */
public class LdapSynchronizer implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(LdapSynchronizer.class);

    // TODO: review and clean up after LDAP schema has been updated
    private static final String[] objectClasses = new String[]{
        "top", "schacLinkageIdentifiers", "sunAMAuthAccountLockout", "schacContactLocation",
        "person", "schacPersonalCharacteristics", "inetUser", "inetorgperson",
        "schacLinkageIdentifiers", "organizationalPerson", "schacEmployeeInfo", "sch-vir",
        "sunFMSAML2NameIdentifier", "top", "schacEntryConfidentiality", "eduPerson",
        "schacEntryMetadata", "schacUserEntitlements"
    };
    private LDAPConnection conn;

    public LdapSynchronizer() throws PekEJBException {
        LdapConfig config = Configuration.getLdapConfig();
        try {
            conn = new LDAPConnection(config.getHost(), config.getPort(), config.getUser(), config.getPassword());
        } catch (LDAPException ex) {
            logger.error("Could not connect to DS.", ex);
            throw new PekEJBException(PekErrorCode.LDAP_CONNECTION_FAILED);
        }
    }

    /**
     * Creates a new entry in DS for the user, using its attributes.
     *
     * @param user the user to register in DS
     * @param password user's chosen password
     * @throws InvalidPasswordException if the password do not meet the
     * requirements: it needs to be at least 6 characters long
     * @throws LDAPException if the ldap opeperation fails
     */
    public void createEntry(User user, String password) throws InvalidPasswordException, LDAPException {
        createEntry(user, password, UserStatus.INACTIVE);
    }

    public void createEntry(User user, String password, UserStatus status) throws InvalidPasswordException, LDAPException {
        validatePassword(password);

        Entry entry = new Entry(LdapUtil.buildDN(user.getScreenName()));

        entry.addAttribute(LdapAttributeNames.OBJECTCLASS.getName(), objectClasses);
        addAttributeIfNotEmpty(entry, LdapAttributeNames.SCREENNAME, user.getScreenName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.EMAIL, user.getEmailAddress());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.FULLNAME, user.getFullName());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.STATUS, status.getStatus());
        addAttributeIfNotEmpty(entry, LdapAttributeNames.PASSWORD, password);

        conn.add(entry);
    }

    /**
     * Pushes the user's attributes to the DS.
     *
     * @param user
     * @throws LDAPException
     */
    public void syncEntry(User user) throws PekEJBException {
        List<Modification> mods = new ArrayList<>();
        final String dn = LdapUtil.buildDN(user.getScreenName());

        mods.add(buildModification(LdapAttributeNames.FULLNAME, user.getFullName()));
        mods.add(buildModification(LdapAttributeNames.EMAIL, user.getEmailAddress()));

        ModifyRequest req = new ModifyRequest(dn, mods);
        try {
            conn.modify(req);
        } catch (LDAPException ex) {
            logger.error("Could not update DS for dn: {}", dn);
            throw new PekEJBException(PekErrorCode.LDAP_UPDATE_FAILED, ex);
        }
    }

    public void updateStatus(User user, UserStatus status) throws PekEJBException {
        ModifyRequest req = new ModifyRequest(
                LdapUtil.buildDN(user.getScreenName()),
                buildModification(LdapAttributeNames.STATUS, status.getStatus()));
        try {
            conn.modify(req);
        } catch (LDAPException ex) {
            logger.error("Could not update status.", ex);
            throw new PekEJBException(PekErrorCode.LDAP_UPDATE_FAILED, ex);
        }
    }

    /**
     * Changes the user's password.
     *
     * @param user
     * @param oldPassword
     * @param newPassword
     * @throws InvalidPasswordException
     * @throws LDAPException
     */
    public void changePassword(User user, String oldPassword, String newPassword)
            throws InvalidPasswordException, LDAPException {
        changePassword(user.getScreenName(), oldPassword, newPassword);
    }

    /**
     * Changes the user's password.
     *
     * @param screenName
     * @param oldPassword
     * @param newPassword
     * @throws InvalidPasswordException
     * @throws LDAPException
     */
    public void changePassword(String screenName, String oldPassword, String newPassword)
            throws InvalidPasswordException, LDAPException {

        validatePassword(newPassword);

        PasswordModifyExtendedRequest req =
                new PasswordModifyExtendedRequest(LdapUtil.buildDN(screenName), oldPassword, newPassword);

        PasswordModifyExtendedResult result =
                (PasswordModifyExtendedResult) conn.processExtendedOperation(req);

        if (result.getResultCode() != ResultCode.SUCCESS) {
            throw new InvalidPasswordException("Changing password failed.");
        }
    }

    private void addAttributeIfNotEmpty(Entry entry, LdapAttributeNames attrname, String value) {
        if (value != null && !value.trim().isEmpty()) {
            entry.addAttribute(attrname.getName(), value);
        }
    }

    private void validatePassword(String pwd) throws InvalidPasswordException {
        boolean hasError = pwd == null || pwd.isEmpty() || pwd.length() < 6;

        if (hasError) {
            throw new InvalidPasswordException("Password in not valid! It has to be at least 6 characters.");
        }
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

    @Override
    public void close() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }
}
