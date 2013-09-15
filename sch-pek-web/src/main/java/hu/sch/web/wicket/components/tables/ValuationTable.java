package hu.sch.web.wicket.components.tables;

import hu.sch.domain.ValuationData;
import hu.sch.services.MembershipManagerLocal;
import java.io.Serializable;
import java.util.*;
import javax.inject.Inject;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
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

    AjaxFallbackDefaultDataTable<ValuationData, String> table;
    MySortableDataProvider provider;
    @Inject
    protected MembershipManagerLocal membershipManager;
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

        provider = new MySortableDataProvider(items);
        this.isShowSvieColumn = showSvieColumn;

        final List<IColumn<ValuationData, String>> columns = new ArrayList<IColumn<ValuationData, String>>(5);
        populateColumns(columns);

        table = new AjaxFallbackDefaultDataTable<ValuationData, String>(id, columns, provider, rowsPerPage);
    }

    protected abstract void populateColumns(List<IColumn<ValuationData, String>> columns);

    public AjaxFallbackDefaultDataTable<ValuationData, String> getDataTable() {
        return table;
    }

    /**
     * Frissítjük a listát.
     *
     * @param list az új lista, amit a táblázatban szerepeltetni szeretnénk
     */
    public void updateList(List<ValuationData> list) {
        provider.items = list;
    }

    static class MySortableDataProvider extends SortableDataProvider<ValuationData, String> {

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
        public Iterator<ValuationData> iterator(long first, long count) {
            // Nagy adathalmaznál jobb lenne DB-be rendezni, de ez olyan
            // kismértékű adat, hogy nem számottevő ráadásul a lista nem a
            // DB-ből jön közvetlenül, így nehéz is lenne megoldani ;)

            sort();
            return items.subList((int) first, (int) (first + count)).iterator();
        }

        @Override
        public long size() {
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
