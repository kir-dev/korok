package hu.sch.web.wicket.components;

import hu.sch.domain.Membership;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.user.User;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.ContextRelativeResource;

/**
 *
 * @author balo
 */
public class SvieMembershipDetailsIcon extends Panel {

    private User innerUser;

    public SvieMembershipDetailsIcon(final String id, final Membership ms) {
        super(id, new CompoundPropertyModel<Membership>(ms));
    }

    public SvieMembershipDetailsIcon(final String id, final User u) {
        super(id);

        innerUser = u;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Membership ms = null;
        User u = innerUser;

        if (u == null) {
            ms = (Membership) getDefaultModelObject();
            u = ms.getUser();
        }

        String icon;
        switch (u.getSvieStatus()) {
            case ELFOGADVA:
                SvieMembershipType msType = u.getSvieMembershipType();
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

        final String altText = u.getSvieMemberText(ms);

        imgIcon.add(AttributeModifier.replace("alt", altText));
        imgIcon.add(AttributeModifier.replace("title", altText));
        imgIcon.add(AttributeModifier.replace("class", "svieStateIcon"));

        add(imgIcon);
    }
}
