/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.services;

import hu.sch.domain.User;
import javax.ejb.Remote;

/**
 *
 * @author aldaris
 */
@Remote
public interface EntitlementManagerRemote {

    User createUserEntry(User user);
    User findUser(String neptun, String email);
    User findUser(Long virId);
}
