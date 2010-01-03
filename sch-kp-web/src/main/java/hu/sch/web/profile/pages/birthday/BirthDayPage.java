/**
 * Copyright (c) 2009, Peter Major
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

    public List<Person> persons = new ArrayList<Person>();
    PersonDataProvider personDataProvider;

    public BirthDayPage() {
        super();
        add(new FeedbackPanel("feedbackPanel"));
        setHeaderLabelModel(new Model("Szülinaposok"));
        personDataProvider = new PersonDataProvider(persons);
        birthDaySearch();

        final DataView<Person> dataView = new DataView<Person>("simple", personDataProvider) {

            @Override
            public void populateItem(final Item<Person> item) {
                final Person user = item.getModelObject();
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
        if (persons.isEmpty()) {
            info("Ma senki se ünnepel:(");
        }
    }
}
