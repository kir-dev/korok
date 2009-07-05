/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.entrantrequests;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.EntrantType;
import hu.sch.domain.Valuation;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.ListDataProviderCompoundPropertyModelImpl;
import hu.sch.services.ValuationManagerLocal;
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
public class EntrantRequestExplanation extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ValuationManagerLocal ertekelesManager;

    public EntrantRequestExplanation(final Valuation ert, final List<EntrantRequest> igenyek) {
        List<EntrantRequest> indoklando = kellIndoklas(igenyek);
        add(new FeedbackPanel("pagemessages"));
        setHeaderLabelText("Színes belépők indoklása");
        Form indoklasform = new Form("indoklasform") {

            @Override
            protected void onSubmit() {

                if (ertekelesManager.belepoIgenyekLeadasa(ert.getId(), igenyek)) {
                    getSession().info(getLocalizer().getString("info.BelepoIgenylesMentve", this));
                    //getSession().info("Belépőigények elmentve");
                    setResponsePage(Valuations.class);
                    return;
                } else {
                    getSession().error(getLocalizer().getString("info.BelepoIgenylesNincsIndoklas", this));
                    setResponsePage(new EntrantRequestExplanation(ert, igenyek));
                    return;
                }


            }
        };

        DataView<EntrantRequest> dview = new DataView<EntrantRequest>("indoklas", new ListDataProviderCompoundPropertyModelImpl<EntrantRequest>(indoklando)) {

            @Override
            protected void populateItem(Item<EntrantRequest> item) {
                item.add(new Label("felhasznalo.nev"));
                item.add(new Label("felhasznalo.becenev"));
                item.add(new Label("belepotipus"));
                TextArea<String> textArea = new TextArea<String>("szovegesErtekeles");
                
                item.add(textArea);
            }
        };

        indoklasform.add(dview);
        add(indoklasform);
    }

    private List<EntrantRequest> kellIndoklas(List<EntrantRequest> igenyek) {
        List<EntrantRequest> indoklando = new ArrayList<EntrantRequest>();
        for (EntrantRequest i : igenyek) {
            if (!i.getEntrantType().equals(EntrantType.KDO)) {
                indoklando.add(i);
            }
        }
        return indoklando;
    }
}
