/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.ertekeles;

import hu.sch.domain.Csoport;
import hu.sch.domain.Ertekeles;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.pages.belepoigenyles.BelepoIgenylesLeadas;
import hu.sch.kp.web.pages.pontigenyles.PontIgenylesLeadas;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.TimeLimitExceededException;
import org.apache.wicket.datetime.DateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class Ertekelesek extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    public Ertekelesek() {
        super();
        
        WebMarkupContainer table = new WebMarkupContainer("ertekelesektabla");
        
        Csoport csoport = getCsoport();
        List<Ertekeles> list = ertekelesManager.findErtekeles(csoport);
        ListView ertekelesek = new ListView("ertekeles", list) {

            @Override
            protected void populateItem(ListItem item) {
                final Ertekeles e = (Ertekeles) item.getModelObject();
                Link ert = new Link("ertekeleslink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ErtekelesReszletek(e));
                    }
                };
                ert.add(new Label("ertekelesszemeszter", new PropertyModel(e, "szemeszter")));
                item.add(ert);
                IModel model = new CompoundPropertyModel(e);
                item.setModel(model);
                
                Link uzenetekLink = new Link("uzeneteklink") {
                    @Override
                    public void onClick() {
                        setResponsePage(new ErtekelesUzenetek(e.getId()));
                    }
                };
                item.add(uzenetekLink);
                
                Link pontkerelemLink = new Link("pontkerelemlink", model) {

                    @Override
                    public void onClick() {
                        setResponsePage(new PontIgenylesLeadas(e));
                    }  
                };
                pontkerelemLink.add(new Label("pontStatusz"));
                item.add(pontkerelemLink);
                
                Link belepokerelemLink = new Link("belepokerelemlink", model) {

                    @Override
                    public void onClick() {
                        setResponsePage(new BelepoIgenylesLeadas((e)));
                    }
                    
                };
                item.add(belepokerelemLink);
                belepokerelemLink.add(new Label("belepoStatusz"));
                
                item.add(DateLabel.forDatePattern("utolsoModositas", "yyyy.MM.dd. kk:mm"));
                item.add(DateLabel.forDatePattern("utolsoElbiralas", "yyyy.MM.dd. kk:mm"));
            }
        };
        table.add(ertekelesek);
        add(table);

        if (list.size() == 0) {
            info(getLocalizer().getString("info.NincsErtekeles", this));
            table.setVisible(false);
        }

        Link ujertekeles = new Link("ujertekeles") {

            @Override
            public void onClick() {
                setResponsePage(UjErtekeles.class);
            }
        };
        if (!ertekelesManager.isErtekelesLeadhato(csoport)) {
            ujertekeles.setVisible(false);
        }
        add(ujertekeles);
    }
}
