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
import hu.sch.web.wicket.components.customlinks.GroupLink;
import hu.sch.web.wicket.components.customlinks.LinkPanel;
import hu.sch.web.wicket.components.customlinks.OldBoyLinkPanel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Application;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;

/**
 * Itt listázzuk ki a felhasználó tagságait.
 *
 * @author  messo
 * @since   2.3.1
 */
public abstract class UsersMembershipTable implements Serializable {

    AjaxFallbackDefaultDataTable<Membership> table;
    MySortableDataProvider provider;

    public UsersMembershipTable(String id, List<Membership> memberships, boolean isOwnProfile, int rowsPerPage) {
        provider = new MySortableDataProvider(memberships);

        List<IColumn<Membership>> columns = new ArrayList<IColumn<Membership>>(5);
        columns.add(new PanelColumn<Membership>("Kör neve", MySortableDataProvider.SORT_BY_GROUP) {

            @Override
            public Panel getPanel(String componentId, Membership ms) {
                return new GroupLink(componentId, ms.getGroup());
            }
        });
        columns.add(new PropertyColumn<Membership>(new Model<String>("Betöltött poszt"), MySortableDataProvider.SORT_BY_POST, "membership"));
        columns.add(new DateIntervalPropertyColumn<Membership>(
                new Model<String>("Tagsági idő"), MySortableDataProvider.SORT_BY_INTERVAL, "start", "end"));
        if (isOwnProfile) {
            // csak akkor kell ez az oszlop, ha ez a mi táblázatunk
            columns.add(new LinkColumn<Membership>("Öregtaggá válás?") {

                @Override
                protected boolean isVisible(Membership ms) {
                    return ms.getEnd() == null;
                }

                @Override
                public void onClick(Membership ms) {
                    onWannabeOldBoy(ms);
                }

                @Override
                protected LinkPanel getLinkPanel(String componentId) {
                    return new OldBoyLinkPanel(componentId);
                }
            });
        }

        table = new AjaxFallbackDefaultDataTable<Membership>(id, columns, provider, rowsPerPage);
        provider.setSort(MySortableDataProvider.SORT_BY_GROUP, true);
    }

    public AjaxFallbackDefaultDataTable<Membership> getDataTable() {
        return table;
    }

    protected abstract void onWannabeOldBoy(Membership ms);

    private class MySortableDataProvider extends SortableDataProvider<Membership> {

        public static final String SORT_BY_GROUP = "group";
        public static final String SORT_BY_POST = "post";
        public static final String SORT_BY_INTERVAL = "semester";

        private List<Membership> items;

        private MySortableDataProvider(List<Membership> items) {
            this.items = items;
        }

        @Override
        public Iterator<? extends Membership> iterator(int first, int count) {
            sort();
            return items.subList(first, first + count).iterator();
        }

        @Override
        public int size() {
            return items.size();
        }

        @Override
        public IModel<Membership> model(Membership ms) {
            return new LoadableDetachableModel<Membership>(ms) {

                @Override
                protected Membership load() {
                    // TODO(messo): ilyenkor mi van? :)
                    return new Membership();
                }
            };
        }

        private void sort() {
            String prop = getSort().getProperty();
            final int asc = getSort().isAscending() ? 1 : -1;

            if (prop.equals(SORT_BY_GROUP)) {
                Collections.sort(items, new Comparator<Membership>() {

                    @Override
                    public int compare(Membership ms1, Membership ms2) {
                        return asc * ms1.getGroup().compareTo(ms2.getGroup());
                    }
                });
            } else if(prop.equals(SORT_BY_POST)) {
                final IConverter c = Application.get().getConverterLocator().getConverter(Membership.class);
                final Locale hu = new Locale("hu");
                Collections.sort(items, new Comparator<Membership>() {

                    @Override
                    public int compare(Membership ms1, Membership ms2) {
                        return asc * c.convertToString(ms1, hu).compareTo(c.convertToString(ms2, hu));
                    }
                });
            } else if (prop.equals(SORT_BY_INTERVAL)) {
                Collections.sort(items, new Comparator<Membership>() {

                    @Override
                    public int compare(Membership ms1, Membership ms2) {
                        int ret = ms1.getStart().compareTo(ms2.getStart());
                        if (ret == 0 && ms1.getEnd() != null && ms2.getEnd() != null) {
                            return asc * ms1.getEnd().compareTo(ms2.getEnd());
                        } else {
                            return asc * ret;
                        }
                    }
                });
            }
        }
    }
}
