/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.services.exceptions;

/**
 *
 * @author tomi
 */
public class LdapDeleteEntryFailedException extends Exception {

    public LdapDeleteEntryFailedException() {
    }

    public LdapDeleteEntryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
