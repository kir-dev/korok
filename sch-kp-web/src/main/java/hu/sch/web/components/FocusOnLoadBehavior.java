/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.IComponentBorder;
import org.apache.wicket.Response;
import org.apache.wicket.behavior.AbstractBehavior;

/**
 *
 * @author aldaris
 */
public class FocusOnLoadBehavior extends AbstractBehavior {

    @Override
    public void bind(Component component) {
        super.bind(component);
        component.setOutputMarkupId(true);
        component.setComponentBorder(new IComponentBorder() {

            @Override
            public void renderBefore(Component component) {
            }

            @Override
            public void renderAfter(Component component) {
                final Response response = component.getResponse();
                response.write(
                        "<script type=\"text/javascript\" language=\"javascript\">document.getElementById(\"" +
                        component.getMarkupId() +
                        "\").focus()</script>");
            }
        });
    }

    @Override
    public boolean isTemporary() {
        return true;
    }
}
