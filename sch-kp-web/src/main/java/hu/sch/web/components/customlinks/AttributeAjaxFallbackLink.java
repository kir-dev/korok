/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components.customlinks;

import hu.sch.domain.profile.Person;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;

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

        img = new Image(imgId);
        img.setOutputMarkupId(true);
        setImgModel();

        this.add(img);
    }

    public void setImgModel() {
        if (isPrivateAttr) {
            img.setDefaultModel(new Model(new ResourceReference(AttributeAjaxFallbackLink.class, "resources/private.gif")));
        } else {
            img.setDefaultModel(new Model(new ResourceReference(AttributeAjaxFallbackLink.class, "resources/public.gif")));
        }
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        person.inversePrivateAttribute(privateAttr);
        isPrivateAttr = !isPrivateAttr;

        setImgModel();
        target.addComponent(img);
    }

    public static void setPerson(Person person2) {
        person = person2;
    }
}
