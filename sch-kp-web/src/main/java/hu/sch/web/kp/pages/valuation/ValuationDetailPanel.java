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

package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Valuation;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.services.ValuationManagerLocal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class ValuationDetailPanel extends Panel {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    private List<PointRequest> pointRequests = new ArrayList<PointRequest>();
    private List<EntrantRequest> entrantRequests = new ArrayList<EntrantRequest>();
    private MultiLineLabel valuationText;

    public ValuationDetailPanel(String id) {
        super(id);
        generateValuationText();
        generatePointTable();
        generateEntrantTable();
    }

    public void generateValuationText() {
        valuationText = new MultiLineLabel("valuationText");
        valuationText.setEscapeModelStrings(false);
        add(valuationText);
    }

    public void generatePointTable() {
        WebMarkupContainer pointTable = new WebMarkupContainer("pointTable");
        ListView<PointRequest> pontListView = new ListView<PointRequest>("points", pointRequests) {

            @Override
            protected void populateItem(ListItem<PointRequest> item) {
                final PointRequest p = item.getModelObject();
                IModel<PointRequest> model = new CompoundPropertyModel<PointRequest>(p);
                item.setModel(model);
                Link userLink = new Link("userLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(ShowUser.class,
                                new PageParameters("id=" + p.getUser().getId().toString()));
                    }
                };
                userLink.add(new Label("userName", p.getUser().getName()));
                item.add(userLink);
                item.add(new Label("point", p.getPoint().toString()));
            }
        };
        pointTable.add(pontListView);
        add(pointTable);
    }

    public void generateEntrantTable() {
        WebMarkupContainer entrantTable = new WebMarkupContainer("entrantTable");
        ListView<EntrantRequest> entrantList = new ListView<EntrantRequest>("entrants", entrantRequests) {

            @Override
            protected void populateItem(ListItem<EntrantRequest> item) {
                final EntrantRequest b = item.getModelObject();
                IModel<EntrantRequest> model = new CompoundPropertyModel<EntrantRequest>(b);
                item.setModel(model);
                Link userLink = new Link("userLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(ShowUser.class,
                                new PageParameters("id=" + b.getUser().getId().toString()));
                    }
                };
                userLink.add(new Label("userName", b.getUser().getName()));
                item.add(userLink);
                item.add(new Label("entrantType"));
                item.add(new Label("valuationText"));
            }
        };
        entrantTable.add(entrantList);
        add(entrantTable);
    }

    public void updateDatas(Valuation ertekeles) {
        if (ertekeles != null) {
            pointRequests.clear();
            pointRequests.addAll(valuationManager.findPontIgenyekForErtekeles(ertekeles.getId()));
            entrantRequests.clear();
            entrantRequests.addAll(valuationManager.findBelepoIgenyekForErtekeles(ertekeles.getId()));
            valuationText.setDefaultModel(new Model<String>(ertekeles.getValuationText()));
        }
    }
}
