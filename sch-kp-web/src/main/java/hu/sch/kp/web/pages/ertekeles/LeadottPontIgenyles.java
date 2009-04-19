/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.ertekeles;

import hu.sch.kp.web.util.ListDataProviderCompoundPropertyModelImpl;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.PontIgeny;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.pages.ertekeles.Ertekelesek;
import hu.sch.kp.web.session.VirSession;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class LeadottPontIgenyles extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public LeadottPontIgenyles(Ertekeles ert) {
        setHeaderLabelText("Leadott pontigénylések megtekintése");
        //TODO jogosultság?!
        final Long ertekelesId = ert.getId();
        final List<PontIgeny> igenylista = igenyeketElokeszit(ert);

        setModel(new CompoundPropertyModel(ert));
        add(new Label("csoport.nev"));
        add(new Label("szemeszter"));

        Form igform = new Form("igenyekform") {

            @Override
            protected void onSubmit() {
                return;
            }
        };

        IDataProvider provider = new ListDataProviderCompoundPropertyModelImpl(igenylista);
        DataView dview = new DataView("igenyek", provider) {

            @Override
            protected void populateItem(Item item) {
                item.add(new Label("felhasznalo.nev"));
                item.add(new Label("felhasznalo.becenev"));
                item.add(new Label("pont"));
            }
        };

        igform.add(dview);
        add(igform);
    }

    private List<PontIgeny> igenyeketElokeszit(Ertekeles ert) {
        List<Felhasznalo> csoporttagok = userManager.getCsoporttagokWithoutOregtagok(ert.getCsoport().getId());
        List<PontIgeny> igenyek = ertekelesManager.findPontIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.size() == 0) {
            for (Felhasznalo f : csoporttagok) {
                igenyek.add(new PontIgeny(f, 0));
            }
        } else {
            // TODO tényleges összefésülés
            if (igenyek.size() != csoporttagok.size()) {
                boolean bentvan;
                for (Felhasznalo felh : csoporttagok) {
                    bentvan = false;
                    for (PontIgeny igeny : igenyek) {
                        if (felh.getId() == igeny.getFelhasznalo().getId()) {
                            bentvan = true;
                            break;
                        }
                    }
                    if (!bentvan) {
                        igenyek.add(new PontIgeny(felh, 0));
                    }
                }
            }
        }

        return igenyek;
    }
}
