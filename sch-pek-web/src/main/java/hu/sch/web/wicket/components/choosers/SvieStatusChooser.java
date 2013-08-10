package hu.sch.web.wicket.components.choosers;

import hu.sch.domain.enums.SvieStatus;
import hu.sch.domain.user.User;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public class SvieStatusChooser extends Panel {

    private static final SvieStatus[] choices = {SvieStatus.FELDOLGOZASALATT, SvieStatus.ELFOGADASALATT, SvieStatus.ELFOGADVA};

    public SvieStatusChooser(String id, User user2) {
        super(id);
        DropDownChoice<SvieStatus> ddc =
                new DropDownChoice<SvieStatus>("svieStatus",
                new PropertyModel<SvieStatus>(user2, "svieStatus"),
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
