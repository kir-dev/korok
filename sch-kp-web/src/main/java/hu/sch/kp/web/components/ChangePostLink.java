/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.Csoporttagsag;
import hu.sch.kp.web.pages.group.ChangePost;
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

    public ChangePostLink(String id, Csoporttagsag cs) {
        super(id, new CompoundPropertyModel(cs));
        init();
    }

    private void init() {
        final Csoporttagsag cs = (Csoporttagsag) getModelObject();
        PageParameters params = new PageParameters();
        params.put("userid", cs.getFelhasznalo().getId());
        params.put("groupid", cs.getCsoport().getId());
        Link fl = new BookmarkablePageLink("postLink", ChangePost.class, params);
        add(fl);
    }
}
