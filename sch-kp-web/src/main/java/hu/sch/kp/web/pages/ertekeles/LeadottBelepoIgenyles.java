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
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import hu.sch.kp.web.util.ListDataProviderCompoundPropertyModelImpl;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class LeadottBelepoIgenyles extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    public LeadottBelepoIgenyles(final Ertekeles ert) {

        setHeaderLabelText("Kiosztott belépők");
        final List<BelepoIgeny> igenylista = igenyeketElokeszit(ert);

        setModel(new CompoundPropertyModel(ert));
        add(new Label("csoport.nev"));
        add(new Label("szemeszter"));

        IDataProvider provider = new ListDataProviderCompoundPropertyModelImpl(igenylista);
        DataView dview = new DataView("igenyek", provider) {

            @Override
            protected void populateItem(Item item) {
                final BelepoIgeny b = (BelepoIgeny) item.getModelObject();
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

        add(dview);
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
