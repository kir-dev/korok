package hu.sch.web.wicket.behaviors;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 *
 * @author konvergal
 */
public class ValidationStyleBehavior extends Behavior {

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        final FormComponent comp = (FormComponent) component;
        if (!comp.isValid()) {
            tag.getAttributes().put("class", "inputError");
        }
    }
}
