/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services;

import hu.sch.domain.*;
import hu.sch.kp.services.exceptions.GroupAlreadyExistsException;
import java.rmi.RemoteException;
import javax.ejb.Remote;

/**
 * Felhasználó kezelés, távoli interfész.
 * @author hege
 */
@Remote
public interface UserManagerRemote {

    /**
     * Létrehoz egy új VIR usert az adatbázisban.
     * 
     * @param firstName
     * @param lastName
     * @param nickName
     * @return VIR-ID
     * @throws java.rmi.RemoteException
     */
    Long createUser(String firstName, String lastName, String nickName) 
            throws RemoteException;
    
    /**
     * Egy felhasználó nevét írja be adatbázisba.
     * 
     * @param userId
     * @param firstName
     * @param lastName
     * @param nickName
     * @throws java.rmi.RemoteException
     */
    void changeUser(Long userId, String firstName, String lastName, String nickName)
            throws RemoteException;

    /**
     * Egy csoport aktív tagjává teszi a usert.
     * 
     * @param userId
     * @param groupId
     * @throws java.rmi.RemoteException
     */
    void createActiveMembership(Long userId, Long groupId)
            throws RemoteException;
    
    /**
     * Egy csoport öregtagjává teszi a usert.
     * 
     * @param userId
     * @param groupId
     * @throws java.rmi.RemoteException
     */
    void inactivateMembership(Long userId, Long groupId)
            throws RemoteException;

    /**
     * Új csoportot hoz létre az adott névvel és szülő csoporttal.
     * 
     * @param groupName
     * @param parentGroupId
     * @return
     * @throws hu.sch.kp.services.exceptions.GroupAlreadyExistsException
     * @throws java.rmi.RemoteException
     */
    Long createGroup(String groupName, Long parentGroupId) throws GroupAlreadyExistsException, 
            RemoteException;
}
