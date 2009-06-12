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
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.templates.SecuredPageTemplate;
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
public class LeadottPontIgenyles extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    public LeadottPontIgenyles(Ertekeles ert) {

        setHeaderLabelText("Kiosztott pontok");
        final List<PontIgeny> igenylista = igenyeketElokeszit(ert);

        setModel(new CompoundPropertyModel(ert));
        add(new Label("csoport.nev"));
        add(new Label("szemeszter"));

        IDataProvider provider = new ListDataProviderCompoundPropertyModelImpl(igenylista);
        DataView dview = new DataView("igenyek", provider) {

            @Override
            protected void populateItem(Item item) {
                final PontIgeny p = (PontIgeny) item.getModelObject();
                Link felhasznaloLink = new Link("felhLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(ShowUser.class,
                                new PageParameters("id=" + p.getFelhasznalo().getId().toString()));
                    }
                };
                Label nicknameLabel = new Label("nickname", p.getFelhasznalo().getBecenev());
                item.add(nicknameLabel);
                felhasznaloLink.add(new Label("felhNev", new PropertyModel(p, "felhasznalo.nev")));
                item.add(felhasznaloLink);
                item.add(new Label("pont"));
            }
        };

        add(dview);
    }

    private List<PontIgeny> igenyeketElokeszit(Ertekeles ert) {
        List<Felhasznalo> csoporttagok = userManager.getCsoporttagokWithoutOregtagok(ert.getCsoport().getId());
        List<PontIgeny> igenyek = ertekelesManager.findPontIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.size() == 0) {
            for (Felhasznalo f : csoporttagok) {
                igenyek.add(new PontIgeny(f, 0));
            }
        }
        return igenyek;
    }
}
