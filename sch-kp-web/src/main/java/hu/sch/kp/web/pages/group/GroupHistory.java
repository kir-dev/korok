/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.ertekeles.ErtekelesDetailPanel;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
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
    private Long id;
    private Csoport csoport;
    private Szemeszter szemeszter = null;
    private String selected = "";
    private Ertekeles selectedErtekeles = null;
    private ErtekelesDetailPanel ertekelesPanel;

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
        Form idoszakForm = new Form("idoszakForm") {

            @Override
            public void onSubmit() {
                Iterator iterator = ertekelesList.iterator();
                while (iterator.hasNext()) {
                    selectedErtekeles = (Ertekeles) iterator.next();
                    szemeszter = selectedErtekeles.getSzemeszter();
                    if (szemeszter.toString().equals(selected)) {
                        setHeaderLabelText("A kör részletes pontozásai");
                        //csoport.getNev()
                        ertekelesPanel.updateDatas(selectedErtekeles);
                        ertekelesPanel.setVisible(true);
                        break;
                    }
                }
            }
        };
        add(new Label("nev",csoport.getNev()));
        DropDownChoice ddc = new DropDownChoice("semesters", szemeszterek);
        ddc.setModel(new PropertyModel(this, "selected"));

        idoszakForm.add(ddc);
        add(idoszakForm);
        setModel(new CompoundPropertyModel(selectedErtekeles));

        ertekelesPanel = new ErtekelesDetailPanel("ertekelesinfo");
        ertekelesPanel.updateDatas(selectedErtekeles);
        ertekelesPanel.setVisible(false);
        add(ertekelesPanel);
    }
}
