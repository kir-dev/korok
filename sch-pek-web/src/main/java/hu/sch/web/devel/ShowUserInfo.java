package hu.sch.web.devel;

import hu.sch.web.kp.KorokPage;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 *
 * @author aldaris
 */
public class ShowUserInfo extends KorokPage {

    public ShowUserInfo() {
        final HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
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
