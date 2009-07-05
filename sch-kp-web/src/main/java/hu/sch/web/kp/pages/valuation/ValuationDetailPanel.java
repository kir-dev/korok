/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
