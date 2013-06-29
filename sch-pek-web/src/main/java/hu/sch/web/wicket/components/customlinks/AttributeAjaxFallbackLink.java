package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.profile.Person;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 *
 * @author aldaris
 */
public class AttributeAjaxFallbackLink extends AjaxFallbackLink {

    private String privateAttr;
    private boolean isPrivateAttr;
    private Image img;
    private static Person person;

    public AttributeAjaxFallbackLink(String id) {
        super(id);
    }

    public AttributeAjaxFallbackLink(String linkId, String imgId, final String privateAttr) {
        super(linkId);
        this.privateAttr = privateAttr;
        isPrivateAttr = person.isPrivateAttribute(privateAttr);

        img = new Image(imgId, getImageResourceReference());
        img.setOutputMarkupId(true);

        this.add(img);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        person.inversePrivateAttribute(privateAttr);
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

    public static void setPerson(Person person2) {
        person = person2;
    }
}
