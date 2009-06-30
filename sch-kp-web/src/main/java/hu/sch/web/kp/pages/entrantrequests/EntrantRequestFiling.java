 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.entrantrequests;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.EntrantType;
import hu.sch.domain.Valuation;
import hu.sch.domain.User;
import hu.sch.web.components.EntrantTypeChooser;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.ListDataProviderCompoundPropertyModelImpl;
import hu.sch.services.ValuationManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class EntrantRequestFiling extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ValuationManagerLocal ertekelesManager;

    public EntrantRequestFiling(final Valuation ert) {
        setHeaderLabelText("Belépőigénylések leadása");
        //TODO jogosultság?!
        final List<EntrantRequest> igenylista = igenyeketElokeszit(ert);

        setDefaultModel(new CompoundPropertyModel(ert));
        add(new Label("csoport.nev"));
        add(new Label("szemeszter"));

        Form igform = new Form("igenyekform") {

            @Override
            protected void onSubmit() {
                // Van-e olyan, amit indokolni kell
                for (EntrantRequest belepoIgeny : igenylista) {
                    if (belepoIgeny.getEntrantType() == EntrantType.AB || belepoIgeny.getEntrantType() == EntrantType.KB) {
                        setResponsePage(new EntrantRequestExplanation(ert, igenylista));
                        return;
                    }
                }
                ertekelesManager.belepoIgenyekLeadasa(ert.getId(), igenylista);
                getSession().info(getLocalizer().getString("info.BelepoIgenylesMentve", getParent()));
                setResponsePage(Valuations.class);
            }
        };
        IDataProvider provider = new ListDataProviderCompoundPropertyModelImpl(igenylista);
        DataView dview = new DataView("igenyek", provider) {

            @Override
            protected void populateItem(Item item) {
                item.add(new Label("felhasznalo.nev"));
                item.add(new Label("felhasznalo.becenev"));
                DropDownChoice bt = new EntrantTypeChooser("belepotipus");
                bt.setRequired(true);
                item.add(bt);
            }
        };

        igform.add(dview);
        add(igform);
    }

    private List<EntrantRequest> igenyeketElokeszit(Valuation ert) {
        List<User> csoporttagok = userManager.getCsoporttagokWithoutOregtagok(ert.getGroup().getId());
        List<EntrantRequest> igenyek = ertekelesManager.findBelepoIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.isEmpty()) {
            for (User f : csoporttagok) {
                igenyek.add(new EntrantRequest(f, EntrantType.KDO));
            }
        } else {

            //tényleges összefésülés
            boolean szerepel = false;
            if (igenyek.size() != csoporttagok.size()) {
                for (User csoporttag : csoporttagok) {
                    szerepel = false;
                    for (EntrantRequest igeny : igenyek) {
                        if (igeny.getUser().getId().equals(csoporttag.getId())) {
                            szerepel = true;
                            break;
                        }
                    }
                    if (!szerepel) {
                        igenyek.add(new EntrantRequest(csoporttag, EntrantType.KDO));
                    }
                }
            }
        }

        return igenyek;
    }
}
