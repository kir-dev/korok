/**
 * Copyright (c) 2009-2010, Peter Major
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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
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
 *
 * @author aldaris
 */
@Stateless
/**
 * Az Ldap-ban levo emberekhez tartozo bejegyzeseket kezelo Session Bean.
 */
@SuppressWarnings("unchecked")
public class LdapManagerBean implements LdapManagerLocal {

    /**
     * A logolashoz szukseges objektum.
     */
    private static final Logger log = Logger.getLogger(LdapManagerBean.class);
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
    private static volatile LdapManagerBean INSTANCE;
    /**
     * Segitsegevel allitjuk elo az xml fajlbol az Ldap-ot kezelo objektumot.
     */
    private static BeanFactory springBeanFactory;
    /**
     * Ez az objektum kezeli az osszes Ldap-ban levo adat elereset, modositasat, mindent.
     */
    private static LdapTemplate ldapTemplate;
    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;

    /**
     * Callback fuggveny, az INSTANCE letrehozasa utan hivodik meg, beallitva ezzel az ldapTemplate-et.
     * @param newLdapTemplate Az ldapTemplate amely segitsegevel tudjuk adatainkat kezelni.
     */
    public void setLdapTemplate(LdapTemplate newLdapTemplate) {
        ldapTemplate = newLdapTemplate;
    }

    /**
     * Az ldapTemplate-et tudjuk vele lekerdezni.
     * @return Az ldapTemplate referenciajat kapjuk vissza.
     */
    LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    /**
     * Singleton-t megvalosito fuggveny, ez garantalja, hogy egyszer es csakis
     * egyszer jon letre az Ldap-kezelo objektum.
     */
    private static void tryCreateInstance() {
        synchronized (LdapManagerBean.class) {
            if (INSTANCE == null) {
                FileSystemResource resource =
                        new FileSystemResource(Configuration.getSpringLdapPath());
                LdapManagerBean.springBeanFactory =
                        new XmlBeanFactory(resource);
                INSTANCE =
                        (LdapManagerBean) LdapManagerBean.springBeanFactory.getBean("ldapContact");
                LdapManagerBean.class.getClassLoader();
            }
        }
    }

    /**
     * Inicializalo fuggveny, segitsegevel keszitjuk el az egyetlen darab Ldap-kezelo objektumunkat
     */
    @PostConstruct
    @Override
    public void initialization() {
        //mivel az EJB thread-safe, megtehetjuk ezt
        LdapManagerBean lmb = INSTANCE;
        if (lmb == null) {
            tryCreateInstance();
        }
    }

    private static class PersonContextMapper extends AbstractContextMapper {

        @Override
        public Object doMapFromContext(DirContextOperations context) {
            Person person = new Person();
            person.setUid(context.getStringAttribute("uid"));
            person.setLastName(context.getStringAttribute("sn"));
            person.setFirstName(context.getStringAttribute("givenName"));
            person.setFullName(context.getStringAttribute("cn"));
            person.setNickName(context.getStringAttribute("displayName"));
            person.setMail(context.getStringAttribute("mail"));
            person.setMobile(context.getStringAttribute("mobile"));
            person.setHomePhone(context.getStringAttribute("homePhone"));
            person.setRoomNumber(context.getStringAttribute("roomNumber"));
            person.setHomePostalAddress(context.getStringAttribute("homePostalAddress"));
            person.setWebpage(context.getStringAttribute("labeledURI"));
            person.setGender(context.getStringAttribute("schacGender"));
            person.setMothersName(context.getStringAttribute("sch-vir-mothersName"));
            person.setEstimatedGraduationYear(context.getStringAttribute("sch-vir-estimatedGraduationYear"));
            person.setDateOfBirth(context.getStringAttribute("schacDateOfBirth"));
            person.setStatus(context.getStringAttribute("inetUserStatus"));
            person.setPhoto((byte[]) context.getObjectAttribute("jpegPhoto"));
            person.setConfirmationCode(context.getStringAttribute("sch-vir-confirmationCodes"));

            // im lista összeállítása
            List<IMAccount> ims = new ArrayList<IMAccount>();
            person.setIMAccounts(ims);
            String[] im = context.getStringAttributes("schacUserPresenceID");
            if (im != null) {
                for (String presenceid : im) {
                    Matcher m = PatternHolder.IM_PATTERN.matcher(presenceid);
                    if (m.matches()) {
                        try {
                            // throws invalidargumentexception
                            ims.add(new IMAccount(
                                    IMProtocol.valueOf(m.group(1)),
                                    m.group(2)));
                        } catch (IllegalArgumentException e) {
                            //TODO WARNING
                        }
                    } else {
                        // TODO WARNING
                    }
                }
            }

            // A tobbi sima attributumnal nem kell vizsgalni a null-t, itt viszont igen.
            if (context.getStringAttributes("schacUserPrivateAttribute") != null) {
                //person.setPrivateAttributes(context.getStringAttributes("schacUserPrivateAttribute"));
                person.setSchacPrivateAttribute(context.getStringAttributes("schacUserPrivateAttribute"));
            }

            // Admin altal valtoztathato attributumok.
            person.setPersonalUniqueCode(context.getStringAttribute("schacPersonalUniqueCode"));
            person.setPersonalUniqueID(context.getStringAttribute("schacPersonalUniqueID"));
            person.setStudentUserStatus(context.getStringAttribute("schacUserStatus"));


            person.setToUse();
            return person;
        }
    }

