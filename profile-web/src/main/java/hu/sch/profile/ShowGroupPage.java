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

import org.apache.wicket.PageParameters;

/**
 *
 * @author Adam Lantos
 */
public class ShowGroupPage extends ProfilePage {

    public ShowGroupPage(PageParameters param) {
//        add(new FeedbackPanel("feedbackPanel"));

//        try {
            String grpName = param.get("groupName").toString();
            IGroupManager gm = LDAPGroupManager.getInstance();
            Group g = gm.getGroupByCN(grpName);
            g.loadMembersEntitlements();
        
            setHeaderLabelText(g.getGroupName());
            
            Person korvezeto;
            try {
                korvezeto = g.getMembersWhoHasEntitlementType(EntitlementType.KORVEZETO).get(0);
            }
            // Nem talaljuk a korvezetot ldap-ban.
            catch (Exception e) {
                korvezeto = new Person();
            }
            add(new PersonLinkPanel("korvezeto", korvezeto));
            add(new PersonsSearchResultsTable("groupMembers", new PersonDataProvider(g.getMembers())));
/*        } catch (Throwable t) {
            setResponsePage(new ErrorPage("Nincs ilyen csoport! :("));
            return;
        }*/
    }
}
