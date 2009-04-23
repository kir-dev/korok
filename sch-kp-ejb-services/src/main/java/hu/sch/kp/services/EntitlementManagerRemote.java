/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services;

import hu.sch.domain.Felhasznalo;
import javax.ejb.Remote;

/**
 *
 * @author aldaris
 */
@Remote
public interface EntitlementManagerRemote {

    Felhasznalo createUserEntry(Felhasznalo user);
}
