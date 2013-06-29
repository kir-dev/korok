package hu.sch.web.wicket.behaviors;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.request.Response;

/**
 *
 * @author aldaris
 */
public class FocusOnLoadBehavior extends Behavior {

    @Override
    public void beforeRender(Component component) {
        component.setOutputMarkupId(true);
    }

    @Override
    public void afterRender(Component component) {
        final Response response = component.getResponse();
        response.write(
                "<script type=\"text/javascript\" language=\"javascript\">document.getElementById(\""
                + component.getMarkupId()
                + "\").focus()</script>");
    }

    @Override
    public void bind(Component arg0) {
    }

    @Override
    public void detach(Component arg0) {
    }

    @Override
    public boolean getStatelessHint(Component arg0) {
        return true;
    }

    @Override
    public boolean isEnabled(Component arg0) {
        return true;
    }

    @Override
    public void onComponentTag(Component arg0, ComponentTag arg1) {
    }
};
