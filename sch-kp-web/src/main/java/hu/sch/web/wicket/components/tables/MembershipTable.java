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

import hu.sch.domain.Membership;
import hu.sch.domain.interfaces.MembershipTableEntry;
import hu.sch.web.wicket.components.CheckBoxHolder;
import hu.sch.web.wicket.components.customlinks.UserLink;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Application;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;

/**
 * Egy felhasználókat listázó táblázat, amely olyan objektumokat tud listázni, melyek
 * implementálják a {@link MembershipTableEntry} interfészt. Azért van erre szükség,
 * mert lehet, hogy sima {@link hu.sch.domain.Membership} objektumokat akarunk listázni,
 * de lehet, hogy egy wrapper segítségével kívánjuk mindezt megtenni.
 *
 * @author      messo
 * @since       2.3.1
 * @see         AjaxFallbackDefaultDataTable
 */
public abstract class MembershipTable<T extends MembershipTableEntry> implements Serializable {

    protected AjaxFallbackDefaultDataTable<T> table;
    public static final Collator huCollator = Collator.getInstance(new Locale("hu"));
    // Ezek azért vannak itt, nem pedig a Provider-ben, hogy kívülről is hozzá lehessen
    // férni, és ehhez ne kelljen az egész Providert kifelé láthatóvá tenni
    public static final String SORT_BY_NAME = "name";
    public static final String SORT_BY_NICKNAME = "nickname";
    public static final String SORT_BY_MEMBERSHIP = "membership";
    public static final String SORT_BY_SVIE = "svie";
    public static final String SORT_BY_MEMBERSHIP_DURATION = "membershipStartEnd";

    /**
     * Konstruktor, amely létrehoz egy {@link AjaxFallbackDefaultDataTable} táblázatot
     * néhány oszloppal, illetve lehetőséget ad arra, hogy egy konkrét osztályban még
     * több oszlopot adhassunk hozzá. Amennyiben az elemek implementálják a {@link SelectableEntry}
     * interfészt, akkor a táblázat jobb szélén megjelenik 1-1 checkbox, hogy ki lehessen
     * jelölni az adott bejegyzést.
     *
     * @param id            táblázat wicket id-ja
     * @param items         az itemek amiket listázni szeretnénk
     * @param rowsPerPage   hány oldal jelenjen meg egy oldal?
     * @param c             az elemek Class objektuma, azért, hogy lekérdezhessük,
     *                      hogy implementálja-e a {@link SelectableEntry} interfészt
     */
    public MembershipTable(String id, List<T> items, int rowsPerPage, Class<T> c) {
        List<IColumn<T>> columns = new ArrayList<IColumn<T>>();
        columns.add(new AbstractColumn<T>(new Model<String>("Felhasználó neve"), SORT_BY_NAME) {

            @Override
            public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                cellItem.add(new UserLink(componentId, rowModel.getObject().getMembership().getUser()));
            }
        });
        columns.add(new PropertyColumn<T>(new Model<String>("Becenév"), SORT_BY_NICKNAME, "membership.user.nickName"));
        columns.add(new PropertyColumn<T>(new Model<String>("Betöltött poszt"), SORT_BY_MEMBERSHIP, "membership"));

        onPopulateColumns(columns);

