/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.web.pages.pontigenyles;

import hu.sch.kp.web.util.ListDataProviderCompoundPropertyModelImpl;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.PontIgeny;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
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
public class PontIgenylesLeadas extends SecuredPageTemplate {
    @EJB(name="ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    @EJB(name="UserManagerBean")
    UserManagerLocal userManager;
    
    public PontIgenylesLeadas(Ertekeles ert) {
        //TODO jogosultság?!
        final Long ertekelesId = ert.getId();
        final List<PontIgeny> igenylista = igenyeketElokeszit(ert);
        
        setModel(new CompoundPropertyModel(ert));
        add(new Label("csoport.nev"));
        add(new Label("szemeszter"));
        
        Form igform = new Form("igenyekform"){
            @Override
            protected void onSubmit() {
                ertekelesManager.pontIgenyekLeadasa(ertekelesId, igenylista);
            }
        };
        
        IDataProvider provider = new ListDataProviderCompoundPropertyModelImpl(igenylista);
        DataView dview = new DataView("igenyek", provider) {
            @Override
            protected void populateItem(Item item) {
                item.add(new Label("felhasznalo.nev"));
                item.add(new Label("felhasznalo.becenev"));
                item.add(new TextField("pont"));
            }
        };
        
        igform.add(dview);
        add(igform);
    }
    
    private List<PontIgeny> igenyeketElokeszit(Ertekeles ert) {
        List<Felhasznalo> csoporttagok = userManager.getCsoporttagok(ert.getCsoport().getId());
        List<PontIgeny> igenyek = ertekelesManager.findPontIgenyekForErtekeles(ert.getId());
        
        //tagok és igények összefésülése
        if (igenyek.size() == 0) {
            for (Felhasznalo f : csoporttagok) {
                igenyek.add(new PontIgeny(f, 0));
            }
        } else {
            // TODO tényleges összefésülés
            if (igenyek.size() != csoporttagok.size()) {
                // TODO összefésülés
                throw new UnsupportedOperationException("PontIgény - Csoporttag összefésülés még nincs implementálva");
            }
        }
        
        return igenyek;
    }
}
