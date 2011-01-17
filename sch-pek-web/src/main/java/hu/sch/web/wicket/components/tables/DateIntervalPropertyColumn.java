/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package hu.sch.web.wicket.components.tables;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author  messo
 * @since   2.3.1
 */
public class DateIntervalPropertyColumn<T> extends PropertyColumn<T> {

    private final String endPropertyExpression;
    private final DateFormat df = new SimpleDateFormat("yyyy.MM.dd.");

    /**
     * Creates a date property column that is also sortable
     *
     * @param displayModel
     *            display model
     * @param sortProperty
     *            sort property
     * @param startPropertyExpression
     *            wicket property expression used by PropertyModel
     * @param endPropertyExpression
     *            wicket property expression used by PropertyModel
     */
    public DateIntervalPropertyColumn(IModel<String> displayModel, String sortProperty,
            String startPropertyExpression, String endPropertyExpression) {
        super(displayModel, sortProperty, startPropertyExpression);
        this.endPropertyExpression = endPropertyExpression;
    }

    /**
     * Creates a non sortable date property column
     *
     * @param displayModel
     *            display model
     * @param propertyExpression
     *            wicket property expression
     * @see PropertyModel
     */
    public DateIntervalPropertyColumn(IModel<String> displayModel, String propertyExpression, String endPropertyExpression) {
        super(displayModel, null, propertyExpression);
        this.endPropertyExpression = endPropertyExpression;
    }

    /**
     * Implementation of populateItem which adds a label to the cell whose model is the provided
     * property expression evaluated against rowModelObject
     *
     * @see ICellPopulator#populateItem(Item, String, IModel)
     */
    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Date start = (Date) createLabelModel(rowModel).getObject();
        Date end = (Date) new PropertyModel(rowModel, endPropertyExpression).getObject();

        StringBuilder sb = new StringBuilder(df.format(start));
        sb.append(" - ");
        if (end != null) {
            sb.append(df.format(end));
        }

        item.add(new Label(componentId, sb.toString()));
        item.add(new SimpleAttributeModifier("style", "width: 180px"));
    }
}
