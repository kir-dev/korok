package hu.sch.web.kp.pages.search;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.profile.Person;
import hu.sch.services.LdapManagerLocal;
import hu.sch.web.profile.pages.search.PersonLinkPanel;
import hu.sch.web.wicket.components.customlinks.GroupLink;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.wicket.util.SortableGroupDataProvider;
import hu.sch.web.wicket.util.SortablePersonDataProvider;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class PersonResultPanel extends Panel {

    @EJB(name = "LdapManagerBean")
    LdapManagerLocal ldapManager;

    public PersonResultPanel(String id, List<Person> persons) {
        super(id);

        InjectorHolder.getInjector().inject(this);

        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add(new AbstractColumn<Person>(new Model<String>("Név"), "fullName") {

            @Override
            public void populateItem(Item<ICellPopulator<Person>> cellItem, String componentId, IModel<Person> rowModel) {
                cellItem.add(new PersonLinkPanel(componentId, rowModel.getObject()));
            }
        });
        columns.add(new PropertyColumn<Person>(new Model<String>("Becenév"), "nickName"));
        columns.add(new PropertyColumn<Person>(new Model<String>("Szobaszám"), "roomNumber"));

        SortablePersonDataProvider provider = new SortablePersonDataProvider(persons);
        AjaxFallbackDefaultDataTable table = new AjaxFallbackDefaultDataTable("personTable", columns, provider, 50);
        add(table);
    }
}