    private static class PersonForSearchContextMapper extends AbstractContextMapper {

        @Override
        public Object doMapFromContext(DirContextOperations context) {
            Person person = new Person();
            person.setUid(context.getStringAttribute("uid"));
            person.setFullName(context.getStringAttribute("cn"));
            person.setNickName(context.getStringAttribute("displayName"));
            person.setRoomNumber(context.getStringAttribute("roomNumber"));
            person.setDateOfBirth(context.getStringAttribute("schacDateOfBirth"));

            person.setToUse();
            return person;
        }
    }

    protected void mapToContext(Person p, DirContextOperations context) {
        p.setToSave();

        context.setAttributeValue("sn", p.getLastName());
        context.setAttributeValue("givenName", p.getFirstName());
        context.setAttributeValue("displayName", p.getNickName());
        context.setAttributeValue("cn", p.getLastName() + " " + p.getFirstName());
        context.setAttributeValue("mail", p.getMail());
        context.setAttributeValue("mobile", p.getMobile());
        context.setAttributeValue("homePhone", p.getHomePhone());
        context.setAttributeValue("roomNumber", p.getRoomNumber());
        context.setAttributeValue("homePostalAddress", p.getHomePostalAddress());
        context.setAttributeValue("labeledURI", p.getWebpage());
        context.setAttributeValue("schacDateOfBirth", p.getDateOfBirth());
        context.setAttributeValue("schacGender", p.getGender());
        context.setAttributeValue("sch-vir-mothersName", p.getMothersName());
        context.setAttributeValue("sch-vir-estimatedGraduationYear", p.getEstimatedGraduationYear());
        context.setAttributeValue("inetUserStatus", p.getStatus());
        context.setAttributeValue("jpegPhoto", p.getPhoto());
        context.setAttributeValue("sch-vir-confirmationCodes", p.getConfirmationCode());

        context.setAttributeValues("schacUserPrivateAttribute", p.getSchacPrivateAttribute());

        List<String> attrs = Arrays.asList(context.getStringAttributes("objectClass"));
        if (!attrs.contains("schacEntryConfidentiality")) {
            context.addAttributeValue("objectClass", "schacEntryConfidentiality");
        }
        if (!attrs.contains("sch-vir")) {
            context.addAttributeValue("objectClass", "sch-vir");
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
        context.setAttributeValues("schacUserPresenceID", ims);

        // Admin altal valtoztathato attributumok.
        context.setAttributeValue("schacPersonalUniqueCode", p.getPersonalUniqueCode());
        context.setAttributeValue("schacPersonalUniqueID", p.getPersonalUniqueID());
        context.setAttributeValue("schacUserStatus", p.getStudentUserStatus());
    }

    protected Name buildDn(String uid) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("uid", uid);
        return dn;
    }

    protected ContextMapper getContextMapper() {
        return new PersonContextMapper();
    }

    @Override
    public void deletePersonByUid(String uid) throws PersonNotFoundException {
        // Tenyleg letezik-e a user.
        getPersonByUid(uid);

        // User torlese.
        Name dn = buildDn(uid);
        getLdapTemplate().unbind(dn);
    }

