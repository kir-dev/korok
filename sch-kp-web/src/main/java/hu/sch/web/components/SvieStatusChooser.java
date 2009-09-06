/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import java.util.Arrays;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public final class SvieStatusChooser extends Panel {

    private static final SvieStatus[] choices = {SvieStatus.FELDOLGOZASALATT, SvieStatus.ELFOGADASALATT, SvieStatus.ELFOGADVA};
    private final User user;

    public SvieStatusChooser(String id, User user2) {
        super(id);
        user = user2;
        DropDownChoice<SvieStatus> ddc =
                new DropDownChoice<SvieStatus>("svieStatus",
                new PropertyModel<SvieStatus>(this, "user.svieStatus"),
                Arrays.asList(choices));
        ddc.setChoiceRenderer(new IChoiceRenderer<SvieStatus>() {

            @Override
            public Object getDisplayValue(SvieStatus object) {
                return object.toString();
            }

            @Override
            public String getIdValue(SvieStatus object, int index) {
                return object.name();
            }
        });
        add(ddc);
    }
}
