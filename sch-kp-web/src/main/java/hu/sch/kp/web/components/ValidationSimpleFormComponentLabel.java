/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;

/**
 *
 * @author major
 */
public class ValidationSimpleFormComponentLabel extends SimpleFormComponentLabel {

    public ValidationSimpleFormComponentLabel(String id, LabeledWebMarkupContainer labelProvider) {
        super(id, labelProvider);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        FormComponent fc = (FormComponent) getFormComponent();
        if (!fc.isValid()) {
            tag.getAttributes().put("class", "labelError");
        }
    }
}