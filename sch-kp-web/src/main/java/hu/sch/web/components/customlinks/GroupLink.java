/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components.customlinks;

import hu.sch.domain.Group;
import hu.sch.web.kp.pages.group.ShowGroup;
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
public class GroupLink extends Panel {

    public GroupLink(String id, Group group) {
        super(id, new CompoundPropertyModel<Group>(group));
        init();
    }

    private void init() {
        final Group csop = (Group) getDefaultModelObject();
        Link fl = new BookmarkablePageLink("csopLink", ShowGroup.class, new PageParameters("id=" + csop.getId()));
        fl.setModel(getDefaultModel());
        fl.add(new Label("name"));
        add(fl);
    }
}
