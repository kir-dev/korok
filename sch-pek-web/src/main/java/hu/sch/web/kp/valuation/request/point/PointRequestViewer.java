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
package hu.sch.web.kp.valuation.request.point;

import hu.sch.domain.PointRequest;
import hu.sch.domain.User;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationStatistic;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.components.customlinks.UserLink;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 * @author messo
 */
public class PointRequestViewer extends Panel {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public PointRequestViewer(String id, final Valuation val) {
        super(id);

        // TODO(messo): ehhez valami rendesebb query-t pakoljunk ide, nem kell két query.
        final List<User> activeMembers = userManager.getCsoporttagokWithoutOregtagok(val.getGroup().getId());
        final List<PointRequest> requests = valuationManager.findPontIgenyekForErtekeles(val.getId());

        /*/tagok és igények összefésülése
        if (requests.isEmpty()) {
            for (User f : activeMembers) {
                requests.add(new PointRequest(f, 0));
            }
        }*/

        add(new ListView<PointRequest>("requests", requests) {

            @Override
            protected void populateItem(ListItem<PointRequest> item) {
                final PointRequest p = item.getModelObject();
                item.setModel(new CompoundPropertyModel<PointRequest>(p));
                item.add(new UserLink("userLink", p.getUser()));
                item.add(new Label("user.nickName"));
                item.add(new Label("point"));
            }
        });

        ValuationStatistic stat = valuationManager.getStatisticForValuation(val.getId());
        add(new Label("stat.averagePoint", stat.getAveragePoint().toString()));
        add(new Label("stat.sumPoint", stat.getSummaPoint().toString()));

        if (requests.isEmpty())
            getSession().info(getLocalizer().getString("info.NincsErtekeles", this.getParent() ));
    }
}
