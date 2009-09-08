/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.Group;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 *
 * @author aldaris
 */
public final class SvieDelegateNumberChooser extends Panel {

    private final Group group;

    public SvieDelegateNumberChooser(String id, Group group2) {
        super(id);
        group = group2;
        TextField<Integer> tf = new TextField<Integer>("delegateNum", new PropertyModel<Integer>(group, "delegateNumber"));
        tf.add(new RangeValidator<Integer>(0, 40));
        add(tf);
    }
}
