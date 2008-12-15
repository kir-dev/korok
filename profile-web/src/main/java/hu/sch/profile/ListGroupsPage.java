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

import java.util.Collections;
import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author Adam Lantos
 */
public class ListGroupsPage extends ProfilePage {

    public ListGroupsPage() {
        IGroupManager gm = LDAPGroupManager.getInstance();
        List<Group> groups = gm.listGroups();
        Collections.sort(groups);

        ListView grps = new ListView("aGroup", groups) {

            @Override
            protected void populateItem(ListItem item) {
                IModel model =
                        new PropertyModel(item.getModelObject(), "groupName");

                PageParameters params = new PageParameters();
                params.put("groupName", model.getObject().toString());
                BookmarkablePageLink bpl = new BookmarkablePageLink("groupLink",
                        ShowGroupPage.class, params);

                bpl.add(new Label("groupName", model));
                item.add(bpl);
            }
        };

        add(grps);
    }
}
