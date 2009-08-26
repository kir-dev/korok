/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.services.MailManagerLocal;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Stateless
public class MailManagerBean implements MailManagerLocal {
    private static final Logger logger = Logger.getLogger(MailManagerBean.class);
    
    @Resource(name = "mail/korokMail")
    private Session mailSession;

    public boolean sendEmail(String to, String subject, String message)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("E-mail küldése ");
        sb.append(to);
        sb.append("-nak.");
        logger.info(sb.toString());

        if (logger.isDebugEnabled())
        {
           StringBuilder sbd = new StringBuilder();

           sbd.append("E-mail küldése\n");
           sbd.append("Címzett: ");
           sbd.append(to);
           sbd.append("\nÜzenet: ");
           sbd.append(message);

           logger.debug(sbd.toString());
        }

        Message msg = new MimeMessage(mailSession);
        try
        {   /*
            if (logger.isDebugEnabled())
            {
            */    // teszt címzés
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("halacs@sch.bme.hu", false));
            /*}
            else
            {
                // rendes címzés
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            }
            */

            msg.setSubject("[VIR] "+subject);
            msg.setText(message);
            msg.setSentDate(new Date());

            Transport.send(msg);
            logger.info("Levél sikeresen elküldve.");
        }
        catch (Exception ex)
        {
            logger.error("Hiba az e-mail elküldése közben.", ex);
            return false;
        }

        return true;
   }
}
