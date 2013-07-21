package hu.sch.web.error;

import hu.sch.web.kp.KorokPage;
import javax.servlet.http.HttpServletResponse;
import org.apache.wicket.request.http.WebResponse;

/**
 *
 * @author aldaris
 */
public final class NotFound extends KorokPage {

    public NotFound() {
        super();
        setHeaderLabelText("Hiba!");
    }

    @Override
    protected void configureResponse(final WebResponse response) {
        super.configureResponse(response);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }
}
