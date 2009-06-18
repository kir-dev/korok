 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.belepoigenyles;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.BelepoTipus;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.components.BelepoTipusValaszto;
import hu.sch.kp.web.pages.ertekeles.Ertekelesek;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import hu.sch.kp.web.util.ListDataProviderCompoundPropertyModelImpl;

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
public class BelepoIgenylesLeadas extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    public BelepoIgenylesLeadas(final Ertekeles ert) {
        setHeaderLabelText("Belépőigénylések leadása");
        //TODO jogosultság?!
        final List<BelepoIgeny> igenylista = igenyeketElokeszit(ert);

        setModel(new CompoundPropertyModel(ert));
        add(new Label("csoport.nev"));
        add(new Label("szemeszter"));

        Form igform = new Form("igenyekform") {

            @Override
            protected void onSubmit() {
                // Van-e olyan, amit indokolni kell
                for (BelepoIgeny belepoIgeny : igenylista) {
                    if (belepoIgeny.getBelepotipus() == BelepoTipus.AB || belepoIgeny.getBelepotipus() == BelepoTipus.KB) {
                        setResponsePage(new BelepoIgenylesIndoklas(ert, igenylista));
                        return;
                    }
                }
                ertekelesManager.belepoIgenyekLeadasa(ert.getId(), igenylista);
                getSession().info(getLocalizer().getString("info.BelepoIgenylesMentve", getParent()));
                setResponsePage(Ertekelesek.class);
            }
        };
        IDataProvider provider = new ListDataProviderCompoundPropertyModelImpl(igenylista);
        DataView dview = new DataView("igenyek", provider) {

            @Override
            protected void populateItem(Item item) {
                item.add(new Label("felhasznalo.nev"));
                item.add(new Label("felhasznalo.becenev"));
                DropDownChoice bt = new BelepoTipusValaszto("belepotipus");
                bt.setRequired(true);
                item.add(bt);
            }
        };

        igform.add(dview);
        add(igform);
    }

    private List<BelepoIgeny> igenyeketElokeszit(Ertekeles ert) {
        List<Felhasznalo> csoporttagok = userManager.getCsoporttagokWithoutOregtagok(ert.getCsoport().getId());
        List<BelepoIgeny> igenyek = ertekelesManager.findBelepoIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.isEmpty()) {
            for (Felhasznalo f : csoporttagok) {
                igenyek.add(new BelepoIgeny(f, BelepoTipus.KDO));
            }
        } else {

            //tényleges összefésülés
            boolean szerepel = false;
            if (igenyek.size() != csoporttagok.size()) {
                for (Felhasznalo csoporttag : csoporttagok) {
                    szerepel = false;
                    for (BelepoIgeny igeny : igenyek) {
                        if (igeny.getFelhasznalo().getId().equals(csoporttag.getId())) {
                            szerepel = true;
                            break;
                        }
                    }
                    if (!szerepel) {
                        igenyek.add(new BelepoIgeny(csoporttag, BelepoTipus.KDO));
                    }
                }
            }
        }

        return igenyek;
    }
}
