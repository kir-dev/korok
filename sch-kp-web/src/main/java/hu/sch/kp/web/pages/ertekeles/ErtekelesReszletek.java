/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.ertekeles;

import hu.sch.domain.Ertekeles;
import hu.sch.domain.ErtekelesStatisztika;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.components.FelhasznaloLink;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Page;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hege
 */
public class ErtekelesReszletek extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    public ErtekelesReszletek(Ertekeles ertekeles) {
        this(ertekeles, null);
    }

    public ErtekelesReszletek(final Ertekeles ertekeles, final Page prevPage) {
        setHeaderLabelText("Leadott értékelés - részletes nézet");
        IModel model = new CompoundPropertyModel(ertekeles);

        setModel(model);
        /*Link backlink = new Link("backlink") {

            @Override
            public void onClick() {
                setResponsePage(prevPage);
            }
        };
        if (prevPage == null) {
            backlink.setVisible(false);
        }
        add(backlink);*/
        add(new Label("csoport.nev", ertekeles.getCsoport().getNev()));
        //TODO fix this with compoundpropertymodel :)
        if (ertekeles.getFelado() != null) {
            add(new FelhasznaloLink("felado", ertekeles.getFelado()));
        } else {
            add(new Label("felado", "Nincs megadva"));
        }
        add(new Label("szemeszter"));
        add(new Label("belepoStatusz"));
        add(new Label("pontStatusz"));

        add(new Link("belepokerelmeklink") {

            @Override
            public void onClick() {
                setResponsePage(new LeadottBelepoIgenyles(ertekeles));
            }
        });
        add(new Link("pontkerelmeklink") {

            @Override
            public void onClick() {
                setResponsePage(new LeadottPontIgenyles(ertekeles));
            }
        });

        List<Long> ids = new ArrayList<Long>();
        ids.add(ertekeles.getId());
        List<ErtekelesStatisztika> statList = ertekelesManager.getStatisztikaForErtekelesek(ids);
        ErtekelesStatisztika stat = statList.iterator().next();
        add(new Label("stat.atlagPont", new Model(stat.getAtlagPont())));
        add(new Label("stat.kiosztottKDO", new Model(stat.getKiosztottKDO())));
        add(new Label("stat.kiosztottKB", new Model(stat.getKiosztottKB())));
        add(new Label("stat.kiosztottAB", new Model(stat.getKiosztottAB())));

        add(new MultiLineLabel("szovegesErtekeles"));
        add(DateLabel.forDatePattern("utolsoModositas", "yyyy.MM.dd. kk:mm"));
        add(DateLabel.forDatePattern("utolsoElbiralas", "yyyy.MM.dd. kk:mm"));
    }
}
