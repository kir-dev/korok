/**
 * Copyright (c) 2008-2010, Peter Major All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Peter Major nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission. * All advertising
 * materials mentioning features or use of this software must display the
 * following acknowledgement: This product includes software developed by the
 * Kir-Dev Team, Hungary and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Peter Major BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.web.wicket.components.tables;

import hu.sch.domain.ValuationData;
import hu.sch.services.UserManagerLocal;
import java.io.Serializable;
import java.util.*;
import javax.ejb.EJB;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * Értékelési táblázat, amely {@link ValuationData}-kat tud megjeleníteni. A
 * fontosabb oszlopok alapján rendezhető, de ezt a konkrét implementáció dönti
 * el.
 *
 * @author messo
 * @since 2.3.1
 * @see AjaxFallbackDefaultDataTable
 * @see ValuationData
 */
public abstract class ValuationTable implements Serializable {

    AjaxFallbackDefaultDataTable<ValuationData> table;
    MySortableDataProvider provider;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    protected boolean isShowSvieColumn = false;

    protected ValuationTable() {
    }

    /**
     * Konstruktor, amely létrehozza a táblázatot a megfelelő oszlopokkal és
     * adatokkal
     *
     * @param id a Wicket ID-ja annak a table-nek, ahova be akarjuk majd szúrni
     * a táblázatot
     * @param items a lista, amit a táblázatban meg akarunk jeleníteni
     * @param rowsPerPage egy oldalon hány elem jelenjne meg?
     * @param showSvieColumn megjelenjen-e a SVIE státusz oszlop
     */
    public ValuationTable(final String id, final List<ValuationData> items,
            final int rowsPerPage, final boolean showSvieColumn) {

        init();

        provider = new MySortableDataProvider(items);
        this.isShowSvieColumn = showSvieColumn;

        final List<IColumn<ValuationData>> columns = new ArrayList<IColumn<ValuationData>>(5);
        populateColumns(columns);

        table = new AjaxFallbackDefaultDataTable<ValuationData>(id, columns, provider, rowsPerPage);
    }

    protected abstract void populateColumns(List<IColumn<ValuationData>> columns);

    public AjaxFallbackDefaultDataTable<ValuationData> getDataTable() {
        return table;
    }

    private void init() {
        Injector.get().inject(this);
    }

    /**
     * Frissítjük a listát.
     *
     * @param list az új lista, amit a táblázatban szerepeltetni szeretnénk
     */
    public void updateList(List<ValuationData> list) {
        provider.items = list;
    }

    static class MySortableDataProvider extends SortableDataProvider<ValuationData> {

        private List<ValuationData> items;
        public static final String SORT_BY_USER = "user";
        public static final String SORT_BY_POINT = "point";
        public static final String SORT_BY_ENTRANT = "entrant";
        public static final String SORT_BY_SEMESTER = "semester";
        public static final String SORT_BY_GROUP = "group";

        public MySortableDataProvider(List<ValuationData> items) {
            this.items = items;
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

            if (prop.equals(SORT_BY_USER)) {
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
            } else if (prop.equals(SORT_BY_SEMESTER)) {
                Collections.sort(items, new Comparator<ValuationData>() {

                    @Override
                    public int compare(ValuationData v1, ValuationData v2) {
                        return asc * v1.getSemester().compareTo(v2.getSemester());
                    }
                });
            } else if (prop.equals(SORT_BY_GROUP)) {
                Collections.sort(items, new Comparator<ValuationData>() {

                    @Override
                    public int compare(ValuationData v1, ValuationData v2) {
                        return asc * v1.getGroup().compareTo(v2.getGroup());
                    }
                });
            }
        }
    }
}
