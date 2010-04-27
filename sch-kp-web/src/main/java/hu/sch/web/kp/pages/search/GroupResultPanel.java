package hu.sch.web.kp.pages.search;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.wicket.components.SvieDelegateNumberField;
import hu.sch.web.wicket.components.SvieGroupStatusSelector;
import hu.sch.web.wicket.components.customlinks.GroupLink;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.wicket.util.SortableGroupDataProvider;
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
public class GroupResultPanel extends Panel {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public GroupResultPanel(String id, List<Group> groups) {
        super(id);

        InjectorHolder.getInjector().inject(this);

        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add(new AbstractColumn<Group>(new Model<String>("Név"), "name") {

            @Override
            public void populateItem(Item<ICellPopulator<Group>> cellItem, String componentId, IModel<Group> rowModel) {
                cellItem.add(new GroupLink(componentId, rowModel.getObject()));
            }
        });
        columns.add(new AbstractColumn<Group>(new Model<String>("Körvezető neve")) {

            @Override
            public void populateItem(Item<ICellPopulator<Group>> cellItem, String componentId, IModel<Group> rowModel) {
                User korvezeto = userManager.getGroupLeaderForGroup(rowModel.getObject().getId());
                cellItem.add(new UserLink(componentId, korvezeto));
            }
        });

        SortableGroupDataProvider provider = new SortableGroupDataProvider(groups);
        AjaxFallbackDefaultDataTable table = new AjaxFallbackDefaultDataTable("groupTable", columns, provider, 50);
        add(table);
    }
}
