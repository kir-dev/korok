package hu.sch.web.devel;

import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.protocol.http.WebRequest;

/**
 *
 * @author aldaris
 */
public class ShowUserInfo extends SecuredPageTemplate {

    public ShowUserInfo() {
        final HttpServletRequest request = ((WebRequest) getRequest()).getHttpServletRequest();
        List<String> attrNames = Collections.list(request.getAttributeNames());
        ListView<String> lv = new ListView<String>("attributes", attrNames) {

            @Override
            protected void populateItem(ListItem item) {
                String attr = item.getDefaultModelObject().toString();
                item.add(new Label("name", attr));
                item.add(new Label("value", request.getAttribute(attr).toString()));
            }
        };
        add(lv);
    }
}