        if (SelectableEntry.class.isAssignableFrom(c)) {
            columns.add(new AbstractColumn<T>(new Model<String>(""), "checkbox") {

                @Override
                public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
                    cellItem.add(new CheckBoxHolder<T>(componentId, rowModel, "selected"));
                }
            });
        }

        table = new AjaxFallbackDefaultDataTable<T>(id, columns, new SortableMembershipDataProvider<T>(items), rowsPerPage);
    }

    /**
     * Alapértelmezetten 50 emberkét jelenítsünk meg.
     *
     * @param id
     * @param items
     * @param c
     * @see MembershipTable#MembershipTable(java.lang.String, java.util.List, int, java.lang.Class) 
     */
    public MembershipTable(String id, List<T> items, Class<T> c) {
        this(id, items, 50, c);
    }

    /**
     * Ezt kell a konkrét osztályokban megvalósítani, hogy egyedi oszlopokat is
     * hozzáadhassunk a meglévőkhöz
     *
     * @param columns   az oszloplista, amihez új oszlopokat adhatunk
     */
    public abstract void onPopulateColumns(List<IColumn<T>> columns);

    /**
     * Lekérjük a tényleges táblázatot, ami egy {@link AjaxFallbackDefaultDataTable} típusú
     * táblázat. Ezt akkor hívjuk meg praktikusan, amikor hozzáakarjuk adni a DOM-hoz
     * a kész táblázatot.
     *
     * @return  táblázat
     */
    public AjaxFallbackDefaultDataTable<T> getDataTable() {
        return table;
    }

    class SortableMembershipDataProvider<T extends MembershipTableEntry> extends SortableDataProvider<T> {

        private List<T> items;

        public SortableMembershipDataProvider(List<T> items) {
            this.items = items;
            setSort(SORT_BY_NAME, true);
        }

        @Override
        public Iterator<T> iterator(int first, int count) {
            SortParam sp = getSort();
            return getIndex(sp.getProperty(), sp.isAscending()).subList(first, first + count).iterator();
        }

        @Override
        public int size() {
            return items.size();
        }

        public List<T> getIndex(String prop, boolean asc) {
            // ha növekvő 1 (nem változik az irány), ha csökkenő, akkor minden
            // eredményt negálunk.
            final int r = asc ? 1 : -1;

            if (prop == null) {
                return items;
            }
            if (prop.equals(SORT_BY_NAME)) {
                Collections.sort(items, new Comparator<T>() {

                    @Override
                    public int compare(T o1, T o2) {
                        return r * o1.getMembership().getUser().compareTo(o2.getMembership().getUser());
                    }
                });
            } else if (prop.equals(SORT_BY_NICKNAME)) {
                Collections.sort(items, new Comparator<T>() {

                    @Override
                    public int compare(T t0, T t1) {
                        // bezavar a buliba, ha valamelyik null. Ezeket inkább kézzel
                        // lekezeljük: rakjuk ezeket a sor elejére a ""-vel együtt.
                        String s0 = t0.getMembership().getUser().getNickName();
                        String s1 = t1.getMembership().getUser().getNickName();
                        if (s0 == null) {
                            return r * -1;
                        }
                        if (s1 == null) {
                            return r * 1;
                        }
                        return r * huCollator.compare(s0, s1);
                    }
                });
            } else if (prop.equals(SORT_BY_MEMBERSHIP)) {
                Collections.sort(items, new Comparator<T>() {

                    @Override
                    public int compare(T t0, T t1) {
                        IConverter ms = Application.get().getConverterLocator().getConverter(Membership.class);
                        Locale hu = new Locale("hu");
                        return r * ms.convertToString(t0.getMembership(), hu).compareTo(ms.convertToString(t1.getMembership(), hu));
                    }
                });
            } else if (prop.equals(SORT_BY_SVIE)) {
                Collections.sort(items, new Comparator<T>() {

                    @Override
                    public int compare(T t0, T t1) {
                        return r * t0.getMembership().getUser().compareToBySvieMemberText(t1.getMembership().getUser());
                    }
                });
            } else if (prop.equals(SORT_BY_MEMBERSHIP_DURATION)) {
                Collections.sort(items, new Comparator<T>() {

                    @Override
                    public int compare(T t0, T t1) {
                        int ret = t0.getMembership().getStart().compareTo(t1.getMembership().getStart());
                        if (ret == 0 && t0.getMembership().getEnd() != null) {
                            return r * t0.getMembership().getEnd().compareTo(t1.getMembership().getEnd());
                        } else {
                            return r * ret;
                        }
                    }
                });
            } else {
                throw new RuntimeException("uknown sort option [" + prop
                        + "]. valid options: [name] , [svieMembershipType]");
            }

            return items;
        }

        @Override
        public IModel<T> model(T object) {
            return new Model<T>(object);
        }
    }
}
