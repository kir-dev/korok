package hu.sch.web.error;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.Model;

/**
 *
 * @author balo
 */
public class Forbidden extends WebPage {

    public Forbidden() {
        super();

        add(new WebComponent("css").add(
                new AttributeModifier("href", Model.of("/css/korok-style.css"))));
        add(new WebComponent("favicon").add(
                new AttributeModifier("href", Model.of("/images/favicon-korok.ico"))));
    }
}
