package hu.sch.web.wicket.components.tables;

import hu.sch.domain.ValuationData;
import hu.sch.web.wicket.components.customlinks.GroupLink;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Értékelési táblázat, amely a félévet, a kör nevét és az értékelést
 * tartalmazza (pont- és belépőkérelem). Ezt használjuk egy felhasználó korábbi
 * értékeléseinek megjelenítéséhez.
 *
 * @author messo
 * @since 2.3.1
 */
public class ValuationTableForUser extends ValuationTable {

    public ValuationTableForUser(String id, List<ValuationData> items, int rowsPerPage) {
        super(id, items, rowsPerPage, false);
        provider.setSort(MySortableDataProvider.SORT_BY_SEMESTER, SortOrder.DESCENDING);
    }

    public ValuationTableForUser(String id, List<ValuationData> items) {
        this(id, items, 20);
    }

    @Override
    protected void populateColumns(List<IColumn<ValuationData, String>> columns) {
        columns.add(new PropertyColumn<ValuationData, String>(new Model<String>("Szemeszter"),
                MySortableDataProvider.SORT_BY_SEMESTER, "semester") {

            @Override
            public void populateItem(Item<ICellPopulator<ValuationData>> item,
                                String componentId, IModel<ValuationData> rowModel) {

                super.populateItem(item, componentId, rowModel);
                item.add(AttributeModifier.replace("style", "width: 130px"));
            }
        });

        columns.add(new PanelColumn<ValuationData>("Kör", MySortableDataProvider.SORT_BY_GROUP) {

            @Override
            protected Panel getPanel(String componentId, ValuationData vd) {
                return new GroupLink(componentId, vd.getGroup());
            }
        });

        columns.add(new PropertyColumn<ValuationData, String>(new Model<String>("Pont"),
                MySortableDataProvider.SORT_BY_POINT, "pointRequest.point"));

        columns.add(new PropertyColumn<ValuationData, String>(new Model<String>("Belépő típusa"),
                MySortableDataProvider.SORT_BY_ENTRANT, "entrantRequest.entrantType"));

        columns.add(new PropertyColumn<ValuationData, String>(new Model<String>("Szöveges értékelés"),
                "entrantRequest.valuationText") {

            @Override
            public void populateItem(Item<ICellPopulator<ValuationData>> item, String componentId, IModel<ValuationData> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(AttributeModifier.replace("style", "width: 350px"));
            }
        });
    }
}
