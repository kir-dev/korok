/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.ertekeles;

import hu.sch.domain.Ertekeles;
import hu.sch.domain.ErtekelesUzenet;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.components.FelhasznaloLink;
import hu.sch.kp.web.templates.SecuredPageTemplate;

import java.util.List;

import javax.ejb.EJB;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ErtekelesUzenetek extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    public ErtekelesUzenetek(final Long ertekelesId) {
        setHeaderLabelText("Üzenetek");
        Ertekeles ertekeles = ertekelesManager.getErtekelesWithUzenetek(ertekelesId);
        ertekeles.sortUzenetek();
        List<ErtekelesUzenet> uzenetek = ertekeles.getUzenetek();
        if (uzenetek.isEmpty()) {
            info(getLocalizer().getString("info.NincsUzenet", this));
        }

        ListView uzenetekView = new ListView("uzenetek", uzenetek) {

            @Override
            protected void populateItem(ListItem item) {
                ErtekelesUzenet u = (ErtekelesUzenet) item.getModelObject();
                item.setModel(new CompoundPropertyModel(u));
                item.add(new FelhasznaloLink("felado", u.getFelado()));
                item.add(DateLabel.forDatePattern("datum", "yyyy.MM.dd. kk:mm"));
                item.add(new MultiLineLabel("uzenet"));
            }
        };
        add(uzenetekView);

        Link ujuzenet = new Link("ujuzenetlink") {

            @Override
            public void onClick() {
                setResponsePage(new UjUzenet(ertekelesId));
            }
        };
        ujuzenet.setVisible(systemManager.getSzemeszter().equals(ertekeles.getSzemeszter()));
        add(ujuzenet);
    }
}
