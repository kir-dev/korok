/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.ejb;

import hu.sch.domain.config.Configuration;
import hu.sch.domain.profile.IMAccount;
import hu.sch.domain.profile.IMProtocol;
import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.services.exceptions.InvalidPasswordException;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.domain.util.PatternHolder;
import hu.sch.ejb.util.I18nFilter;
import hu.sch.services.MailManagerLocal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.ldap.filter.OrFilter;

/**
 * Az Ldap-ban levo emberekhez tartozo bejegyzeseket kezelo Session Bean.
 *
 * @author aldaris
 */
@Stateless
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
    /**
     * Bean objektum, letrehozasakor jon letre maga az ldapTemplate.
     */
    private static volatile LdapManagerBean INSTANCE = null;
    /**
     * Segitsegevel allitjuk elo az xml fajlbol az Ldap-ot kezelo objektumot.
     */
    private static BeanFactory springBeanFactory;
    /**
     * Ez az objektum kezeli az osszes Ldap-ban levo adat elereset, modositasat, mindent.
     */
    private static LdapTemplate ldapTemplate;
    /**
     * Levélküldéshez szükséges EJB referencia
     */
    @EJB(name = "MailManagerBean")
    private MailManagerLocal mailManager;

    /**
     * Deklaratív jogkezeléshez
     */
    @Resource
    private SessionContext ctx;
    /**
     * Bean inicializáló metódus, ez hozza létre igazából a Spring Beant.
     */
    @PostConstruct
    private void init() {
        if (INSTANCE == null) {
            FileSystemResource resource =
                    new FileSystemResource(Configuration.getSpringLdapPath());
            LdapManagerBean.springBeanFactory =
                    new XmlBeanFactory(resource);
            INSTANCE =
                    (LdapManagerBean) LdapManagerBean.springBeanFactory.getBean("ldapContact");
        }
    }

    /**
     * Callback fuggveny, az INSTANCE letrehozasa utan hivodik meg, beallitva ezzel az ldapTemplate-et.
     * @param newLdapTemplate Az ldapTemplate amely segitsegevel tudjuk adatainkat kezelni.
     */
    public void setLdapTemplate(LdapTemplate newLdapTemplate) {
        ldapTemplate = newLdapTemplate;
    }

    private static class PersonContextMapper extends AbstractContextMapper {

        @Override
        public Object doMapFromContext(DirContextOperations ctx) {
            Person person = new Person();
            person.setUid(ctx.getStringAttribute("uid"));
            person.setLastName(ctx.getStringAttribute("sn"));
            person.setFirstName(ctx.getStringAttribute("givenName"));
            person.setFullName(ctx.getStringAttribute("cn"));
            person.setNickName(ctx.getStringAttribute("displayName"));
            person.setMail(ctx.getStringAttribute("mail"));
            person.setMobile(ctx.getStringAttribute("mobile"));
            person.setHomePhone(ctx.getStringAttribute("homePhone"));
            person.setRoomNumber(ctx.getStringAttribute("roomNumber"));
            person.setHomePostalAddress(ctx.getStringAttribute("homePostalAddress"));
            person.setWebpage(ctx.getStringAttribute("labeledURI"));
            person.setGender(ctx.getStringAttribute("schacGender"));
            person.setMothersName(ctx.getStringAttribute("sch-vir-mothersName"));
            person.setEstimatedGraduationYear(ctx.getStringAttribute("sch-vir-estimatedGraduationYear"));
            person.setDateOfBirth(ctx.getStringAttribute("schacDateOfBirth"));
            person.setStatus(ctx.getStringAttribute("inetUserStatus"));
            person.setPhoto((byte[]) ctx.getObjectAttribute("jpegPhoto"));
            person.setConfirmationCode(ctx.getStringAttribute("sch-vir-confirmationCodes"));

            // im lista összeállítása
            List<IMAccount> ims = new ArrayList<IMAccount>();
            String[] im = ctx.getStringAttributes("schacUserPresenceID");
            if (im != null) {
                for (String presenceid : im) {
                    Matcher m = PatternHolder.IM_PATTERN.matcher(presenceid);
                    if (m.matches()) {
                        try {
                            // throws illegalargumentexception
                            ims.add(new IMAccount(
                                    IMProtocol.valueOf(m.group(1)),
                                    m.group(2)));
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
            if (ctx.getStringAttributes("schacUserPrivateAttribute") != null) {
                person.setSchacPrivateAttribute(ctx.getStringAttributes("schacUserPrivateAttribute"));
            }

            // Admin altal valtoztathato attributumok.
            person.setPersonalUniqueCode(ctx.getStringAttribute("schacPersonalUniqueCode"));
            person.setPersonalUniqueID(ctx.getStringAttribute("schacPersonalUniqueID"));
            person.setStudentUserStatus(ctx.getStringAttribute("schacUserStatus"));

            person.setToUse();
            return person;
        }
    }

    private static class PersonForSearchContextMapper extends AbstractContextMapper {

        @Override
        public Object doMapFromContext(DirContextOperations ctx) {
            Person person = new Person();
            person.setUid(ctx.getStringAttribute("uid"));
            person.setFullName(ctx.getStringAttribute("cn"));
            person.setNickName(ctx.getStringAttribute("displayName"));
            person.setRoomNumber(ctx.getStringAttribute("roomNumber"));
            person.setDateOfBirth(ctx.getStringAttribute("schacDateOfBirth"));

            person.setToUse();
            return person;
        }
    }

    protected void mapToContext(Person p, DirContextOperations ctx) {
        p.setToSave();

        ctx.setAttributeValue("sn", p.getLastName());
        ctx.setAttributeValue("givenName", p.getFirstName());
        ctx.setAttributeValue("displayName", p.getNickName());
        ctx.setAttributeValue("cn", p.getLastName() + " " + p.getFirstName());
        ctx.setAttributeValue("mail", p.getMail());
        ctx.setAttributeValue("mobile", p.getMobile());
        ctx.setAttributeValue("homePhone", p.getHomePhone());
        ctx.setAttributeValue("roomNumber", p.getRoomNumber());
        ctx.setAttributeValue("homePostalAddress", p.getHomePostalAddress());
        ctx.setAttributeValue("labeledURI", p.getWebpage());
        ctx.setAttributeValue("schacDateOfBirth", p.getDateOfBirth());
        ctx.setAttributeValue("schacGender", p.getGender());
        ctx.setAttributeValue("sch-vir-mothersName", p.getMothersName());
        ctx.setAttributeValue("sch-vir-estimatedGraduationYear", p.getEstimatedGraduationYear());
        ctx.setAttributeValue("inetUserStatus", p.getStatus());
        ctx.setAttributeValue("jpegPhoto", p.getPhoto());
        ctx.setAttributeValue("sch-vir-confirmationCodes", p.getConfirmationCode());

        ctx.setAttributeValues("schacUserPrivateAttribute", p.getSchacPrivateAttribute());

        List<String> attrs = Arrays.asList(ctx.getStringAttributes("objectClass"));
        if (!attrs.contains("schacEntryConfidentiality")) {
            ctx.addAttributeValue("objectClass", "schacEntryConfidentiality");
        }
        if (!attrs.contains("sch-vir")) {
            ctx.addAttributeValue("objectClass", "sch-vir");
        }

        Iterator<IMAccount> iterator = p.getIMAccounts().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPresenceID() == null) {
                iterator.remove();
            }
        }
        String[] ims = new String[p.getIMAccounts().size()];
        for (int i = 0; i < p.getIMAccounts().size(); i++) {
            ims[i] = p.getIMAccounts().get(i).toString();
        }
        ctx.setAttributeValues("schacUserPresenceID", ims);

        // Admin altal valtoztathato attributumok.
        ctx.setAttributeValue("schacPersonalUniqueCode", p.getPersonalUniqueCode());
        ctx.setAttributeValue("schacPersonalUniqueID", p.getPersonalUniqueID());
        ctx.setAttributeValue("schacUserStatus", p.getStudentUserStatus());
    }

    protected Name buildDn(String uid) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("uid", uid);
        return dn;
    }

    protected ContextMapper getContextMapper() {
        return new PersonContextMapper();
    }

    @RolesAllowed("ADMIN")
    @Override
    public void deletePersonByUid(String uid) throws PersonNotFoundException {
        // Tenyleg letezik-e a user.
        getPersonByUid(uid);

        // User torlese.
        Name dn = buildDn(uid);
        ldapTemplate.unbind(dn);
    }

    @Override
    public Person getPersonByUid(String uid) throws PersonNotFoundException {
        Person p = null;
        try {
            p = (Person) ldapTemplate.lookup(buildDn(uid), getContextMapper());
        } catch (NameNotFoundException nnfe) {
            logger.error("Nincs ilyen UID! -- ", nnfe);
        } catch (Exception e) {
            logger.error("Nem sikerült lekérni UID alapján a személyt! -- ", e);
        }

        if (p == null) {
            throw new PersonNotFoundException();
        }

        return p;
    }

    @Override
    public Person getPersonByVirId(String virId) throws PersonNotFoundException {
        EqualsFilter equalsFilter = new EqualsFilter("schacPersonalUniqueID", "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:" + virId);

        List<Person> searchResult = ldapTemplate.search("", equalsFilter.encode(), getContextMapper());

        if (searchResult.isEmpty()) {
            throw new PersonNotFoundException();
        }
        return searchResult.get(0);
    }

    @Override
    public Person getPersonByNeptun(String neptun) throws PersonNotFoundException {
        EqualsFilter equalsFilter = new EqualsFilter("schacPersonalUniqueCode", "urn:mace:terena.org:schac:personalUniqueCode:hu:BME-NEPTUN:" + neptun);

        List<Person> searchResult = ldapTemplate.search("", equalsFilter.encode(), getContextMapper());

        if (searchResult.isEmpty()) {
            throw new PersonNotFoundException();
        }
        return searchResult.get(0);
    }

    @Override
    public void update(Person p) {
        Name dn = buildDn(p.getUid());
        DirContextOperations context = ldapTemplate.lookupContext(dn);
        mapToContext(p, context);
        ldapTemplate.modifyAttributes(context);
    }

    /**
    /*
    (cn = "*almafa*") or
    (displayName = "*almafa*") or
    (mail = "*almafa*" and schacUserPrivateAttribute != "mail") or
    (roomNumber = "*almafa*" and schacUserPrivateAttribute != "roomNumber")
     *
     * @param keyWord
     * @return
     */
    private AndFilter setUpAndFilter(String keyWord) {
        AndFilter andFilter = new AndFilter();

        for (String word : keyWord.split(" ")) {
            OrFilter orFilter = new OrFilter();
            orFilter.or(new I18nFilter("cn", "*" + word + "*"));
            orFilter.or(new I18nFilter("displayName", "*" + word + "*"));
            orFilter.or(new AndFilter().and(new NotFilter(
                    new EqualsFilter("schacUserPrivateAttribute", "mail"))).
                    and(new EqualsFilter("mail", word)));
            orFilter.or(new AndFilter().and(new NotFilter(
                    new EqualsFilter("schacUserPrivateAttribute", "roomNumber"))).
                    and(new I18nFilter("roomNumber", "*" + word + "*")));
            andFilter.and(orFilter);
        }
        andFilter.and(new EqualsFilter("objectclass", "person"));
        return andFilter;
    }

    @Override
    public List<Person> searchMyUid(String mail) {
        AndFilter andFilter = new AndFilter();
        andFilter.and(new EqualsFilter("mail", mail));
        andFilter.and(new EqualsFilter("inetUserStatus", "Active"));
        return ldapTemplate.search("", andFilter.encode(), getContextMapper());
    }

    @Override
    public List<Person> search(String keyWord) {
        AndFilter andFilter = setUpAndFilter(keyWord);
        if (!ctx.isCallerInRole("ADMIN")) {
            andFilter.and(new EqualsFilter("inetUserStatus", "active"));
        }
        return ldapTemplate.search("",
                andFilter.encode(), getSearchContextMapper());
    }

    @Override
    public List<Person> getPersonsWhoHasBirthday(String searchDate) {
        AndFilter andFilter = new AndFilter();
        andFilter.and(
                new NotFilter(new EqualsFilter("schacUserPrivateAttribute", "schacDateOfBirth"))).and(
                new LikeFilter("schacDateOfBirth", "*" + searchDate));
        return ldapTemplate.search("",
                andFilter.encode(), getSearchContextMapper());
    }

    @Override
    public List<Person> searchInactives() {
        EqualsFilter filter = new EqualsFilter("inetUserStatus", "Inactive");
        return ldapTemplate.search("", filter.encode(), getContextMapper());
    }

    @Override
    public void changePassword(String uid, String oldPassword, String newPassword)
            throws InvalidPasswordException {

        LdapContextSource authContext =
                (LdapContextSource) springBeanFactory.getBean("authContext");
        Name name = buildDn(uid);
        DistinguishedName dn = new DistinguishedName(
                authContext.getBaseLdapPath());
        dn.append(new DistinguishedName(name));

        authContext.setUserDn(dn.encode());
        authContext.setPassword(oldPassword);
        DirContext readWriteContext;
        try {
            authContext.afterPropertiesSet();
            readWriteContext = authContext.getReadWriteContext();
        } catch (Exception ex) {
            throw new InvalidPasswordException();
        }

        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                new BasicAttribute("userPassword", newPassword));
        try {
            readWriteContext.modifyAttributes(name, mods);
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected ContextMapper getSearchContextMapper() {
        return new PersonForSearchContextMapper();
    }

    @Override
    public void registerPerson(Person p, String password) {
        register(p, password, false);
    }

    @Override
    public void registerNewbie(Person p, String password) {
        register(p, password, true);
    }

    private void register(Person p, String password, boolean isNewbie) {
        boolean sendPass = (password == null);
        //az attribútumok formára hozása
        if (sendPass) {
            SecureRandom random = new SecureRandom();
            password = new BigInteger(45, random).toString(32);
        }

        bindPerson(p, password);
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

    //TODO: LDAP-attribútumnevek konstanssá átalakítása!!
    private void bindPerson(Person p, String password) {
        p.setToSave();
        try {
            Name dn = buildDn(p.getUid());
            Attributes attrs = new BasicAttributes();
            BasicAttribute ocattr = new BasicAttribute("objectclass");
            for (String oc : objectClasses) {
                ocattr.add(oc);
            }

            attrs.put(ocattr);
            addNotNullAttribute(attrs, "sn", p.getLastName());
            addNotNullAttribute(attrs, "givenName", p.getFirstName());
            addNotNullAttribute(attrs, "cn", p.getFullName());
            addNotNullAttribute(attrs, "mail", p.getMail());
            addNotNullAttribute(attrs, "schacUserStatus", p.getStudentUserStatus());
            addNotNullAttribute(attrs, "inetUserStatus", p.getStatus());
            addNotNullAttribute(attrs, "schacDateOfBirth", p.getDateOfBirth());
            addNotNullAttribute(attrs, "schacGender", p.getGender());
            addNotNullAttribute(attrs, "schacPersonalUniqueCode", p.getPersonalUniqueCode());
            addNotNullAttribute(attrs, "displayName", p.getNickName());
            addNotNullAttribute(attrs, "schacPersonalUniqueId", p.getPersonalUniqueID());
            addNotNullAttribute(attrs, "userPassword", password);

            ldapTemplate.bind(dn, null, attrs);
        } catch (Exception ex) {
            logger.error("Nem sikerült menteni a felhasználót", ex);
            throw new RuntimeException("nem sikerült létrehozni a felhasználót", ex);
        }
    }

    private void addNotNullAttribute(Attributes attrs, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            attrs.put(key, value);
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
}
