/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.admin;

import hu.sch.domain.profile.Person;
import hu.sch.web.components.customlinks.DeletePersonLink;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.SortablePersonDataProvider;
import hu.sch.web.profile.pages.search.PersonLinkPanel;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class ShowInactive extends SecuredPageTemplate {

    private SortablePersonDataProvider personProvider;

    public ShowInactive() {
        if (!isCurrentUserAdmin()) {
            throw new RestartResponseException(NotFound.class);
        }
        setHeaderLabelText("Inaktív felhasználók");
        add(new FeedbackPanel("pagemessages"));
        List<Person> inactivePersons = ldapManager.searchInactives();

        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add(new AbstractColumn<Person>(new Model<String>("Név"), "name") {

            @Override
            public void populateItem(Item<ICellPopulator<Person>> item, String componentId, IModel<Person> imodel) {
                item.add(new PersonLinkPanel(componentId, imodel.getObject()));
            }
        });
        columns.add(new PropertyColumn<Person>(new Model<String>("Uid"), "uid", "uid"));
        columns.add(new PropertyColumn<Person>(new Model<String>("E-mail"), "mail"));
        columns.add(new PropertyColumn<Person>(new Model<String>("Neptun kód"), "neptun", "neptun"));
        columns.add(new AbstractColumn<Person>(new Model<String>("Törlés")) {

            @Override
            public void populateItem(Item<ICellPopulator<Person>> item, String componentId, IModel<Person> imodel) {
                item.add(new DeletePersonLink(componentId, imodel.getObject(), ShowInactive.class));
            }
        });

        personProvider = new SortablePersonDataProvider(inactivePersons);
        final AjaxFallbackDefaultDataTable table =
                new AjaxFallbackDefaultDataTable("table", columns, personProvider, 100);
        table.setOutputMarkupId(true);
        add(table);
    }
}
