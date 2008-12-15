/*
 *  Copyright 2008 konvergal.
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

/**
 *
 * @author konvergal
 */
public class MembershipManager {

    static void addPersonToGroup(Person person, Group group, EntitlementType entitlementType) {
        group.addMember(person);
        Entitlement entitlement = new Entitlement(entitlementType, group);
        person.addEntitlement(entitlement);

        try {
            Person p = LDAPPersonManager.getInstance().getPersonByUid(person.getUid());
            p.loadEntitlements();
            p.addEntitlement(entitlement);
            LDAPPersonManager.getInstance().update(p);

            Group g = LDAPGroupManager.getInstance().getGroupByCN(group.getGroupName());
            g.addMember(p);
            LDAPGroupManager.getInstance().update(group);
        } catch (PersonNotFoundException e) {
        }
    }
}
