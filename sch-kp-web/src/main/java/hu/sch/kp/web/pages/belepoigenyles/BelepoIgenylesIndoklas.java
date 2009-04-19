/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.belepoigenyles;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.BelepoTipus;
import hu.sch.domain.Ertekeles;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.ertekeles.Ertekelesek;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import hu.sch.kp.web.util.ListDataProviderCompoundPropertyModelImpl;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 *
 * @author hege
 */
public class BelepoIgenylesIndoklas extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public BelepoIgenylesIndoklas(final Ertekeles ert, final List<BelepoIgeny> igenyek) {
        List<BelepoIgeny> indoklando = kellIndoklas(igenyek);
        add(new FeedbackPanel("pagemessages"));
        setHeaderLabelText("Színes belépők indoklása");
        Form indoklasform = new Form("indoklasform") {

            @Override
            protected void onSubmit() {

                if (ertekelesManager.belepoIgenyekLeadasa(ert.getId(), igenyek)) {
                    getSession().info(getLocalizer().getString("info.BelepoIgenylesMentve", this));
                    //getSession().info("Belépőigények elmentve");
                    setResponsePage(Ertekelesek.class);
                    return;
                } else {
                    getSession().info(getLocalizer().getString("info.BelepoIgenylesNincsIndoklas", this));
                    setResponsePage(new BelepoIgenylesIndoklas(ert, igenyek));
                    return;
                }


            }
        };

        DataView dview = new DataView("indoklas", new ListDataProviderCompoundPropertyModelImpl(indoklando)) {

            @Override
            protected void populateItem(Item item) {
                item.add(new Label("felhasznalo.nev"));
                item.add(new Label("felhasznalo.becenev"));
                item.add(new Label("belepotipus"));
                TextArea textArea = new TextArea("szovegesErtekeles");
                
                item.add(textArea);
            }
        };

        indoklasform.add(dview);
        add(indoklasform);
    }

    private List<BelepoIgeny> kellIndoklas(List<BelepoIgeny> igenyek) {
        List<BelepoIgeny> indoklando = new ArrayList<BelepoIgeny>();
        for (BelepoIgeny i : igenyek) {
            if (!i.getBelepotipus().equals(BelepoTipus.KDO)) {
                indoklando.add(i);
            }
        }
        return indoklando;
    }
}
