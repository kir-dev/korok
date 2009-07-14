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
    private static final Logger logger = Logger.getLogger(ValuationManagerBean.class);
    
    @Resource(name = "mail/korokMail")
    private Session mailSession;

    public boolean sendEmail(String to, String subject, String message) {
        System.out.println("E-mail küldése " + to + "-nak.");

        logger.info("E-mail küldése\n" +
                "Címzett: " + to + "\n" +
                "Üzenet: " + message);

        Message msg = new MimeMessage(mailSession);
        try {
            // teszt címzés
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("halacs@sch.bme.hu", false));
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("majorpetya@sch.bme.hu", false));

            // rendes címzés
            //msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

            msg.setSubject("[VIR] "+subject);
            msg.setText(message);
            msg.setSentDate(new Date());

            Transport.send(msg);
            logger.info("Levél sikeresen elküldve.");

        } catch (Exception ex) {
            logger.error("Hiba az e-mail elküldése közben.");
            ex.printStackTrace();
            return false;
        }

        return true;
    }
}
