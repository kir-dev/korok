/**
 * Copyright (c) 2009-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
