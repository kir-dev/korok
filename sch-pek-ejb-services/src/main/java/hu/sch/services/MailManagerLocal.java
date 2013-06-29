package hu.sch.services;

import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface MailManagerLocal {

    boolean sendEmail(String to, String subject, String message);
}
