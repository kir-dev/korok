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
package hu.sch.web.profile.pages.search;

import hu.sch.domain.profile.Person;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author konvergal
 */
public final class DormitoryRoomNumberLinkPanel extends Panel {

    public DormitoryRoomNumberLinkPanel(String id, final Person person) {
        super(id);

        Link l = new Link("dormitoryRoomNumberLink") {

            @Override
            public void onClick() {
                setResponsePage(new SearchResultPage("\"" + person.getRoomNumber() + "\""));
            }
        };
        l.add(new Label("dormitoryRoomNumber", person.getRoomNumber()));
        add(l);
        setMarkupId(id);
    }
}
