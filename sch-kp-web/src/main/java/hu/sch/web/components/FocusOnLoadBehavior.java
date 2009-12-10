/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.Response;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;

/**
 *
 * @author aldaris
 */
public class FocusOnLoadBehavior implements IBehavior {

    @Override
    public void beforeRender(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void afterRender(Component component) {
        final Response response = component.getResponse();
        response.write(
                "<script type=\"text/javascript\" language=\"javascript\">document.getElementById(\"" +
                component.getMarkupId() +
                "\").focus()</script>");

    }

    public void bind(Component arg0) {
    }

    public void detach(Component arg0) {
    }

    public void exception(Component arg0, RuntimeException arg1) {
    }

    public boolean getStatelessHint(Component arg0) {
        return true;
    }

    public boolean isEnabled(Component arg0) {
        return true;
    }

    public boolean isTemporary() {
        return false;
    }

    public void onComponentTag(Component arg0, ComponentTag arg1) {
    }
};
