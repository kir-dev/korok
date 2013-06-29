package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.Membership;
import hu.sch.web.kp.group.ChangePost;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

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
        Link fl = new BookmarkablePageLink("postLink", ChangePost.class,
                new PageParameters().add("memberid", ms.getId()));
        add(fl);
    }
}