    @Override
    public Person getPersonByUid(String uid) throws PersonNotFoundException {
        Name dn = buildDn(uid);
        Person p = null;
        try {
            p = (Person) getLdapTemplate().lookup(dn, getContextMapper());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (p == null) {
            throw new PersonNotFoundException();
        }

        return p;
    }

    @Override
    public Person getPersonByVirId(String virId) throws PersonNotFoundException {
        EqualsFilter equalsFilter = new EqualsFilter("schacPersonalUniqueID", "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:" + virId);

        List<Person> searchResult = getLdapTemplate().search("", equalsFilter.encode(), getContextMapper());

        if (searchResult.isEmpty()) {
            throw new PersonNotFoundException();
        }
        return searchResult.get(0);
    }

    @Override
    public Person getPersonByNeptun(String neptun) throws PersonNotFoundException {
        EqualsFilter equalsFilter = new EqualsFilter("schacPersonalUniqueCode", "urn:mace:terena.org:schac:personalUniqueCode:hu:BME-NEPTUN:" + neptun);

        List<Person> searchResult = getLdapTemplate().search("", equalsFilter.encode(), getContextMapper());

        if (searchResult.isEmpty()) {
            throw new PersonNotFoundException();
        }
        return searchResult.get(0);
    }

    @Override
    public void update(Person p) {
        Name dn = buildDn(p.getUid());
        DirContextOperations context = getLdapTemplate().lookupContext(dn);
        mapToContext(p, context);
        getLdapTemplate().modifyAttributes(context);
    }

    private AndFilter setUpAndFilter(List<String> searchWords) {
        Iterator<String> liter = searchWords.iterator();

        AndFilter andFilter = new AndFilter();
        while (liter.hasNext()) {
            String s = liter.next();
            /*
            (cn = "*almafa*") or
            (displayName = "*almafa*") or
            (mail = "*almafa*" and schacUserPrivateAttribute != "mail") or
            (roomNumber = "*almafa*" and schacUserPrivateAttribute != "roomNumber")
             */

            OrFilter orFilter = new OrFilter();
            orFilter.or(new LikeFilter("cn", "*" + s + "*"));
            orFilter.or(new LikeFilter("displayName", "*" + s + "*"));
            orFilter.or(new AndFilter().and(new NotFilter(
                    new EqualsFilter("schacUserPrivateAttribute", "mail"))).
                    and(new EqualsFilter("mail", s)));
            //            orFilter.or(new EqualsFilter("mail", s));
            //            orFilter.or(new LikeFilter("roomNumber", "*" + s));
            orFilter.or(new AndFilter().and(new NotFilter(
                    new EqualsFilter("schacUserPrivateAttribute", "roomNumber"))).
                    and(new LikeFilter("roomNumber", "*"
                    + s + "*")));
            andFilter.and(orFilter);
        }
        andFilter.and(new EqualsFilter("objectclass", "person"));
        return andFilter;
    }

    @Override
    public List<Person> searchMyUid(String mail) {
        AndFilter andFilter = new AndFilter();
        if (mail != null) {
            andFilter.and(new EqualsFilter("mail", mail));
        }
        andFilter.and(new EqualsFilter("inetUserStatus", "Active"));
        return getLdapTemplate().search("", andFilter.encode(), getContextMapper());
    }

    @Override
    public List<Person> search(List<String> searchWords) {
        AndFilter andFilter = setUpAndFilter(searchWords);
        andFilter.and(new EqualsFilter("inetUserStatus", "active"));
        return getLdapTemplate().search("",
                andFilter.encode(), getSearchContextMapper());
    }

    @Override
    public List<Person> searchByAdmin(List<String> searchWords) {
        AndFilter andFilter = setUpAndFilter(searchWords);
        return getLdapTemplate().search("",
                andFilter.encode(), getSearchContextMapper());
    }

    @Override
    public List<Person> getPersonsWhoHasBirthday(String searchDate) {
        AndFilter andFilter = new AndFilter();
        andFilter.and(
                new NotFilter(new EqualsFilter("schacUserPrivateAttribute", "schacDateOfBirth"))).and(
                new LikeFilter("schacDateOfBirth", "*" + searchDate));
        return getLdapTemplate().search("",
                andFilter.encode(), getSearchContextMapper());
    }

    @Override
    public List<Person> searchInactives() {
        EqualsFilter filter = new EqualsFilter("inetUserStatus", "Inactive");
        return getLdapTemplate().search("", filter.encode(), getContextMapper());
    }

    @Override
    public List<Person> getPersonByDn(List<String> dnList) {
        List<Person> LDAPPersons = new ArrayList<Person>(dnList.size());
        for (String dnStr : dnList) {
            DistinguishedName dn = new DistinguishedName(dnStr);
            Name dnSuffix = dn.getSuffix(dn.size() - 1);
            try {
                LDAPPersons.add(
                        (Person) getLdapTemplate().lookup(dnSuffix, getContextMapper()));
            } catch (Throwable e) {
            }
        }

        return LDAPPersons;
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
        boolean sendPass = (password == null);
        //az attribútumok formára hozása
        if (sendPass) {
            SecureRandom random = new SecureRandom();
            password = new BigInteger(45, random).toString(32);
        }

        bindPerson(p, password);
        StringBuilder sb = new StringBuilder(300);
        sb.append("Kedves leendő VIR felhasználó!\n\n");
        sb.append("Azért kapod ezt a levelet, mert Te, vagy valaki a nevedben regisztrált ");
        sb.append("a Villanykari Információs Rendszerbe.\n");
        sb.append("Ha nem Te voltál az, akkor ezt a levelet nyugodtan törölheted, ellenkező ");
        sb.append("esetben meg kell erősítened a regisztrációdat. Ehhez nem kell mást tenned, mint ");
        sb.append("egy böngészőbe beírni az alábbi URL-t: ");
        sb.append("https://korok.sch.bme.hu/korok/confirm/uid/").append(p.getUid());
        sb.append("/confirmationcode/").append(getConfirmationCode(p)).append("\n\n");

        if (sendPass) {
            sb.append("Felhasználói neved: ").append(p.getUid()).append('\n');
            sb.append("Jelszavad: ").append(password).append("\n\n");
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
            log.error("Nem sikerült menteni a felhasználót", ex);
            throw new RuntimeException("nem sikerült létrehozni a felhasználót", ex);
        }
    }

    private void addNotNullAttribute(Attributes attrs, String key, Object value) {
        if (value != null) {
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
