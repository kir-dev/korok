/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services;

import hu.sch.domain.Felhasznalo;
import javax.ejb.Local;

/**
 *
 * @author aldaris
 */
@Local
public interface EntitlementManagerLocal {

    Felhasznalo createUserEntry(Felhasznalo user);
}
