/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.Csoport;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.PontIgeny;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class GroupHistory extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    private List<Ertekeles> ertekelesList = new ArrayList<Ertekeles>();
    private Ertekeles selectedErtekeles = null;
    private Long id;
    private Csoport csoport;
    private Szemeszter szemeszter = null;
    private String selected = "";
    private List<PontIgeny> pontIgenyek = new ArrayList<PontIgeny>();
    private List<BelepoIgeny> belepoIgenyek = new ArrayList<BelepoIgeny>();

    public GroupHistory(PageParameters parameters) {
        Object p = parameters.get("id");
        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            setResponsePage(Index.class);
        }

        setHeaderLabelText("Időszakválasztás");
        //add(new FeedbackPanel("pagemessages"));
        add(new BookmarkablePageLink("simpleView", ShowGroup.class, new PageParameters("id=" + id.toString())));

        csoport = userManager.findGroupById(id);
        ertekelesList.clear();
        ertekelesList.addAll(ertekelesManager.findErtekeles(csoport));
        final List<String> szemeszterek = new ArrayList<String>();
        Iterator iterator = ertekelesList.iterator();
        while (iterator.hasNext()) {
            szemeszterek.add(((Ertekeles) iterator.next()).getSzemeszter().toString());
        }
        Collections.sort(szemeszterek);
        updatePage();
        Form idoszakForm = new Form("idoszakForm") {

            @Override
            public void onSubmit() {
                Iterator iterator = ertekelesList.iterator();
                while (iterator.hasNext()) {
                    selectedErtekeles = (Ertekeles) iterator.next();
                    szemeszter = selectedErtekeles.getSzemeszter();
                    if (szemeszter.toString().equals(selected)) {
                        updatePage();
                        break;
                    }
                }
            }
        };

        DropDownChoice ddc = new DropDownChoice("semesters", szemeszterek);
        ddc.setModel(new PropertyModel(this, "selected"));

        idoszakForm.add(ddc);
        add(idoszakForm);
        generatePointTable();
        generateEntrantTable();
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

    public void updatePage() {
        if (selectedErtekeles != null) {
            pontIgenyek.clear();
            pontIgenyek.addAll(ertekelesManager.findPontIgenyekForErtekeles(selectedErtekeles.getId()));
            belepoIgenyek.clear();
            belepoIgenyek.addAll(ertekelesManager.findBelepoIgenyekForErtekeles(selectedErtekeles.getId()));
            setHeaderLabelText(csoport.getNev() + " részletes pontozásai");
        }
    }
}
