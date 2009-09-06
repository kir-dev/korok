/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.User;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class MembershipTypeChooser extends Panel {

    private static final SvieMembershipType[] choices = {SvieMembershipType.PARTOLOTAG, SvieMembershipType.RENDESTAG};
    private final User user;

    public MembershipTypeChooser(String id, User user2) {
        super(id);
        user = user2;
        DropDownChoice<SvieMembershipType> ddc =
                new DropDownChoice<SvieMembershipType>("svieMembershipType",
                new PropertyModel<SvieMembershipType>(this, "user.svieMembershipType"),
                Arrays.asList(choices));
        ddc.setChoiceRenderer(new IChoiceRenderer<SvieMembershipType>() {

            @Override
            public Object getDisplayValue(SvieMembershipType object) {
                return object.toString();
            }

            @Override
            public String getIdValue(SvieMembershipType object, int index) {
                return object.name();
            }
        });
        add(ddc);
    }
}

