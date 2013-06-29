package hu.sch.web.wicket.components;

import hu.sch.domain.Group;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 *
 * @author aldaris
 */
public final class SvieDelegateNumberField extends Panel {

    public SvieDelegateNumberField(String id, Group group) {
        super(id);
        TextField<Integer> tf = new TextField<Integer>("delegateNum", new PropertyModel<Integer>(group, "delegateNumber"));
        tf.add(new RangeValidator<Integer>(0, 40));
        add(tf);
    }
}
