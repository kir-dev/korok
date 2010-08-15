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

import hu.sch.domain.ValuationData;
import hu.sch.web.wicket.components.customlinks.UserLink;
import java.util.List;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Értékelési táblázat, amely neveket, pont- és belépőkérelmeket tartalmaz.
 * Ezt használjuk egy kör korábbi értékeléseinek megjelenítéséhez.
 *
 * @author  messo
 * @since   2.3.1
 */
public class ValuationTableForGroup extends ValuationTable {

    public ValuationTableForGroup(String id, List<ValuationData> items, int rowsPerPage) {
        super(id, items, rowsPerPage);
        provider.setSort(MySortableDataProvider.SORT_BY_USER, true);
    }

    public ValuationTableForGroup(String id, List<ValuationData> items) {
        this(id, items, 20);
    }

    @Override
    protected void populateColumns(List<IColumn<ValuationData>> columns) {
        columns.add(new PanelColumn<ValuationData>("Név", MySortableDataProvider.SORT_BY_USER) {

            @Override
            protected Panel getPanel(String componentId, ValuationData vd) {
                return new UserLink(componentId, vd.getUser());
            }
        });

        columns.add(new PropertyColumn<ValuationData>(new Model<String>("Pont"), MySortableDataProvider.SORT_BY_POINT, "pointRequest.point"));
        columns.add(new PropertyColumn<ValuationData>(new Model<String>("Belépő típusa"), MySortableDataProvider.SORT_BY_ENTRANT, "entrantRequest.entrantType"));
        columns.add(new PropertyColumn<ValuationData>(new Model<String>("Szöveges értékelés"), "entrantRequest.valuationText") {

            @Override
            public void populateItem(Item<ICellPopulator<ValuationData>> item, String componentId, IModel<ValuationData> rowModel) {
                super.populateItem(item, componentId, rowModel);
                item.add(new SimpleAttributeModifier("style", "width: 400px"));
            }
        });
    }
}
