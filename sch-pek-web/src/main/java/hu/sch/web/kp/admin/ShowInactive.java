package hu.sch.web.kp.admin;

import hu.sch.domain.profile.Person;
import hu.sch.web.wicket.components.customlinks.DeletePersonLink;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.util.SortablePersonDataProvider;
import hu.sch.web.profile.search.PersonLinkPanel;
import hu.sch.web.wicket.components.tables.PanelColumn;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class ShowInactive extends KorokPage {

    private SortablePersonDataProvider personProvider;

    public ShowInactive() {
        if (!isCurrentUserAdmin()) {
            throw new RestartResponseException(NotFound.class);
        }
        setHeaderLabelText("Inaktív felhasználók");
        List<Person> inactivePersons = ldapManager.searchInactives();

        List<IColumn<Person, String>> columns = new ArrayList<IColumn<Person, String>>();
        columns.add(new PanelColumn<Person>("Név", Person.SORT_BY_NAME) {

            @Override
            protected Panel getPanel(String componentId, Person p) {
                return new PersonLinkPanel(componentId, p);
            }
        });
        columns.add(new PropertyColumn<Person, String>(new Model<String>("Uid"), Person.SORT_BY_UID, "uid"));
        columns.add(new PropertyColumn<Person, String>(new Model<String>("E-mail"), Person.SORT_BY_MAIL, "mail"));
        columns.add(new PropertyColumn<Person, String>(new Model<String>("Neptun kód"), Person.SORT_BY_NEPTUN, "neptun"));
        columns.add(new PanelColumn<Person>("Törlés") {

            @Override
            protected Panel getPanel(String componentId, Person p) {
                return new DeletePersonLink(componentId, p, ShowInactive.class);
            }
        });

        personProvider = new SortablePersonDataProvider(inactivePersons);
        final AjaxFallbackDefaultDataTable<Person, String> table =
                new AjaxFallbackDefaultDataTable<Person, String>("table", columns, personProvider, 100);
        table.setOutputMarkupId(true);
        add(table);
    }
}
