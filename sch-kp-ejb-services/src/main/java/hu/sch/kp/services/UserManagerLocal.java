/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.services;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.Csoport;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.PontIgeny;
import hu.sch.domain.TagsagTipus;
import javax.ejb.Local;
import java.util.Date;
import java.util.List;

/**
 * Felhasználó kezelés, lokális interfész
 * @author hege
 */
@Local
public interface UserManagerLocal {

    List<Felhasznalo> getAllUsers();

    void updateUserAttributes(Felhasznalo user);

    Felhasznalo findUserById(Long userId);

    Felhasznalo findUserWithCsoporttagsagokById(Long userId);

    void addUserToGroup(Felhasznalo user, Csoport group, Date membershipStart, Date membershipEnd);

    List<Csoport> getAllGroups();

    List<String> getEveryGroupName();

    Csoport getGroupHierarchy();

    List<Csoport> findGroupByName(String name);

    Csoport getGroupByName(String name);

    Csoport findGroupById(Long id);

    Csoport findGroupWithCsoporttagsagokById(Long id);

    void modifyMembership(Felhasznalo user, Csoport group, Date start, Date end);

    void deleteMembership(Felhasznalo user, Csoport group);

    List<Felhasznalo> getCsoporttagok(Long csoportId);

    List<Felhasznalo> getCsoporttagokWithoutOregtagok(Long csoportId);

    List<BelepoIgeny> getBelepoIgenyekForUser(Felhasznalo felhasznalo);

    List<PontIgeny> getPontIgenyekForUser(Felhasznalo felhasznalo);

    void groupInfoUpdate(Csoport cs);

    Csoporttagsag getCsoporttagsag(Long userId, Long groupId);

    void updateMemberRights(Csoporttagsag oldOne, Csoporttagsag newOne, TagsagTipus type);

    void setMemberToOldBoy(Csoporttagsag user);

    void setOldBoyToActive(Csoporttagsag cst);

    Felhasznalo findKorvezetoForCsoport(Long csoportId);
}
