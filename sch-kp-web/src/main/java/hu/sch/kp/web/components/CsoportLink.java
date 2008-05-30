/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.web.components;

import hu.sch.domain.Csoport;
import hu.sch.kp.web.pages.group.ShowGroup;
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
public class CsoportLink extends Panel {
    
    public CsoportLink(String id, Csoport csoport) {
        super(id, new CompoundPropertyModel(csoport));
        init();
    }

    private void init() {
        final Csoport csop = (Csoport)getModelObject();
        Link fl = new BookmarkablePageLink("csopLink",ShowGroup.class,new PageParameters("id="+csop.getId()));
        fl.setModel(getModel());
        fl.add(new Label("nev"));
        add(fl);
    }

}
