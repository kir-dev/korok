/**
 * Copyright (c) 2009-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package hu.sch.web.profile.pages.search;

import hu.sch.domain.profile.Person;
import hu.sch.web.wicket.util.PersonDataProvider;
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

        add(new DefaultDataTable<Person>("personsTable", columns, getPersonDataProvider(), 50));
    }

    /**
     * @return the personDataProvider
     */
    public PersonDataProvider getPersonDataProvider() {
        return personDataProvider;
    }
}
