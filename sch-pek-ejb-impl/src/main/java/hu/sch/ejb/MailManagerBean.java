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
    public boolean sendEmail(String to, final String subject, final String message) {
        log.info("E-mail küldés, címzett={}", to);

        if (log.isDebugEnabled() || Configuration.getEnvironment() == Environment.TESTING) {
            log.debug("Tárgy={}\nÜzenet={}", subject, message);
        }

        final Message mail = new MimeMessage(mailSession);
        try {
            if (Configuration.getEnvironment() != Environment.PRODUCTION) {
                to = Configuration.getDevEmail();
                log.debug("[dev mód] új címzett={}", to);
            }

            mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

            mail.setSubject("[VIR Körök] " + subject);
            mail.setText(message);
            mail.setSentDate(new Date());

            if (Configuration.getEnvironment() != Environment.TESTING) {
                // TESTING esetén ne küldjünk levelet!
                Transport.send(mail);
                log.info("Levél sikeresen elküldve.");
            }
        } catch (Exception ex) {
            log.error("Hiba az e-mail elküldése közben.", ex);
            return false;
        }

        return true;
    }
}
