/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.ejb;

import hu.sch.domain.ldap.LdapPerson;
import hu.sch.kp.services.LdapPersonManagerLocal;
import hu.sch.kp.services.exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.naming.Name;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
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
public class LdapPersonManagerBean implements LdapPersonManagerLocal {

    /**
     * A logolashoz szukseges objektum.
     */
    private Logger log = Logger.getLogger(getClass());
    /**
     * Bean objektum, letrehozasakor jon letre maga az ldapTemplate.
     */
    private static volatile LdapPersonManagerBean INSTANCE;
    /**
     * Segitsegevel allitjuk elo az xml fajlbol az Ldap-ot kezelo objektumot.
     */
    private static BeanFactory springBeanFactory;
    /**
     * Ez az objektum kezeli az osszes Ldap-ban levo adat elereset, modositasat, mindent.
     */
    private static LdapTemplate ldapTemplate;

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
        synchronized (LdapPersonManagerBean.class) {
            if (INSTANCE == null) {
                FileSystemResource resource =
                        new FileSystemResource("/home/major/springldap-kp.xml");
                LdapPersonManagerBean.springBeanFactory =
                        new XmlBeanFactory(resource);
                INSTANCE =
                        (LdapPersonManagerBean) LdapPersonManagerBean.springBeanFactory.getBean("ldapContact");                
            }
        }
    }

    /**
     * Inicializalo fuggveny, segitsegevel keszitjuk el az egyetlen darab Ldap-kezelo objektumunkat
     */
    public void initialization() {
        //mivel az EJB thread-safe, megtehetjuk ezt
        LdapPersonManagerBean lpmb = INSTANCE;
        if (lpmb == null) {
            tryCreateInstance();
        }
    }

    private static class PersonContextMapper extends AbstractContextMapper {

        public Object doMapFromContext(DirContextOperations context) {
            LdapPerson ldapPerson = new LdapPerson();
            ldapPerson.setUid(context.getStringAttribute("uid"));
            ldapPerson.setLastName(context.getStringAttribute("sn"));
            ldapPerson.setFirstName(context.getStringAttribute("givenName"));
            ldapPerson.setFullName(context.getStringAttribute("cn"));
            ldapPerson.setNickName(context.getStringAttribute("displayName"));

//            if (context.getStringAttributes("eduLDAPPersonEntitlement") != null) {
//                LDAPPerson.setEduLDAPPersonEntitlement(Arrays.asList(context.getStringAttributes("eduLDAPPersonEntitlement")));
//            }

            String[] memberships =
                    context.getStringAttributes("sch-vir-csoporttagsag");
            if (memberships != null) {
                ldapPerson.loadMemberships(Arrays.asList(memberships));
            }

            // Admin altal valtoztathato attributumok.
            ldapPerson.setPersonalUniqueCode(context.getStringAttribute("schacLDAPPersonalUniqueCode"));
            ldapPerson.setPersonalUniqueID(context.getStringAttribute("schacLDAPPersonalUniqueID"));

            ldapPerson.setToUse();
            return ldapPerson;
        }
    }

    protected void mapToContext(LdapPerson p, DirContextOperations context) {
        p.setToSave();

//        // Csoporttagsagok beallitasa.
//        context.setAttributeValues("eduLDAPPersonEntitlement", p.getEduPersonEntitlement().toArray());
//??????????????????????????????????????????????????????????????????????????????
        List<String> attrs = Arrays.asList(context.getStringAttributes("objectClass"));
        if (!attrs.contains("schacEntryConfidentiality")) {
            context.addAttributeValue("objectClass", "schacEntryConfidentiality");
        }
        if (!attrs.contains("schacUserEntitlements")) {
            context.addAttributeValue("objectClass", "schacUserEntitlements");
        }
//??????????????????????????????????????????????????????????????????????????????
        //TODO: sch-vir-csoporttagsagok beallitasa is!
        context.setAttributeValue("schacLDAPPersonalUniqueID", p.getPersonalUniqueID());
    }

    protected Name buildDn(LdapPerson p) {
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

    public LdapPerson getPersonByUid(String uid) throws PersonNotFoundException {
        Name dn = buildDn(uid);
        LdapPerson p = null;
        try {
            p = (LdapPerson) getLdapTemplate().lookup(dn, getContextMapper());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (p == null) {
            throw new PersonNotFoundException();
        }

        return p;
    }

    public LdapPerson getPersonByVirId(String virId) throws PersonNotFoundException {
        EqualsFilter equalsFilter = new EqualsFilter("schacPersonalUniqueID", "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:" + virId);

        List<LdapPerson> searchResult = (List<LdapPerson>) getLdapTemplate().search("", equalsFilter.encode(), getContextMapper());

        if (searchResult.size() == 0) {
            throw new PersonNotFoundException();
        }

        return searchResult.get(0);
    }

    public void update(LdapPerson p) {
        Name dn = buildDn(p);
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
            orFilter.or(new AndFilter().and(new NotFilter(new EqualsFilter("schacUserPrivateAttribute", "mail"))).and(new EqualsFilter("mail", s)));
//            orFilter.or(new EqualsFilter("mail", s));
//            orFilter.or(new LikeFilter("roomNumber", "*" + s));
            orFilter.or(new AndFilter().and(new NotFilter(new EqualsFilter("schacUserPrivateAttribute", "roomNumber"))).and(new LikeFilter("roomNumber", "*" +
                    s + "*")));
            andFilter.and(orFilter);
        }
        andFilter.and(new EqualsFilter("objectclass", "LDAPPerson"));
        return andFilter;
    }

    public List<LdapPerson> search(List<String> searchWords) {
        AndFilter andFilter = setUpAndFilter(searchWords);
        andFilter.and(new EqualsFilter("inetUserStatus", "active"));
        return (List<LdapPerson>) getLdapTemplate().search("",
                andFilter.encode(), getContextMapper());
    }

    public List<LdapPerson> searchByAdmin(List<String> searchWords) {
        AndFilter andFilter = setUpAndFilter(searchWords);
        return (List<LdapPerson>) getLdapTemplate().search("",
                andFilter.encode(), getContextMapper());
    }

    public List<LdapPerson> searchsomething(String searchDate) {
        AndFilter andFilter = new AndFilter();
        andFilter.and(new NotFilter(new EqualsFilter("schacUserPrivateAttribute", "schacDateOfBirth"))).and(new LikeFilter("schacDateOfBirth", "*" + searchDate));
        return (List<LdapPerson>) getLdapTemplate().search("", andFilter.encode(), getContextMapper());
    }

    public List<LdapPerson> getPersonByDn(List<String> dnList) {
        List<LdapPerson> LDAPPersons = new ArrayList<LdapPerson>(dnList.size());
        for (String dnStr : dnList) {
            DistinguishedName dn = new DistinguishedName(dnStr);
            Name dnSuffix = dn.getSuffix(dn.size() - 1);
            try {
                LDAPPersons.add(
                        (LdapPerson) getLdapTemplate().lookup(dnSuffix, getContextMapper()));
            } catch (Throwable e) {
            }
        }

        return LDAPPersons;
    }
}