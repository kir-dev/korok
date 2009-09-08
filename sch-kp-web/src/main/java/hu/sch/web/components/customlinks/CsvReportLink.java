/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components.customlinks;

import hu.sch.domain.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.kp.util.ByteArrayResourceStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;

/**
 *
 * @author aldaris
 */
public final class CsvReportLink extends Panel {

    @EJB(name = "SvieManagerBean")
    SvieManagerLocal svieManager;

    public CsvReportLink(String id) {
        super(id);
        add(new Link<Void>("csvLink") {

            public void onClick() {
                try {
                    IResourceStream resourceStream = new ByteArrayResourceStream(
                            ((ByteArrayOutputStream) generateContent()).toByteArray(),
                            "text/csv");
                    getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {

                        @Override
                        public String getFileName() {
                            return ("export.csv");
                        }
                    });
                } catch (Exception ex) {
                    getSession().error("Hiba történt a CSV export generálása közben!");
                    ex.printStackTrace();
                }
            }
        });
    }

    public OutputStream generateContent() throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder(100);
        List<User> users = svieManager.getDelegatedUsers();

        sb.append("Delegált neve,Képviselt kör\n");
        for (User user : users) {
            sb.append(user.getName()).append(",");
            sb.append(user.getSviePrimaryMembership().getGroup().getName()).append("\n");
        }
        os.write(sb.toString().getBytes("UTF-8"));
        return os;
    }
}
