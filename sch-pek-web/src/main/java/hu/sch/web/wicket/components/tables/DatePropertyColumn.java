package hu.sch.web.wicket.components.tables;

import java.util.Date;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Egy olyan {@link PropertyColumn} gyermekosztály, ami egy Date típusú propertyt
 * tud megjeleníteni ÉÉÉÉ.HH.NN. formátumban.
 *
 * @author  messo
 * @since   2.3.1
 */
public class DatePropertyColumn<T> extends PropertyColumn<T, String> {

    private static final String datePattern = "yyyy.MM.dd.";

    /**
     * Creates a date property column that is also sortable
     *
     * @param displayModel          display model
     * @param sortProperty          sort property
     * @param propertyExpression    wicket property expression used by PropertyModel
     */
    public DatePropertyColumn(IModel<String> displayModel, String sortProperty,
            String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    /**
     * Creates a non sortable date property column
     *
     * @param displayModel          display model
     * @param propertyExpression    wicket property expression
     */
    public DatePropertyColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, null, propertyExpression);
    }

    /**
     * Implementation of populateItem which adds a label to the cell whose model is the provided
     * property expression evaluated against rowModelObject
     *
     * @see ICellPopulator#populateItem(Item, String, IModel)
     */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Model<Date> dateModel = Model.of((Date) getDataModel(rowModel).getObject());
        item.add(DateLabel.forDatePattern(componentId,
                dateModel, datePattern));
    }
}
