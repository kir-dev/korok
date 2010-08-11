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

package hu.sch.web.kp.search;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.wicket.components.customlinks.GroupLink;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.wicket.components.tables.PanelColumn;
import hu.sch.web.wicket.util.SortableGroupDataProvider;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author aldaris
 */
public class GroupResultPanel extends Panel {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public GroupResultPanel(String id, List<Group> groups) {
        super(id);

        InjectorHolder.getInjector().inject(this);

        List<IColumn<Group>> columns = new ArrayList<IColumn<Group>>();
        columns.add(new PanelColumn<Group>("Név", "name") {

            @Override
            protected Panel getPanel(String componentId, Group g) {
                return new GroupLink(componentId, g);
            }
        });
        columns.add(new PanelColumn<Group>("Körvezető neve") {

            @Override
            protected Panel getPanel(String componentId, Group g) {
                // FIXME: ez így nagyon gány, minden egyes sorhoz külön query???
                User korvezeto = userManager.getGroupLeaderForGroup(g.getId());
                return new UserLink(componentId, korvezeto);
            }
        });

        SortableGroupDataProvider provider = new SortableGroupDataProvider(groups);
        AjaxFallbackDefaultDataTable table = new AjaxFallbackDefaultDataTable("groupTable", columns, provider, 50);
        add(table);
    }
}
