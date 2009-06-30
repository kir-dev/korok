/*
 *  Copyright 2008 aldaris.
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
package hu.sch.web.profile.pages.birthday;

import hu.sch.domain.profile.Person;
import hu.sch.web.profile.pages.show.PersonDataProvider;
import hu.sch.web.profile.pages.template.ProfilePage;
import hu.sch.web.profile.pages.search.PersonLinkPanel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class BirthDayPage extends ProfilePage {

    public List<Person> persons = new ArrayList();
    PersonDataProvider personDataProvider;

    public BirthDayPage() {
        super();
        add(new FeedbackPanel("feedbackPanel"));
        setHeaderLabelModel(new Model("Szülinaposok"));
        personDataProvider = new PersonDataProvider(persons);
        birthDaySearch();

        final DataView dataView = new DataView("simple", personDataProvider) {

            public void populateItem(final Item item) {
                final Person user = (Person) item.getModelObject();
                item.add(new PersonLinkPanel("id", user));
            }
        };
        add(dataView);
    }

    public void birthDaySearch() {
        persons.clear();
        Date date = Calendar.getInstance().getTime();
        String date2 = new SimpleDateFormat("MMdd").format(date);
        persons.addAll(ldapManager.getPersonsWhoHasBirthday(date2));
        personDataProvider.setPersons(persons);
        if (persons.size() == 0) {
            info("Ma senki se ünnepel:(");
        }
    }
}
