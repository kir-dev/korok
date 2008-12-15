/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.profile;

import hu.sch.kp.services.UserManagerRemote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
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
 * @author konvergal
 */
public class LDAPPersonManager implements IPersonManager {

    private Logger log = Logger.getLogger(getClass());
    private static volatile IPersonManager INSTANCE;
    private static BeanFactory springBeanFactory;
    private LdapTemplate ldapTemplate;

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    public static IPersonManager getInstance() {
        synchronized (LDAPPersonManager.class) {
            if (INSTANCE == null) {
                ClassPathResource resource =
                        new ClassPathResource("hu/sch/profile/springldap.xml");
                LDAPPersonManager.springBeanFactory =
                        new XmlBeanFactory(resource);
                INSTANCE =
                        (LDAPPersonManager) LDAPPersonManager.springBeanFactory.getBean("ldapContact");
            }
            return INSTANCE;
        }
    }

    public UserManagerRemote getRemoteUserManager() {
        try {
            InitialContext ic = new InitialContext();

            return (UserManagerRemote) ic.lookup("java:comp/env/UserManager");
        } catch (NamingException ex) {
            log.error("CORBA / Remote EJB error", ex);

            return null;
        }
    }

    private static class PersonContextMapper extends AbstractContextMapper {

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
            person.setDateOfBirth(context.getStringAttribute("schacDateOfBirth"));
            person.setIM(context.getStringAttribute("schacUserPresenceID"));
            person.setStatus(context.getStringAttribute("inetUserStatus"));
            person.setPhoto((byte[]) context.getObjectAttribute("jpegPhoto"));

            // A tobbi sima attributumnal nem kell vizsgalni a null-t, itt viszont igen.
            if (context.getStringAttributes("schacUserPrivateAttribute") != null) {
                //person.setPrivateAttributes(context.getStringAttributes("schacUserPrivateAttribute"));
                person.setSchacPrivateAttribute(context.getStringAttributes("schacUserPrivateAttribute"));
            }

            if (context.getStringAttributes("eduPersonEntitlement") != null) {
                person.setEduPersonEntitlement(Arrays.asList(context.getStringAttributes("eduPersonEntitlement")));
            }

            String[] memberships =
                    context.getStringAttributes("sch-vir-csoporttagsag");
            if (memberships != null) {
                person.loadMemberships(Arrays.asList(memberships));
            }


            // Admin altal valtoztathato attributumok.
            person.setPersonalUniqueCode(context.getStringAttribute("schacPersonalUniqueCode"));
            person.setPersonalUniqueID(context.getStringAttribute("schacPersonalUniqueID"));
            person.setStudentUserStatus(context.getStringAttribute("schacUserStatus"));


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
        context.setAttributeValue("inetUserStatus", p.getStatus());
        context.setAttributeValue("jpegPhoto", p.getPhoto());

        context.setAttributeValues("schacUserPrivateAttribute", p.getSchacPrivateAttribute());

        // Csoporttagsagok beallitasa.
        context.setAttributeValues("eduPersonEntitlement", p.getEduPersonEntitlement().toArray());

        List<String> attrs = Arrays.asList(context.getStringAttributes("objectClass"));
        if (!attrs.contains("schacEntryConfidentiality")) {
            context.addAttributeValue("objectClass", "schacEntryConfidentiality");
        }
        if (!attrs.contains("schacUserEntitlements")) {
            context.addAttributeValue("objectClass", "schacUserEntitlements");
        }



        // Admin altal valtoztathato attributumok.
        context.setAttributeValue("schacPersonalUniqueCode", p.getPersonalUniqueCode());
        context.setAttributeValue("schacPersonalUniqueID", p.getPersonalUniqueID());
        context.setAttributeValue("schacUserStatus", p.getStudentUserStatus());
    }

    protected Name buildDn(Person p) {
        return buildDn(p.getUid());
    }

    protected Name buildDn(String uid) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("uid", uid);
        return dn;
    }

    protected ContextMapper getContextMapper() {
        return new PersonContextMapper();
    }

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

    public Person getPersonByVirId(String virId) throws PersonNotFoundException {
        EqualsFilter equalsFilter = new EqualsFilter("schacPersonalUniqueID", "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:" + virId);

        List<Person> searchResult = (List<Person>) getLdapTemplate().search("", equalsFilter.encode(), getContextMapper());

        if (searchResult.size() == 0) {
            throw new PersonNotFoundException();
        }

        return searchResult.get(0);
    }

    public void update(Person p) {
        Name dn = buildDn(p);
        DirContextOperations context = getLdapTemplate().lookupContext(dn);
        mapToContext(p, context);
        getLdapTemplate().modifyAttributes(context);
    }

    public AndFilter setUpAndFilter(List<String> searchWords) {
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
            orFilter.or(new AndFilter().and(new NotFilter(new EqualsFilter("schacUserPrivateAttribute", "mail"))).and(new EqualsFilter("mail", s)));
//            orFilter.or(new EqualsFilter("mail", s));
//            orFilter.or(new LikeFilter("roomNumber", "*" + s));
            orFilter.or(new AndFilter().and(new NotFilter(new EqualsFilter("schacUserPrivateAttribute", "roomNumber"))).and(new LikeFilter("roomNumber", "*" +
                    s + "*")));
            andFilter.and(orFilter);
        }
        andFilter.and(new EqualsFilter("objectclass", "person"));
        return andFilter;
    }

    public List<Person> search(List<String> searchWords) {
        AndFilter andFilter = setUpAndFilter(searchWords);
        andFilter.and(new EqualsFilter("inetUserStatus", "active"));
        return (List<Person>) getLdapTemplate().search("",
                andFilter.encode(), getContextMapper());
    }

    public List<Person> searchByAdmin(List<String> searchWords) {
        AndFilter andFilter = setUpAndFilter(searchWords);
        return (List<Person>) getLdapTemplate().search("",
                andFilter.encode(), getContextMapper());
    }

    public List<Person> getPersonsWhoHasBirthday(String searchDate) {
        AndFilter andFilter = new AndFilter();
        andFilter.and(new NotFilter(new EqualsFilter("schacUserPrivateAttribute", "schacDateOfBirth"))).and(new LikeFilter("schacDateOfBirth", "*" + searchDate));
        return (List<Person>) getLdapTemplate().search("", andFilter.encode(), getContextMapper());
    }

    public List<Person> getPersonByDn(List<String> dnList) {
        List<Person> persons = new ArrayList<Person>(dnList.size());
        for (String dnStr : dnList) {
            DistinguishedName dn = new DistinguishedName(dnStr);
            Name dnSuffix = dn.getSuffix(dn.size() - 1);
            try {
                persons.add(
                        (Person) getLdapTemplate().lookup(dnSuffix, getContextMapper()));
            } catch (Throwable e) {
            }
        }

        return persons;
    }

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
}
