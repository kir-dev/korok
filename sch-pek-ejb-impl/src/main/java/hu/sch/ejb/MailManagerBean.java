package hu.sch.ejb;

import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.Configuration.Environment;
import hu.sch.services.MailManagerLocal;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
@Stateless
public class MailManagerBean implements MailManagerLocal {

    private static final Logger log = LoggerFactory.getLogger(MailManagerBean.class);
    @Resource(name = "mail/korokMail")
    private Session mailSession;

    @Override
    public boolean sendEmail(String to, String subject, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("E-mail küldése ");
        sb.append(to);
        sb.append("-nak.");
        log.info(sb.toString());

        if (log.isDebugEnabled() || Configuration.getEnvironment() == Environment.TESTING) {
            StringBuilder sbd = new StringBuilder();

            sbd.append("E-mail küldése\n");
            sbd.append("Címzett: ");
            sbd.append(to);
            sbd.append("\nTárgy: ");
            sbd.append(subject);
            sbd.append("\nÜzenet: ");
            sbd.append(message);

            log.debug(sbd.toString());
        }

        Message msg = new MimeMessage(mailSession);
        try {
            if (log.isDebugEnabled()) {
                log.debug("Eredeti cím: " + to);
            }
            if (Configuration.getEnvironment() != Environment.PRODUCTION) {
                to = Configuration.getDevEmail();
            }
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

            msg.setSubject("[VIR Körök] " + subject);
            msg.setText(message);
            msg.setSentDate(new Date());

            if( Configuration.getEnvironment() != Environment.TESTING ) {
                // TESTING esetén ne küldjünk levelet!
                Transport.send(msg);
                log.info("Levél sikeresen elküldve.");
            }
        } catch (Exception ex) {
            log.error("Hiba az e-mail elküldése közben.", ex);
            return false;
        }

        return true;
    }
}
