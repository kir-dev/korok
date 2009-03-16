/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.ertekeles;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.PontIgeny;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.pages.user.ShowUser;
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
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class ErtekelesDetailPanel extends Panel {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    private Ertekeles selectedErtekeles = null;
    private List<PontIgeny> pontIgenyek = new ArrayList<PontIgeny>();
    private List<BelepoIgeny> belepoIgenyek = new ArrayList<BelepoIgeny>();
    private MultiLineLabel ertekelesSzoveg;

    public ErtekelesDetailPanel(String id) {
        super(id);
        generateValuationText();
        generatePointTable();
        generateEntrantTable();
    }

    public void generateValuationText() {
        ertekelesSzoveg = new MultiLineLabel("felevesErtekeles");
        add(ertekelesSzoveg);
    }

    public void generatePointTable() {
        WebMarkupContainer pontTable = new WebMarkupContainer("pontTabla");
        ListView pontListView = new ListView("pontok", pontIgenyek) {

            @Override
            protected void populateItem(ListItem item) {
                final PontIgeny p = (PontIgeny) item.getModelObject();
                IModel model = new CompoundPropertyModel(p);
                item.setModel(model);
                Link felhasznaloLink = new Link("felhLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(ShowUser.class,
                                new PageParameters("id=" + p.getFelhasznalo().getId().toString()));
                    }
                };
                felhasznaloLink.add(new Label("felhNev", new PropertyModel(p, "felhasznalo.nev")));
                item.add(felhasznaloLink);
                item.add(new Label("pontszam", new PropertyModel(p, "pont")));
            }
        };
        pontTable.add(pontListView);
        add(pontTable);
    }

    public void generateEntrantTable() {
        WebMarkupContainer belepoTable = new WebMarkupContainer("belepoTabla");
        ListView belepoListView = new ListView("belepok", belepoIgenyek) {

            @Override
            protected void populateItem(ListItem item) {
                final BelepoIgeny b = (BelepoIgeny) item.getModelObject();
                IModel model = new CompoundPropertyModel(b);
                item.setModel(model);
                Link felhasznaloLink = new Link("felhLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(ShowUser.class,
                                new PageParameters("id=" + b.getFelhasznalo().getId().toString()));
                    }
                };
                felhasznaloLink.add(new Label("felhNev", new PropertyModel(b, "felhasznalo.nev")));
                item.add(felhasznaloLink);
                item.add(new Label("belepotipus"));
                item.add(new Label("szovegesErtekeles"));
            }
        };
        belepoTable.add(belepoListView);
        add(belepoTable);
    }

    public void updateDatas(Ertekeles ertekeles) {
        if (ertekeles != null) {
            pontIgenyek.clear();
            pontIgenyek.addAll(ertekelesManager.findPontIgenyekForErtekeles(ertekeles.getId()));
            belepoIgenyek.clear();
            belepoIgenyek.addAll(ertekelesManager.findBelepoIgenyekForErtekeles(ertekeles.getId()));
            ertekelesSzoveg.setModel(new Model(ertekeles.getSzovegesErtekeles()));
        }
    }
}
