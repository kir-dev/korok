/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services;

import javax.ejb.Local;
import hu.sch.domain.*;
import hu.sch.kp.services.exceptions.GroupAlreadyExistsException;
import hu.sch.kp.services.exceptions.UserAlreadyExistsException;
import java.util.Date;
import java.util.List;

/**
 * Felhasználó kezelés, lokális interfész
 * @author hege
 */
@Local
public interface UserManagerLocal {

    List<Felhasznalo> getAllUsers();

    Felhasznalo saveOrAddUser(Felhasznalo user) throws UserAlreadyExistsException;

    Felhasznalo findUserById(Long userId);

    Felhasznalo findUserByLoginName(String loginName);

    void addUserToGroup(Felhasznalo user, Csoport group, Date membership_start, Date membership_end);

    List<Csoport> getAllGroups();
    
    Csoport getGroupHierarchy();

    Csoport findGroupByName(String name);
    
    Csoport findGroupById(Long id);

    Csoport saveOrAddGroup(Csoport group) throws GroupAlreadyExistsException;

    void modifyMembership(Felhasznalo user, Csoport group, Date start, Date end);

    void deleteMembership(Felhasznalo user, Csoport group);
    
    List<Felhasznalo> getCsoporttagok(Long csoportId);
    
    List<BelepoIgeny> getBelepoIgenyekForUser(Felhasznalo felhasznalo);
    
    List<PontIgeny> getPontIgenyekForUser(Felhasznalo felhasznalo);
}
