package hu.sch.web.wicket.components.customlinks;

import hu.sch.domain.GivenPoint;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.util.ByteArrayResourceStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * Egy olyan link, amire ha a user rákapcsol, akkor letöltődik CSV-ben az adott
 * félévhez tartozó pontozás.
 *
 * @author messo
 * @since 2.3.1
 */
public class CsvExportForKfbLink extends Link<Void> {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    @EJB(name = "SystemManagerBean")
    SystemManagerLocal systemManager;

    public CsvExportForKfbLink(String id) {
        super(id);
    }

    @Override
    public void onClick() {
        try {
            IResourceStream resourceStream = new ByteArrayResourceStream(
                    ((ByteArrayOutputStream) generateContent()).toByteArray(),
                    "text/csv");
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(resourceStream, "kfbexport.csv"));
        } catch (Exception ex) {
            getSession().error("Az export során hiba történt!");
            setResponsePage(getApplication().getHomePage());
        }
    }

    public OutputStream generateContent() throws IOException {
        OutputStream os = new ByteArrayOutputStream();

        List<GivenPoint> points = valuationManager.getPointsForKfbExport(systemManager.getSzemeszter());

        os.write("Neptun kód,Számított pont\n".getBytes("UTF-8"));
        for (GivenPoint p : points) {
            os.write(p.getNeptun().getBytes());
            os.write(',');
            os.write(p.getPoints().toString().getBytes());
            os.write("\n".getBytes());
        }

        return os;
    }
}
