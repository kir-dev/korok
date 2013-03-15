package hu.sch.web.wicket.components.choosers;

import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author balo
 */
public class GoogleAnalyticsScript extends Panel {

    public GoogleAnalyticsScript(final String id) {
        super(id);
    }

    @Override
    public boolean isVisible() {
        return getApplication().usesDeploymentConfig();
    }
}
