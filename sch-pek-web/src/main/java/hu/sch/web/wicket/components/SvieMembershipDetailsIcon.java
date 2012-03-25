package hu.sch.web.wicket.components;

import hu.sch.domain.Membership;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.User;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.resource.ContextRelativeResource;

/**
 *
 * @author balo
 */
public class SvieMembershipDetailsIcon extends Panel {

    public SvieMembershipDetailsIcon(String id, Membership ms) {
        super(id, new CompoundPropertyModel<Membership>(ms));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final Membership ms = (Membership) getDefaultModelObject();
        User u = ms.getUser();

        String icon;
        switch (u.getSvieStatus()) {
            case ELFOGADVA:
                SvieMembershipType msType = ms.getUser().getSvieMembershipType();
                switch (msType) {
                    case PARTOLOTAG:
                        icon = "heart";
                        break;
                    default:
                        Membership sviePrimaryMembership = u.getSviePrimaryMembership();
                        if (sviePrimaryMembership != null
                                && sviePrimaryMembership.equals(ms)) {

                            icon = "checkmark";
                        } else {
                            icon = "info";
                        }
                }
                break;
            case ELFOGADASALATT:
            case FELDOLGOZASALATT:
                icon = "help";
                break;
            default:
                icon = "error";
        }


        final Image imgIcon = new Image("msAsImg", new ContextRelativeResource(
                new StringBuilder("/images/icons/").append(icon).append("_32.png").toString()));

        String altText = ms.getUser().getSvieMemberText(ms);

        imgIcon.add(new SimpleAttributeModifier("alt", altText));
        imgIcon.add(new SimpleAttributeModifier("title", altText));
        imgIcon.add(new SimpleAttributeModifier("class", "svieStateIcon"));

        add(imgIcon);
    }
}