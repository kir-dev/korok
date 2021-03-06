package hu.sch.web.wicket.components.tables;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author messo
 * @since 2.3.1
 */
public class DateIntervalPropertyColumn<T> extends PropertyColumn<T, String> {

    private final String endPropertyExpression;
    private final DateFormat df = new SimpleDateFormat("yyyy.MM.dd.");

    /**
     * Creates a date property column that is also sortable
     *
     * @param displayModel            display model
     * @param sortProperty            sort property
     * @param startPropertyExpression wicket property expression used by
     * PropertyModel
     * @param endPropertyExpression   wicket property expression used by
     * PropertyModel
     */
    public DateIntervalPropertyColumn(IModel<String> displayModel, String sortProperty,
            String startPropertyExpression, String endPropertyExpression) {
        super(displayModel, sortProperty, startPropertyExpression);
        this.endPropertyExpression = endPropertyExpression;
    }

    /**
     * Creates a non sortable date property column
     *
     * @param displayModel       display model
     * @param propertyExpression wicket property expression
     * @see PropertyModel
     */
    public DateIntervalPropertyColumn(IModel<String> displayModel, String propertyExpression, String endPropertyExpression) {
        super(displayModel, null, propertyExpression);
        this.endPropertyExpression = endPropertyExpression;
    }

    /**
     * Implementation of populateItem which adds a label to the cell whose model
     * is the provided property expression evaluated against rowModelObject
     *
     * @see ICellPopulator#populateItem(Item, String, IModel)
     */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Date start = (Date) getDataModel(rowModel).getObject();
        Date end = (Date) new PropertyModel(rowModel, endPropertyExpression).getObject();

        StringBuilder sb = new StringBuilder(df.format(start));
        sb.append(" - ");
        if (end != null) {
            sb.append(df.format(end));
        }

        item.add(new Label(componentId, sb.toString()));
        item.add(AttributeModifier.replace("style", "width: 180px"));
    }
}
