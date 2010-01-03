/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.services.MailManagerLocal;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

/**
 *
 * @author aldaris
 */
@Stateless
public class MailManagerBean implements MailManagerLocal {

    private static final Logger log = Logger.getLogger(MailManagerBean.class);
    @Resource(name = "mail/korokMail")
    private Session mailSession;

    @Override
    public boolean sendEmail(String to, String subject, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("E-mail küldése ");
        sb.append(to);
        sb.append("-nak.");
        log.info(sb.toString());

        if (log.isDebugEnabled()) {
            StringBuilder sbd = new StringBuilder();

            sbd.append("E-mail küldése\n");
            sbd.append("Címzett: ");
            sbd.append(to);
            sbd.append("\nÜzenet: ");
            sbd.append(message);

            log.debug(sbd.toString());
        }

        Message msg = new MimeMessage(mailSession);
        try {
            // teszt címzés
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("majorpetya@sch.bme.hu", false));
            // rendes címzés
            if (log.isDebugEnabled()) {
                log.debug("Eredeti cím: " + to);
            }
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

            msg.setSubject("[VIR Körök] " + subject);
            msg.setText(message);
            msg.setSentDate(new Date());

            Transport.send(msg);
            log.info("Levél sikeresen elküldve.");
        } catch (Exception ex) {
            log.error("Hiba az e-mail elküldése közben.", ex);
            return false;
        }

        return true;
    }
}
