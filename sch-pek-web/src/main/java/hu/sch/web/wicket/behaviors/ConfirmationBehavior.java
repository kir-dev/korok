package hu.sch.web.wicket.behaviors;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;

/**
 *
 * @author aldaris
 */
public class ConfirmationBehavior extends Behavior {

    private String message;

    /**
     * Constructor.
     *
     * @param message Message to be shown in the confirm box.
     */
    public ConfirmationBehavior(final String message) {
        super();
        this.message = message;
    }

    /**
     * @param component Component to attach.
     * @param tag       Tag to modify.
     * @see
     * org.apache.wicket.behavior.Behavior#onComponentTag(org.apache.wicket.Component,
     * org.apache.wicket.markup.ComponentTag)
     */
    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        if (component instanceof Button || component instanceof Link) {
            tag.getAttributes().remove("onclick");
            tag.getAttributes().put("onclick", "return confirm('" + message + "')");
        }
    }
}
