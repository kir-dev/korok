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

/**
 *
 * @author aldaris
 */
@Stateless
public class MailManagerBean implements MailManagerLocal {

    @Resource(name = "mail/korokMail")
    private Session mailSession;

    public boolean sendEmail(String to, String subject, String message) {
        Message msg = new MimeMessage(mailSession);
        try {
            msg.setSubject(subject);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setText(message);
            msg.setSentDate(new Date());

            Transport.send(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }
}
