/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services;

import hu.sch.domain.*;
import hu.sch.kp.services.exceptions.GroupAlreadyExistsException;
import hu.sch.kp.services.exceptions.UserAlreadyExistsException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

/**
 * Felhasználó kezelés, távoli interfész
 * @author hege
 */
@Remote
public interface UserManagerRemote {

    Felhasznalo saveOrAddUser(Felhasznalo user) throws 
            UserAlreadyExistsException, RemoteException;

    Felhasznalo findUserById(Long userId) throws RemoteException;

    void addUserToGroup(Felhasznalo user, Csoport group, Date membership_start, Date membership_end)
            throws RemoteException;

    Csoport saveOrAddGroup(Csoport group) throws GroupAlreadyExistsException, 
            RemoteException;

    void modifyMembership(Felhasznalo user, Csoport group, Date start, Date end)
            throws RemoteException;

    void deleteMembership(Felhasznalo user, Csoport group)
            throws RemoteException;

    List<Felhasznalo> getCsoporttagok(Long csoportId)
            throws RemoteException;
}
