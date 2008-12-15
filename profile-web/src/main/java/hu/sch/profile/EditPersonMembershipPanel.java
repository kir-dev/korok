package hu.sch.profile;

import java.util.ArrayList;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

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
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;

/**
 *
 * @author konvergal
 */
final class EditPersonMembershipPanel extends Panel {
    
    class MembershipLink extends Link {
        
        Person person;
        Group group;
        
        MembershipLink(String id) {
            super(id);
        }

        MembershipLink(String id, Person person, Group group) {
            this(id);
            this.person = person;
            this.group = group;
        }

        @Override
        public void onClick() {
            MembershipManager.addPersonToGroup(this.person, this.group, EntitlementType.TAG);
        }

    }
    

    EditPersonMembershipPanel(String id) {
        super(id);
    }

    EditPersonMembershipPanel(String id, Person p1, final Person p2) {
        this(id);

        p1.loadEntitlements();

        ArrayList<Group> korvezetoGroups = (ArrayList<Group>) p1.getGroupsByEntitlementType(EntitlementType.KORVEZETO);

        DataView editPersonMembershipTable = new DataView("editPersonMembershipTableRow", new ListDataProvider(korvezetoGroups)) {

            @Override
            protected void populateItem(Item item) {
                Group group = (Group) item.getModelObject();
                Label groupName = new Label("groupName", group.getGroupName());
                item.add(groupName);

                MembershipLink membershipLink = new MembershipLink("activateMembershipLink", p2, group);
                item.add(membershipLink);
                
                if (group.hasMember(p2)) {
                    groupName.setVisible(false);
                    membershipLink.setVisible(false);
                }
            }
        };

        add(editPersonMembershipTable);
    }
}
