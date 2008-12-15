/*
 *  Copyright 2008 Adam Lantos.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package hu.sch.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.Name;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;

/**
 *
 * @author Adam Lantos
 */
public class LDAPGroupManager implements IGroupManager {

    private static volatile IGroupManager INSTANCE;
    private LdapTemplate ldapTemplate;

    public LdapTemplate getLdapTemplate() {
        return ldapTemplate;
    }

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public static IGroupManager getInstance() {
        synchronized (LDAPGroupManager.class) {
            if (INSTANCE == null) {
                ClassPathResource resource =
                        new ClassPathResource("hu/sch/profile/springldap.xml");
                BeanFactory factory = new XmlBeanFactory(resource);
                INSTANCE = (LDAPGroupManager) factory.getBean("ldapGroup");
            }
            return INSTANCE;
        }
    }

    protected Name buildDn(Group g) {
        return buildDn(g.getGroupName());
    }

    protected Name buildDn(String cn) {
        DistinguishedName dn = new DistinguishedName();
        dn.add("cn", cn);
        return dn;
    }

    public List<Group> listGroups() {
        return getLdapTemplate().search("",
                "objectClass=groupOfUniqueNames",
                new SimpleGroupContextMapper());
    }

    public Group getGroupByCN(String groupCN) {
        Name dn = buildDn(groupCN);
        Group g = null;
        try {
            g = (Group) getLdapTemplate().lookup(dn,
                    new CompoundGroupContextMapper());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return g;
    }

    private abstract class MyAbstractContextMapper extends AbstractContextMapper {

        protected boolean hasObjectClass(DirContextOperations context, String objectClass) {
            String[] objClasses = context.getStringAttributes("objectClass");
            for (String s : Arrays.asList(objClasses)) {
                if (s.equalsIgnoreCase(objectClass)) {
                    return true;
                }
            }
            return false;
        }
    }

    private class SimpleGroupContextMapper extends MyAbstractContextMapper {

        public Object doMapFromContext(DirContextOperations context) {
            if (!hasObjectClass(context, "groupofuniquenames")) {
                return null;
            }

            Group group = new Group();
            group.setGroupName(context.getStringAttribute("cn"));


            return group;
        }
    }

    private class CompoundGroupContextMapper extends MyAbstractContextMapper {

        public Object doMapFromContext(DirContextOperations context) {
            if (!hasObjectClass(context, "groupofuniquenames")) {
                return null;
            }

            Group group = new Group();

            group.setGroupName(context.getStringAttribute("cn"));
            if (context.getStringAttributes("uniqueMember") != null) {
                String[] members = context.getStringAttributes("uniqueMember");
                if (members != null) {
                    group.setUniqueMember(new ArrayList<String>(Arrays.asList(members)));


                    IPersonManager pm = LDAPPersonManager.getInstance();
                    List<Person> persons = pm.getPersonByDn(Arrays.asList(members));
                    group.setMembers(persons);
                }
            }

            return group;
        }
    }

    protected void mapToContext(Group group, DirContextOperations context) {
        context.setAttributeValue("cn", group.getGroupName());
        context.setAttributeValues("uniqueMember", group.getUniqueMember().toArray());
    }

    public void update(Group group) {
        Name dn = buildDn(group);
        DirContextOperations context = getLdapTemplate().lookupContext(dn);
        mapToContext(group, context);
        getLdapTemplate().modifyAttributes(context);
    }
}
