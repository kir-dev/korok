package hu.sch.web.wicket.components.tables;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Egy olyan {@link AbstractColumn} gyermekosztály, ami egy {@link Panel}t képes megjeleníteni
 *
 * @author  messo
 * @since   2.3.1
 */
public abstract class PanelColumn<T> extends AbstractColumn<T, String> {

    public PanelColumn(final IModel<String> displayModel, final String sortProperty) {
        super(displayModel, sortProperty);
    }

    public PanelColumn(final String header, final String sortProperty) {
        this(new Model<String>(header), sortProperty);
    }

    public PanelColumn(final IModel<String> displayModel) {
        this(displayModel, null);
    }

    public PanelColumn(final String header) {
        this(header, null);
    }

    @Override
    public void populateItem(final Item<ICellPopulator<T>> item, final String componentId,
                        final IModel<T> rowModel) {
        item.add(getPanel(componentId, rowModel.getObject()));
    }

    protected abstract Panel getPanel(final String componentId, final T obj);
}
