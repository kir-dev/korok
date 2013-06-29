package hu.sch.web.error;

import hu.sch.web.kp.KorokPage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 *
 * @author aldaris
 */
public final class PageExpiredError extends KorokPage {

    public PageExpiredError() {
        super();
        setHeaderLabelText("Hiba!");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        final StringBuilder refreshMeta = new StringBuilder("<meta http-equiv=\"refresh\" content=\"5;URL=");
        refreshMeta.append(RequestCycle.get().urlFor(getApplication().getHomePage(), null));
        refreshMeta.append("\">");
        response.render(StringHeaderItem.forString(refreshMeta.toString()));
    }
}
