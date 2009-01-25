/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.Felhasznalo;
import hu.sch.kp.web.pages.user.ShowUser;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class FelhasznaloLink extends Panel {

    public FelhasznaloLink(String id, Felhasznalo felhasznalo) {
        super(id, new CompoundPropertyModel(felhasznalo));
        init();
    }

    private void init() {
        final Felhasznalo felh = (Felhasznalo) getModelObject();
        Link fl = new BookmarkablePageLink("felhLink", ShowUser.class, new PageParameters("id=" + felh.getId()));
        fl.setModel(getModel());
        fl.add(new Label("nev"));
        add(fl);
    }
}
