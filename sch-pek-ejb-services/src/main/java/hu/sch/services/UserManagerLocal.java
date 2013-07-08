package hu.sch.services;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Semester;
import hu.sch.domain.SpotImage;
import hu.sch.domain.user.User;
import hu.sch.domain.PointRequest;
import hu.sch.services.exceptions.MembershipAlreadyExistsException;
import javax.ejb.Local;
import java.util.Date;
import java.util.List;

/**
 * Felhasználó kezelés, lokális interfész
 * @author hege
 */
@Local
public interface UserManagerLocal {

    void updateUserAttributes(User user);

    User findUserById(Long userId);

    User findUserWithMembershipsById(Long userId);

    /**
     * Felhasználó felvétele csoportba.
     *
     * @param user              felvett felhasználó
     * @param group             célcsoport, ahova felvesszük
     * @param membershipStart   tagság kezdete
     * @param membershipEnd     tagság vége
     * @param isAuthorized      amennyiben ez igaz, a felhasználó automatikusan "tag" posztot kap.
     */
    void addUserToGroup(User user, Group group, Date membershipStart, Date membershipEnd, boolean isAuthorized)
            throws MembershipAlreadyExistsException;

    List<Group> getAllGroups();

    List<String> getEveryGroupName();

    List<Group> getGroupHierarchy();

    List<Group> findGroupByName(String name);

    Group getGroupByName(String name);

    Group findGroupById(Long id);

    Group findGroupWithMembershipsById(Long id);

    void loadMemberships(Group g);

    void deleteMembership(Membership ms);

    List<User> getMembersForGroup(Long csoportId);

    List<User> getMembersForGroupAndPost(Long groupId, String post);

    List<User> getCsoporttagokWithoutOregtagok(Long csoportId);

    List<EntrantRequest> getBelepoIgenyekForUser(User felhasznalo);

    List<PointRequest> getPontIgenyekForUser(User felhasznalo);

    List<User> getUsersWithPrimaryMembership(Long groupId);

    void groupInfoUpdate(Group cs);

    Membership getMembership(Long memberId);

    void setMemberToOldBoy(Membership user);

    void setUserDelegateStatus(User user, boolean isDelegated);

    void setOldBoyToActive(Membership cst);

    void updateUser(User user);

    void updateGroup(Group group);

    /**
     * Megtalálja egy adott körnek a körvezetőjét
     * @param groupId A körnek az azonosítója, akinek a körvezetőjét keressük
     * @return null, ha nem találta meg a körvezetőt
     */
    User getGroupLeaderForGroup(Long groupId);

    List<Group> getAllGroupsWithCount();

    Membership getMembership(final Long groupId, final Long userId);

    void createNewGroupWithLeader(Group group, User user);

    List<User> searchForUserByName(String name);

    /** Visszaadja az összes olyan szemesztert csökkenő sorrendben, ahol az adott felhasználónak van elfogadott pontkérelme.
     * @param user - A felhasználó, akit vizsgálunk
     * @return A szemeszterek, amikor van elfogadott pontkérelme*/
    public List<Semester> getAllValuatedSemesterForUser(User user);

    /** Visszaadja a felhasználó felvételi pontjait az adott félévre. Ezt úgy kapjuk, hogy az aktuális és az előző félévben
     * szerzett pontjait körönként összeadjuk, majd ezek négyzetes közepét vesszük. Legfeljebb 100 lehet, és egészre csonkolva adjuk vissza.
     * @param user - A felhasználó, akinek a pontjait vizsgáljuk
     * @param semester - Erre a szemeszterre számolunk
     * @return A felhasználó felvételi pontjai az adott félévre*/
    public int getSemesterPointForUser(User user, Semester semester);

    public Group getParentGroups(Long id);

    /**
     * Visszaadja az adott kör alá tartozó köröket.
     *
     * @param id A kör azonosítója
     * @return A kör alá tartozó alkörök
     */
    List<Group> getChildGroups(Long id);

    /**
     * Lekérjük egy adott felhasználóhoz tartozó SPOT képet, ha van ilyen
     * 
     * @param user
     * @return spot kép, vagy null
     */
    SpotImage getSpotImage(User user);

    /**
     * Az adott UID-val rendelkező usernek megpróbáljuk beállítani a javasolt
     * fotót.
     *
     * @param userId
     * @return sikeres volt-e a beállítás
     */
    boolean acceptRecommendedPhoto(String userId);

    /**
     * Az adott felhasználó elutasította a javasolt fotót, töröljük a
     * SpotImage-t a DB-ből.
     *
     * @param user
     */
    void declineRecommendedPhoto(User user);
}
