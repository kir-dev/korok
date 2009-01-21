/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.components.FelhasznaloLink;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;

import org.apache.wicket.PageParameters;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ShowGroup extends SecuredPageTemplate {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public ShowGroup(PageParameters parameters) {
        Object p = parameters.get("id");
        Long id = null;

        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            setResponsePage(Index.class);
        }

        Csoport cs = userManager.findGroupWithCsoporttagsagokById(id);
        if (cs == null) {
            info("Nem vagy körtag");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        if (cs.getNev().contains("Informatikus-hallgatók")) {
            setHeaderLabelText("MAVE adatlapja");
        } else {
            setHeaderLabelText(cs.getNev() + " adatlapja");
        }
        setModel(new CompoundPropertyModel(cs));
        add(new Label("nev"));
        add(new Label("alapitasEve"));
        add(new Label("webpage"));
        add(new Label("levelezoLista"));
        add(new MultiLineLabel("leiras"));
        cs.sortCsoporttagsagok();
        ListView csoptagsagok = new ListView("csoptagsag",
                cs.getCsoporttagsagok()) {

            @Override
            protected void populateItem(ListItem item) {
                Csoporttagsag cs = (Csoporttagsag) item.getModelObject();
                item.setModel(new CompoundPropertyModel(cs));
                item.add(new FelhasznaloLink("felhlink", cs.getFelhasznalo()));
                item.add(new Label("becenev", cs.getFelhasznalo().getBecenev()));
                item.add(new Label("jogok",
                                   getConverter(TagsagTipus.class).convertToString(cs.getJogokString(), getLocale())));
                item.add(DateLabel.forDatePattern("kezdet", "yyyy.MM.dd."));
                item.add(DateLabel.forDatePattern("veg", "yyyy.MM.dd."));
            }
        };
        add(csoptagsagok);
    }
}
