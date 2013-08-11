package hu.sch.ejb;

import hu.sch.domain.config.Configuration;
import hu.sch.domain.config.Configuration.Environment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.Local;
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
public class MailManagerBean {

    private static final Logger log = LoggerFactory.getLogger(MailManagerBean.class);
    private static final String STRINGS_FILE = "/META-INF/mail_messages.properties";
    private static final Properties mailStrings = new Properties();
    //keys in mail_messages.properties, package protected fields
    //system exception report
    static final String MAIL_SYSTEM_EXCEPTIONREPORT_SUBJECT = "system.exceptionreport.subject";
    static final String MAIL_SYSTEM_EXCEPTIONREPORT_BODY = "system.exceptionreport.body";
    //valuation messages
    static final String MAIL_VALUATIONMESSAGE_SUBJECT = "valuationmessage.subject";
    static final String MAIL_VALUATIONMESSAGE_TO_GROUPLEADER_BODY = "valuationmessage.to.groupleader.body";
    static final String MAIL_VALUATIONMESSAGE_TO_JETI_BODY = "valuationmessage.to.jeti.body";
    //primary membership changed
    static final String MAIL_PRIMARYMEMBERSHIP_CHANGED_SUBJECT = "primarymembership.changed.subject";
    static final String MAIL_PRIMARYMEMBERSHIP_CHANGED_BODY = "primarymembership.changed.body";
    //username reminder
    static final String MAIL_USERNAME_REMINDER_SUBJECT = "username.reminder.subject";
    static final String MAIL_USERNAME_REMINDER_BODY = "username.reminder.body";
    static final String MAIL_USERNAME_REMINDER_BODY_NEWBIE = "username.reminder.body.newbie";
    @Resource(name = "mail/korokMail")
    private Session mailSession;

    static {
        try {
            final InputStream mailStringsStream = MailManagerBean.class.getResourceAsStream(STRINGS_FILE);
            mailStrings.load(mailStringsStream);
        } catch (IOException ex) {
            log.error(String.format("Can't read file=%s from ejb-impl.jar", STRINGS_FILE), ex);
        }
    }

    /**
     * Sends email through JDBC mail resource with the given attributes.
     * <br/>NOTE: if {@link Environment#TESTING} is active, it doesn't send email.
     *
     * @param to recipient of the message.
     * @param subject part after "[VIR Körök]"
     * @param message
     * @return true if sending was successful.
     */
    public boolean sendEmail(String to, final String subject, final String message) {
        log.info("E-mail küldés, címzett={}", to);

        if (log.isDebugEnabled() || !Environment.PRODUCTION.equals(Configuration.getEnvironment())) {
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

    /**
     * Returns a string resource from the property file which contains the mail
     * subject and body strings. We should use this only from ejb, so it's
     * package protected.
     *
     * @param key one of MailManagerBean.MAIL_* constant.
     * @return the value in property list with the specified key value.
     * @throws IllegalArgumentException when key is null or empty.
     */
    static String getMailString(final String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key can't be null or empty in getMailString");
        }

        return mailStrings.getProperty(key);
    }
}
