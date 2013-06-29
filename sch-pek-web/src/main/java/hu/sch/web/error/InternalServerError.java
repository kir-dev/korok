package hu.sch.web.error;

import hu.sch.services.MailManagerLocal;
import hu.sch.web.kp.KorokPage;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public final class InternalServerError extends KorokPage {

    private static Logger logger = LoggerFactory.getLogger(InternalServerError.class);
    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;

    public InternalServerError(Page page, Exception ex) {
        setHeaderLabelText("Hiba!");
        SmartLinkLabel mailtoLink = new SmartLinkLabel("support", "https://support.sch.bme.hu");
        add(mailtoLink);

        StringBuilder sb = new StringBuilder(500);
        sb.append("Helló!\n\nA Körök alkalmazás futása közben exception keletkezett, pedig nem kellett volna.");
        if (page != null) {
            sb.append("\nA hibáért a következő osztály volt a felelős: ").append(page.getClass().getName());
            sb.append(", ami a következő URL-en volt elérhető: ").append(page.getPageRelativePath());
        }
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();

        sb.append("\nA hibát előidézte: ").append(request.getRemoteUser());
        sb.append("\n\t").append(request.getRemoteAddr());
        sb.append("\n\t").append(request.getAttribute("mail"));
        sb.append("\n\t").append(request.getAttribute("virid"));

        sb.append("\nA hibához tartozó stacktrace:\n\n");
        sb.append(Strings.toString(ex));

        sb.append("\n\nJó debugolást! :)\nKörök");
        try {
            mailManager.sendEmail("jee-dev@sch.bme.hu", "Programhiba", sb.toString());
        } catch (Exception e) {
            e.initCause(ex);
            logger.error("Error while sending the error e-mail!", e);
        }
    }
}
