/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 *
 * @author aldaris
 */
public class ValidationStyleBehavior extends AbstractBehavior {

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        FormComponent comp = (FormComponent) component;
        if (comp.isValid() && comp.getConvertedInput() != null) {
            //tag.getAttributes().put("class", "valid");
        } else if (!comp.isValid()) {
            tag.getAttributes().put("class", "inputError");
        }
    }
}
