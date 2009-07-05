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
package hu.sch.web.profile.pages.search;

import hu.sch.domain.profile.Person;
import hu.sch.web.profile.pages.show.PersonDataProvider;
import java.util.ArrayList;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author Adam Lantos
 */
public class PersonsSearchResultsTable extends Panel {

    private PersonDataProvider personDataProvider = new PersonDataProvider(new ArrayList<Person>());

	public PersonsSearchResultsTable(String id) {
        super(id);

        //this.personDataProvider = personDataProvider;
        @SuppressWarnings("unchecked")
        IColumn<Person>[] columns = new IColumn[3];
        columns[0] = new TextFilteredPropertyColumn<Person, Object>(new Model<String>("Név"), "fullName", "fullName") {

            @Override
            public void populateItem(Item<ICellPopulator<Person>> item, String componentId, IModel<Person> model) {
                final Person person = model.getObject();
                item.add(new PersonLinkPanel(componentId, person));
            }
        };
        columns[1] = new PropertyColumn<Person>(
                new Model<String>("Becenév"), "nickName", "nickName");
        columns[2] = new TextFilteredPropertyColumn<Person, Object>(
                new Model<String>("Kollégium"), "roomNumber", "roomNumber") {

            @Override
            public void populateItem(Item<ICellPopulator<Person>> item, String componentId, IModel<Person> model) {
                final Person person = model.getObject();
                item.add(new DormitoryRoomNumberLinkPanel(componentId, person).setVisible(!person.isPrivateAttribute("roomNumber")));
            }
        };

        add(new DefaultDataTable<Person>("personsTable", columns,getPersonDataProvider(), 50));
    }

    /**
     * @return the personDataProvider
     */
    public PersonDataProvider getPersonDataProvider() {
        return personDataProvider;
    }

}
