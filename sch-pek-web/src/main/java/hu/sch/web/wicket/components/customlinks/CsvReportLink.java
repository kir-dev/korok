package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.user.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.wicket.util.ByteArrayResourceStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public final class CsvReportLink extends Link<Void> {

    private static Logger logger = LoggerFactory.getLogger(CsvReportLink.class);
    @Inject
    SvieManagerLocal svieManager;

    public CsvReportLink(String id) {
        super(id);
    }

    @Override
    public void onClick() {
        try {
            IResourceStream resourceStream = new ByteArrayResourceStream(
                    ((ByteArrayOutputStream) generateContent()).toByteArray(),
                    "text/csv");
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "export.csv"));
        } catch (Exception ex) {
            getSession().error("Hiba történt a CSV export generálása közben!");
            logger.error("Error while generating CSV export about delegates", ex);
        }
    }

    public OutputStream generateContent() throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder(100);
        List<User> users = svieManager.getDelegatedUsers();

        sb.append("Delegált neve,Delegált e-mail címe,Képviselt kör\n");
        for (User user : users) {
            sb.append(user.getFullName()).append(",");
            sb.append(user.getEmailAddress()).append(",");
            sb.append(user.getSviePrimaryMembership().getGroup().getName()).append("\n");
        }
        os.write(sb.toString().getBytes("UTF-8"));
        return os;
    }
}
