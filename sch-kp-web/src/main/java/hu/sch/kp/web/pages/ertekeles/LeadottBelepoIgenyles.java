 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.ertekeles;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.BelepoTipus;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.components.BelepoTipusValaszto;
import hu.sch.kp.web.pages.ertekeles.Ertekelesek;
import hu.sch.kp.web.session.VirSession;
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
public class LeadottBelepoIgenyles extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public LeadottBelepoIgenyles(final Ertekeles ert) {
        //TODO jogosultság?!
        final List<BelepoIgeny> igenylista = igenyeketElokeszit(ert);

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
                item.add(new Label("belepotipus"));
            }
        };

        igform.add(dview);
        add(igform);
    }

    private List<BelepoIgeny> igenyeketElokeszit(Ertekeles ert) {
        List<Felhasznalo> csoporttagok = userManager.getCsoporttagokWithoutOregtagok(ert.getCsoport().getId());
        List<BelepoIgeny> igenyek = ertekelesManager.findBelepoIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.size() == 0) {
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
