package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.services.UserManagerLocal;
import org.apache.wicket.AttributeModifier;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 *
 * @author aldaris
 * @author tomi
 */
public class AttributeAjaxFallbackLink extends AjaxFallbackLink {

    private UserAttributeName attr;
    private boolean isPrivateAttr;
    private Image img;
    private User user;
    @Inject
    private UserManagerLocal userManager;

    public AttributeAjaxFallbackLink(String linkId, String imgId, final UserAttributeName attr, User user) {
        super(linkId);
        this.attr = attr;
        this.user = user;
        isPrivateAttr = !user.isAttributeVisible(attr);

        img = new Image(imgId, getImageResourceReference());
        img.setOutputMarkupId(true);
        img.add(getImageTitleAttribute());

        this.add(img);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        invertAttributeVisibility();
        isPrivateAttr = !isPrivateAttr;

        img.setImageResourceReference(getImageResourceReference());
        img.add(getImageTitleAttributeMod());
        target.add(img);
    }

    private ResourceReference getImageResourceReference() {
        if (isPrivateAttr) {
            return new PackageResourceReference(AttributeAjaxFallbackLink.class, "resources/private.gif");
        } else {
            return new PackageResourceReference(AttributeAjaxFallbackLink.class, "resources/public.gif");
        }
    }

    private AttributeAppender getImageTitleAttribute() {
        if (isPrivateAttr) {
            return new AttributeAppender("title", Model.of("Mutat"));
        } else {
            return new AttributeAppender("title", Model.of("Elrejt"));
        }
    }

    private AttributeModifier getImageTitleAttributeMod() {
        if (isPrivateAttr) {
            return new AttributeModifier("title", Model.of("Mutat"));
        } else {
            return new AttributeModifier("title", Model.of("Elrejt"));
        }

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void invertAttributeVisibility() {
        userManager.invertAttributeVisibility(user, attr);
    }
}
