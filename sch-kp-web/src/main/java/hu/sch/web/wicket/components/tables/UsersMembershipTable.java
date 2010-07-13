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
import hu.sch.web.wicket.util.SortableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

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
        columns.add(new PanelColumn<Membership>("Kör neve", Membership.SORT_BY_GROUP) {

            @Override
            public Panel getPanel(String componentId, Membership ms) {
                return new GroupLink(componentId, ms.getGroup());
            }
        });
        columns.add(new PropertyColumn<Membership>(new Model<String>("Betöltött poszt"), Membership.SORT_BY_POSTS, "membership"));
        columns.add(new DateIntervalPropertyColumn<Membership>(
                new Model<String>("Tagsági idő"), Membership.SORT_BY_INTERVAL, "start", "end"));
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
                protected LinkPanel getLinkPanel(String componentId, Membership ms) {
                    return new OldBoyLinkPanel(componentId, ms);
                }
            });
        }

        table = new AjaxFallbackDefaultDataTable<Membership>(id, columns, provider, rowsPerPage);
        provider.setSort(Membership.SORT_BY_GROUP, true);
    }

    public AjaxFallbackDefaultDataTable<Membership> getDataTable() {
        return table;
    }

    protected abstract void onWannabeOldBoy(Membership ms);

    private class MySortableDataProvider extends SortableDataProvider<Membership> {

        private SortableList<Membership> items;

        private MySortableDataProvider(List<Membership> items) {
            this.items = new SortableList<Membership>(items);
        }

        @Override
        public Iterator<? extends Membership> iterator(int first, int count) {
            items.sort(getSort());
            return items.getList().subList(first, first + count).iterator();
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
    }
}
