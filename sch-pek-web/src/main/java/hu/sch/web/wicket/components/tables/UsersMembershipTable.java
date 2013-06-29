package hu.sch.web.wicket.components.tables;

import hu.sch.domain.Membership;
import hu.sch.web.wicket.components.customlinks.GroupLink;
import hu.sch.web.wicket.components.customlinks.LinkPanel;
import hu.sch.web.wicket.components.customlinks.OldBoyLinkPanel;
import hu.sch.web.wicket.util.SortableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

/**
 * Itt listázzuk ki a felhasználó tagságait.
 *
 * @author messo
 * @since 2.3.1
 */
public abstract class UsersMembershipTable implements Serializable {

    AjaxFallbackDefaultDataTable<Membership, String> table;
    MySortableDataProvider provider;

    public UsersMembershipTable(String id, List<Membership> memberships, boolean isOwnProfile, int rowsPerPage) {
        provider = new MySortableDataProvider(memberships);

        List<IColumn<Membership, String>> columns = new ArrayList<IColumn<Membership, String>>(5);
        columns.add(new PanelColumn<Membership>("Kör neve", Membership.SORT_BY_GROUP) {

            @Override
            public Panel getPanel(String componentId, Membership ms) {
                return new GroupLink(componentId, ms.getGroup());
            }
        });
        columns.add(new PropertyColumn<Membership, String>(new Model<String>("Betöltött poszt"), Membership.SORT_BY_POSTS, "membership"));
        columns.add(new DateIntervalPropertyColumn<Membership>(
                new Model<String>("Tagsági idő"), Membership.SORT_BY_INTERVAL, "start", "end"));
        if (isOwnProfile) {
            // csak akkor kell ez az oszlop, ha ez a mi táblázatunk
            columns.add(new LinkColumn<Membership>("Öregtaggá válás?") {

                @Override
                protected boolean isVisible(Membership ms) {
                    return ms.getEnd() == null;
                }

                @Override
                public void onClick(Membership ms) {
                    onWannabeOldBoy(ms);
                }

                @Override
                protected LinkPanel getLinkPanel(String componentId, Membership ms) {
                    return new OldBoyLinkPanel(componentId, ms);
                }
            });
        }

        table = new AjaxFallbackDefaultDataTable<Membership, String>(id, columns, provider, rowsPerPage);
        provider.setSort(Membership.SORT_BY_GROUP, SortOrder.ASCENDING);
    }

    public AjaxFallbackDefaultDataTable<Membership, String> getDataTable() {
        return table;
    }

    protected abstract void onWannabeOldBoy(Membership ms);

    static class MySortableDataProvider extends SortableDataProvider<Membership, String> {

        private SortableList<Membership> items;

        private MySortableDataProvider(List<Membership> items) {
            this.items = new SortableList<Membership>(items);
        }

        @Override
        public Iterator<? extends Membership> iterator(long first, long count) {
            items.sort(getSort());
            return items.getList().subList((int) first, (int) (first + count)).iterator();
        }

        @Override
        public long size() {
            return items.size();
        }

        @Override
        public IModel<Membership> model(Membership ms) {
            return new LoadableDetachableModel<Membership>(ms) {

                @Override
                protected Membership load() {
                    // TODO(messo): ilyenkor mi van? :)
                    return new Membership();
                }
            };
        }
    }
}
