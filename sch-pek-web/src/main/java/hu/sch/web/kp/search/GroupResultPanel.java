package hu.sch.web.kp.search;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.wicket.components.customlinks.GroupLink;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.wicket.components.tables.PanelColumn;
import hu.sch.web.wicket.util.SortableGroupDataProvider;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author aldaris
 */
public class GroupResultPanel extends Panel {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public GroupResultPanel(String id, List<Group> groups) {
        super(id);

        Injector.get().inject(this);

        List<IColumn<Group, String>> columns = new ArrayList<IColumn<Group, String>>();
        columns.add(new PanelColumn<Group>("Név", "name") {

            @Override
            protected Panel getPanel(String componentId, Group g) {
                return new GroupLink(componentId, g);
            }
        });
        columns.add(new PanelColumn<Group>("Körvezető neve") {

            @Override
            protected Panel getPanel(String componentId, Group g) {
                // FIXME: ez így nagyon gány, minden egyes sorhoz külön query???
                User korvezeto = userManager.getGroupLeaderForGroup(g.getId());
                return new UserLink(componentId, korvezeto);
            }
        });

        SortableGroupDataProvider provider = new SortableGroupDataProvider(groups);
        AjaxFallbackDefaultDataTable<Group, String> table =
                new AjaxFallbackDefaultDataTable<Group, String>("groupTable", columns, provider, 50);
        add(table);
    }
}
