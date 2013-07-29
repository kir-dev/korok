package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserAttributeName;
import hu.sch.services.UserManagerLocal;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.image.Image;
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

    @EJB(name = "UserManagerBean")
    private UserManagerLocal userManager;

    public AttributeAjaxFallbackLink(String linkId, String imgId, final UserAttributeName attr, User user) {
        super(linkId);
        this.attr = attr;
        this.user = user;
        isPrivateAttr = !user.isAttributeVisible(attr);
        isPrivateAttr = false;

        img = new Image(imgId, getImageResourceReference());
        img.setOutputMarkupId(true);

        this.add(img);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        invertAttributeVisibility();
        isPrivateAttr = !isPrivateAttr;

        img.setImageResourceReference(getImageResourceReference());
        target.add(img);
    }

    private ResourceReference getImageResourceReference() {
        if (isPrivateAttr) {
            return new PackageResourceReference(AttributeAjaxFallbackLink.class, "resources/private.gif");
        } else {
            return new PackageResourceReference(AttributeAjaxFallbackLink.class, "resources/private.gif");
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void invertAttributeVisibility() {
        userManager.invertAttributeVisibility(user, attr);
    }
}
