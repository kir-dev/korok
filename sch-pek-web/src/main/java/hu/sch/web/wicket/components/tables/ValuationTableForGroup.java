package hu.sch.web.wicket.components.tables;

import hu.sch.domain.Membership;
import hu.sch.domain.ValuationData;
import hu.sch.web.wicket.components.SvieMembershipDetailsIcon;
import hu.sch.web.wicket.components.customlinks.UserLink;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.cdi.CdiContainer;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Értékelési táblázat, amely neveket, pont- és belépőkérelmeket tartalmaz. Ezt
 * használjuk egy kör korábbi értékeléseinek megjelenítéséhez.
 *
 * @author messo
 * @since 2.3.1
 */
public class ValuationTableForGroup extends ValuationTable {

    public ValuationTableForGroup(String id, List<ValuationData> items, int rowsPerPage,
            final boolean showSvieColumn) {

        super(id, items, rowsPerPage, showSvieColumn);
        CdiContainer.get().getNonContextualManager().inject(this);
        provider.setSort(MySortableDataProvider.SORT_BY_POINT, SortOrder.DESCENDING);
    }

    public ValuationTableForGroup(String id, List<ValuationData> items,
            final boolean showSvieColumn) {

        this(id, items, 20, showSvieColumn);
    }

    @Override
    protected void populateColumns(List<IColumn<ValuationData, String>> columns) {
        columns.add(new PanelColumn<ValuationData>("Név", MySortableDataProvider.SORT_BY_USER) {

            @Override
            protected Panel getPanel(String componentId, ValuationData vd) {
                return new UserLink(componentId, vd.getUser());
            }
        });

        if (isShowSvieColumn) {
            columns.add(new PanelColumn<ValuationData>("SVIE") {

                @Override
                protected Panel getPanel(final String componentId, final ValuationData vd) {
                    final Membership ms = membershipManager.findMembership(vd.getGroup().getId(),
                            vd.getUser().getId());

                    if (ms != null) {
                        return new SvieMembershipDetailsIcon(componentId, ms);
                    }

                    // törölt körtagságból eredő értékelés
                    return new SvieMembershipDetailsIcon(componentId, vd.getUser());
                }
            });
        }

        columns.add(new PropertyColumn<ValuationData, String>(new Model<String>("Pont"),
                MySortableDataProvider.SORT_BY_POINT, "pointRequest.point"));

        columns.add(new PropertyColumn<ValuationData, String>(new Model<String>("Belépő típusa"),
                MySortableDataProvider.SORT_BY_ENTRANT, "entrantRequest.entrantType"));

        columns.add(new PropertyColumn<ValuationData, String>(new Model<String>("Szöveges értékelés"),
                "entrantRequest.valuationText") {

            @Override
            public void populateItem(Item<ICellPopulator<ValuationData>> item, String componentId, IModel<ValuationData> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(AttributeModifier.replace("style", "width: 400px"));
            }
        });
    }
}
