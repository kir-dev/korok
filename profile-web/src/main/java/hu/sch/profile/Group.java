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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Adam Lantos
 */
public class Group implements Comparable<Group>, Serializable {
    private String groupName;
    private List<Person> members = new ArrayList<Person>();
    private List<String> uniqueMember = new ArrayList<String>();

    public void setUniqueMember(List<String> uniqueMember) {
        this.uniqueMember = uniqueMember;
//        this.uniqueMember.clear();
//        this.uniqueMember.addAll(uniqueMember);
    }

    public List<String> getUniqueMember() {
        return uniqueMember;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public int compareTo(Group o) {
        return getGroupName().compareTo(o.getGroupName());
    }
    
    public void loadMembersEntitlements() {
        for (Person person : members) {
            person.loadEntitlements();
        }
    }
    
    public List<Person> getMembersWhoHasEntitlementType(EntitlementType entitlementType) {
        List<Person> p = new ArrayList<Person>();
        
        for (Person person : members) {
//            Entitlement e = new Entitlement(entitlementType, this);
            if (person.hasEntitlementType(entitlementType)) {
                p.add(person);
            }
        }
        
        return p;
    }
    
    public Boolean hasMember(Person person) {
        for (Person p : members) {
            if (p.getUid().equals(person.getUid())) {
                return true;
            }
        }
        
        return false;
    }
    
    public void addMember(Person p) {
        members.add(p);
        uniqueMember.add(new String("uid=" + p.getUid() + ",dc=sch,dc=hu"));
    }
}
