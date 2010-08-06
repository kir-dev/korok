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
package hu.sch.web.kp.pages.admin;

import hu.sch.domain.profile.Person;
import hu.sch.web.wicket.components.customlinks.DeletePersonLink;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.templates.KorokPageTemplate;
import hu.sch.web.wicket.util.SortablePersonDataProvider;
import hu.sch.web.profile.pages.search.PersonLinkPanel;
import hu.sch.web.wicket.components.tables.PanelColumn;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class ShowInactive extends KorokPageTemplate {

    private SortablePersonDataProvider personProvider;

    public ShowInactive() {
        if (!isCurrentUserAdmin()) {
            throw new RestartResponseException(NotFound.class);
        }
        setHeaderLabelText("Inaktív felhasználók");
        add(new FeedbackPanel("pagemessages"));
        List<Person> inactivePersons = ldapManager.searchInactives();

        List<IColumn<Person>> columns = new ArrayList<IColumn<Person>>();
        columns.add(new PanelColumn<Person>("Név", Person.SORT_BY_NAME) {

            @Override
            protected Panel getPanel(String componentId, Person p) {
                return new PersonLinkPanel(componentId, p);
            }
        });
        columns.add(new PropertyColumn<Person>(new Model<String>("Uid"), Person.SORT_BY_UID, "uid"));
        columns.add(new PropertyColumn<Person>(new Model<String>("E-mail"), Person.SORT_BY_MAIL, "mail"));
        columns.add(new PropertyColumn<Person>(new Model<String>("Neptun kód"), Person.SORT_BY_NEPTUN, "neptun"));
        columns.add(new PanelColumn<Person>("Törlés") {

            @Override
            protected Panel getPanel(String componentId, Person p) {
                return new DeletePersonLink(componentId, p, ShowInactive.class);
            }
        });

        personProvider = new SortablePersonDataProvider(inactivePersons);
        final AjaxFallbackDefaultDataTable table =
                new AjaxFallbackDefaultDataTable("table", columns, personProvider, 100);
        table.setOutputMarkupId(true);
        add(table);
    }
}
