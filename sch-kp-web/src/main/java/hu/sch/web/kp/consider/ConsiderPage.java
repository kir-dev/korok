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
package hu.sch.web.kp.consider;

import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.ValuationStatus;
import hu.sch.web.wicket.components.choosers.ValuationStatusChooser;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.kp.valuation.ValuationMessages;
import hu.sch.web.kp.valuation.EntrantRequestViewer;
import hu.sch.web.kp.valuation.PointRequestViewer;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.kp.KorokPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
//TODO
public class ConsiderPage extends KorokPageTemplate {

    @EJB(name = "ValuationManagerBean")
    private ValuationManagerLocal valuationManager;
    Map<Long, ConsideredValuation> underConsidering = new HashMap<Long, ConsideredValuation>();

    public Map<Long, ConsideredValuation> getUnderConsidering() {
        return underConsidering;
    }

    public void setUnderConsidering(Map<Long, ConsideredValuation> underConsidering) {
        this.underConsidering = underConsidering;
    }

    public ConsiderPage() {
        // jogosultság ellenőrzés
        if (!isCurrentUserJETI()) {
            getSession().error("Nincs jogod a megadott művelethez");
            throw new RestartResponseException(GroupHierarchy.class);
        }

        // időszak ellenőrzés
        if (systemManager.getErtekelesIdoszak() != ValuationPeriod.ERTEKELESELBIRALAS) {
            getSession().error("Nincs elbírálási időszak!");
            throw new RestartResponseException(GroupHierarchy.class);
        }

        setHeaderLabelText("Leadott értékelések elbírálása");
        add(new Label("semester", getSemester().toString()));
        IDataProvider<ValuationStatistic> dp = new ValuationStatisticDataProvider(
                valuationManager.findValuationStatisticForSemester());

        Form form = new Form("considerForm") {

            @Override
            protected void onSubmit() {
                List<ConsideredValuation> list = new ArrayList<ConsideredValuation>();

                for (ConsideredValuation consideredValuation : getUnderConsidering().values()) {
                    //if ((elbiraltertekeles.getPointStatus().equals(ValuationStatus.ELBIRALATLAN) && (elbiraltertekele)

                    // Ha valtozott valamelyik belepokerelemhez vagy pontkerelemhez tartozo legordulo,
                    if ((consideredValuation.getPointStatus() != consideredValuation.getValuation().getPointStatus())
                            || (consideredValuation.getEntrantStatus() != consideredValuation.getValuation().getEntrantStatus())) {
                        list.add(consideredValuation);
                    }
                }

                if (!hasError() && list.isEmpty()) {
                    error("Nem bíráltál el egy értékelést sem!");
                }
                if (!hasError()) {
                    setResponsePage(new ConsiderExplainPage(list));
                }
            }
        };
        form.add(new KeepAliveBehavior());
        add(form);

        form.add(new DataView<ValuationStatistic>("valuationList", dp) {

            @Override
            protected void populateItem(Item<ValuationStatistic> item) {
                final Valuation val = item.getModelObject().getValuation();

                ConsideredValuation cv = null;
                if (!getUnderConsidering().containsKey(val.getId())) {
                    cv = new ConsideredValuation(val, val.getPointStatus(), val.getEntrantStatus());
                    getUnderConsidering().put(val.getId(), cv);
                } else {
                    cv = getUnderConsidering().get(val.getId());
                }

                Link valuationLink = new Link("valuationLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ValuationDetails(val, getPage()));
                    }
                };
                item.add(valuationLink);
                valuationLink.add(new Label("valuation.group.name"));
                Link givenKDOLink = new Link("givenKDOLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new EntrantRequestViewer(val));
                    }
                };
                givenKDOLink.add(new Label("givenKDO"));

                Link givenKBLink = new Link("givenKBLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new EntrantRequestViewer(val));
                    }
                };
                givenKBLink.add(new Label("givenKB"));

                Link givenABLink = new Link("givenABLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new EntrantRequestViewer(val));
                    }
                };
                givenABLink.add(new Label("givenAB"));

                Link pointLink = new Link("pointLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new PointRequestViewer(val));
                    }
                };
                pointLink.add(new Label("averagePoint"));

                Link summaPointLink = new Link("summaPointLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new PointRequestViewer(val));
                    }
                };
                summaPointLink.add(new Label("summaPoint"));

                item.add(givenKDOLink);
                item.add(givenKBLink);
                item.add(givenABLink);

                item.add(pointLink);
                item.add(summaPointLink);

                Link messagesLink = new Link("messagesLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ValuationMessages(val.getId()));
                    }
                };
                item.add(messagesLink);

                Component pointStatus = new ValuationStatusChooser("pointStatus");
                Component entrantStatus = new ValuationStatusChooser("entrantStatus");
                pointStatus.setVisible(!val.getPointStatus().equals(ValuationStatus.NINCS));
                //if (belepoStatusz != null && ert != null && ert.getEntrantStatus() != null) { // null check always false
                if (val.getEntrantStatus() != null) {
                    entrantStatus.setVisible(!val.getEntrantStatus().equals(ValuationStatus.NINCS));
                }
                pointStatus.setDefaultModel(new PropertyModel<ConsideredValuation>(cv, "pointStatus"));
                entrantStatus.setDefaultModel(new PropertyModel<ConsideredValuation>(cv, "entrantStatus"));
                item.add(pointStatus);
                item.add(entrantStatus);
            }
            /*
            class ErtekelesStatuszValasztoImpl extends ErtekelesStatuszValaszto {
            
            public ErtekelesStatuszValasztoImpl(String id) {
            super(id);
            }
            
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
            return true;
            }
            
            @Override
            protected void onSelectionChanged(Object newSelection) {
            updateModel();
            Object o = getInnermostModel().getObject();
            ConsideredValuation e = (ConsideredValuation) o;
            
            ConsideredValuation ee = getUnderConsidering().get(e.getValuation().getId());
            getUnderConsidering().put(e.getValuation().getId(), ee);
            }
            }*/
        });

//        form.add(new OrderByBorderImpl("orderByCsoport", "csoportNev", dp));
//        form.add(new OrderByBorderImpl("orderByAtlagPont", "atlagPont", dp));
//        form.add(new OrderByBorderImpl("orderByKiosztottKDO", "kiosztottKDO", dp));
//        form.add(new OrderByBorderImpl("orderByKiosztottKB", "kiosztottKB", dp));
//        form.add(new OrderByBorderImpl("orderByKiosztottAB", "kiosztottAB", dp));
//        form.add(new OrderByBorderImpl("orderByPontStatusz", "pontStatusz", dp));
//        form.add(new OrderByBorderImpl("orderByBelepoStatusz", "belepoStatusz", dp));
    }

    @SuppressWarnings("unused")
    private class OrderByBorderImpl extends OrderByBorder {

        public OrderByBorderImpl(String id, String property, ISortStateLocator stateLocator) {
            super(id, property, stateLocator);
        }

        @Override
        protected void onSortChanged() {
            getUnderConsidering().clear();
        }
    }
}
