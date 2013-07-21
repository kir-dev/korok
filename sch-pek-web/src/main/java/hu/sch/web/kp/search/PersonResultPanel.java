package hu.sch.web.kp.search;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.web.profile.search.PersonLinkPanel;
import hu.sch.web.wicket.components.customlinks.SearchLink;
import hu.sch.web.wicket.components.tables.PanelColumn;
import hu.sch.web.wicket.util.SortablePersonDataProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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

    public PersonResultPanel(String id, List<User> persons) {
        super(id);

        List<IColumn<User, String>> columns = new ArrayList<IColumn<User, String>>();
        columns.add(new PanelColumn<User>("Név", "fullName") {
            @Override
            protected Panel getPanel(String componentId, User p) {
                return new PersonLinkPanel(componentId, p);
            }
        });
        columns.add(new PropertyColumn<User, String>(new Model<String>("Becenév"), "nickName", "nickName"));
        columns.add(new PanelColumn<User>("Szobaszám", "fullRoomNumber") {
            @Override
            protected Panel getPanel(String componentId, User p) {
                Panel panel = new SearchLink(componentId, SearchLink.USER_TYPE, p.getFullRoomNumber());
                panel.setVisible(p.isAttributeVisible(UserAttributeName.ROOM_NUMBER)
                        && !StringUtils.isBlank(p.getFullRoomNumber()));
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
