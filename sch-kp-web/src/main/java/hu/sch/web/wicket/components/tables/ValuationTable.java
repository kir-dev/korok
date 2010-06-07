/**
 * Copyright (c) 2009-2010, Peter Major
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

/**
 * Értékelési táblázat, amely neveket, pont- és belépőkérelmeket tartalmaz.
 * A fontosabb oszlopok alapján rendezhető természetesen
 *
 * @author  messo
 * @since   2.3.1
 * @see     AjaxFallbackDefaultDataTable
 * @see     ValuationData
 */
public class ValuationTable implements Serializable {

    AjaxFallbackDefaultDataTable<ValuationData> table;

    public ValuationTable(String id, List<ValuationData> items, int rowsPerPage) {
        List<IColumn<ValuationData>> columns = new ArrayList<IColumn<ValuationData>>(4);
        columns.add(new AbstractColumn<ValuationData>(new Model<String>("Név"), MySortableDataProvider.SORT_BY_NAME) {

            @Override
            public void populateItem(Item<ICellPopulator<ValuationData>> cellItem, String componentId, IModel<ValuationData> rowModel) {
                cellItem.add(new UserLink(componentId, rowModel.getObject().getUser()));
            }
        });
        columns.add(new PropertyColumn<ValuationData>(new Model<String>("Pont"), MySortableDataProvider.SORT_BY_POINT, "pointRequest.point"));
        columns.add(new PropertyColumn<ValuationData>(new Model<String>("Belépő típusa"), MySortableDataProvider.SORT_BY_ENTRANT, "entrantRequest.entrantType"));
        columns.add(new PropertyColumn<ValuationData>(new Model<String>("Szöveges értékelés"), "entrantRequest.valuationText"));

        table = new AjaxFallbackDefaultDataTable<ValuationData>(id, columns, new MySortableDataProvider(items), rowsPerPage);
    }

    public AjaxFallbackDefaultDataTable<ValuationData> getDataTable() {
        return table;
    }

    class MySortableDataProvider extends SortableDataProvider<ValuationData> {

        private final List<ValuationData> items;
        public static final String SORT_BY_NAME = "name";
        public static final String SORT_BY_POINT = "point";
        public static final String SORT_BY_ENTRANT = "entrant";

        public MySortableDataProvider(List<ValuationData> items) {
            this.items = items;
            setSort(SORT_BY_NAME, true);
        }

        @Override
        public Iterator<ValuationData> iterator(int first, int count) {
            // Nagy adathalmaznál jobb lenne DB-be rendezni, de ez olyan
            // kismértékű adat, hogy nem számottevő ráadásul a lista nem a
            // DB-ből jön közvetlenül, így nehéz is lenne megoldani ;)

            sort();
            return items.subList(first, first + count).iterator();
        }

        @Override
        public int size() {
            return items.size();
        }

        @Override
        public IModel<ValuationData> model(ValuationData valData) {
            return new LoadableDetachableModel<ValuationData>(valData) {

                @Override
                protected ValuationData load() {
                    return new ValuationData(null, null, null);
                }
            };
        }

        private void sort() {
            String prop = getSort().getProperty();
            final int asc = getSort().isAscending() ? 1 : -1;

            if (prop == null) {
                return;
            }

            if (prop.equals(SORT_BY_NAME)) {
                Collections.sort(items, new Comparator<ValuationData>() {

                    @Override
                    public int compare(ValuationData v1, ValuationData v2) {
                        return asc * v1.getUser().compareTo(v2.getUser());
                    }
                });
            } else if (prop.equals(SORT_BY_POINT)) {
                Collections.sort(items, new Comparator<ValuationData>() {

                    @Override
                    public int compare(ValuationData v1, ValuationData v2) {
                        return asc * v1.getPointRequest().getPoint().compareTo(v2.getPointRequest().getPoint());
                    }
                });
            } else if (prop.equals(SORT_BY_ENTRANT)) {
                Collections.sort(items, new Comparator<ValuationData>() {

                    @Override
                    public int compare(ValuationData v1, ValuationData v2) {
                        return asc * v1.getEntrantRequest().getEntrantType().compareTo(v2.getEntrantRequest().getEntrantType());
                    }
                });
            }
        }
    }
}
