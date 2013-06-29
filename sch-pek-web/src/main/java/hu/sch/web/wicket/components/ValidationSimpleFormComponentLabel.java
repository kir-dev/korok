package hu.sch.web.wicket.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;

/**
 *
 * @author konvergal
 */
public class ValidationSimpleFormComponentLabel extends SimpleFormComponentLabel {

    public ValidationSimpleFormComponentLabel(final String id,
            final LabeledWebMarkupContainer labelProvider) {

        super(id, labelProvider);
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);
        final FormComponent fc = (FormComponent) getFormComponent();
        if (!fc.isValid()) {
            tag.getAttributes().put("class", "labelError");
        }
    }
}
