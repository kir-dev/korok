/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.Csoport;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.PontIgeny;
import javax.ejb.Local;
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

    Felhasznalo findUserWithCsoporttagsagokById(Long userId);

    void addUserToGroup(Felhasznalo user, Csoport group, Date membershipStart, Date membershipEnd);

    List<Csoport> getAllGroups();

    Csoport getGroupHierarchy();

    Csoport findGroupByName(String name);

    Csoport findGroupById(Long id);

    Csoport findGroupWithCsoporttagsagokById(Long id);

    Csoport saveOrAddGroup(Csoport group) throws GroupAlreadyExistsException;

    void modifyMembership(Felhasznalo user, Csoport group, Date start, Date end);

    void deleteMembership(Felhasznalo user, Csoport group);

    List<Felhasznalo> getCsoporttagok(Long csoportId);

    List<Felhasznalo> getCsoporttagokWithoutOregtagok(Long csoportId);

    List<BelepoIgeny> getBelepoIgenyekForUser(Felhasznalo felhasznalo);

    List<PontIgeny> getPontIgenyekForUser(Felhasznalo felhasznalo);
}
