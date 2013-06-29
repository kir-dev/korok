package hu.sch.web.wicket.components;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 *
 * @author aldaris
 */
public class CheckBoxHelper extends Panel implements IHeaderContributor {

    public CheckBoxHelper(String componentId) {
        super(componentId);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(CheckBoxHelper.class,
                "CheckBoxHelper.js")));
    }
}
