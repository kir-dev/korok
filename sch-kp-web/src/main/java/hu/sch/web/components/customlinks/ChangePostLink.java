/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components.customlinks;

import hu.sch.domain.Membership;
import hu.sch.web.kp.pages.group.ChangePost;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author aldaris
 */
public class ChangePostLink extends Panel {

    public ChangePostLink(String id, Membership ms) {
        super(id, new CompoundPropertyModel<Membership>(ms));
        init();
    }

    private void init() {
        final Membership ms = (Membership) getDefaultModelObject();
        PageParameters params = new PageParameters();
        params.put("memberid", ms.getId());
        Link fl = new BookmarkablePageLink("postLink", ChangePost.class, params);
        add(fl);
    }
}
