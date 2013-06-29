package hu.sch.web.kp.search;

import hu.sch.domain.profile.Person;
import hu.sch.web.profile.search.PersonLinkPanel;
import hu.sch.web.wicket.components.customlinks.SearchLink;
import hu.sch.web.wicket.components.tables.PanelColumn;
import hu.sch.web.wicket.util.SortablePersonDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class PersonResultPanel extends Panel {

    private final SortablePersonDataProvider provider;

    public PersonResultPanel(String id, List<Person> persons) {
        super(id);

        List<IColumn<Person, String>> columns = new ArrayList<IColumn<Person, String>>();
        columns.add(new PanelColumn<Person>("Név", Person.SORT_BY_NAME) {
            @Override
            protected Panel getPanel(String componentId, Person p) {
                return new PersonLinkPanel(componentId, p);
            }
        });
        columns.add(new PropertyColumn<Person, String>(new Model<String>("Becenév"), Person.SORT_BY_NICKNAME, "nickName"));
        columns.add(new PanelColumn<Person>("Szobaszám", Person.SORT_BY_ROOMNUMBER) {
            @Override
            protected Panel getPanel(String componentId, Person p) {
                Panel panel = new SearchLink(componentId, SearchLink.USER_TYPE, p.getRoomNumber());
                panel.setVisible(!p.isPrivateAttribute("roomNumber") && p.getRoomNumber() != null);
                return panel;
            }
        });

        provider = new SortablePersonDataProvider(persons);
        AjaxFallbackDefaultDataTable table = new AjaxFallbackDefaultDataTable("personTable", columns, provider, 50);
        add(table);
    }

    public SortablePersonDataProvider getPersonDataProvider() {
        return provider;
    }
}
