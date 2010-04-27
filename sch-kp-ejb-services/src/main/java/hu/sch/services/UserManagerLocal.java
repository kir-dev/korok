/**
 * Copyright (c) 2009, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.services;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.Semester;
import hu.sch.domain.User;
import hu.sch.domain.PointRequest;
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

    User findUserWithCsoporttagsagokById(Long userId);

    void addUserToGroup(User user, Group group, Date membershipStart, Date membershipEnd);

    List<Group> getAllGroups();

    List<String> getEveryGroupName();

    List<Group> getGroupHierarchy();

    List<Group> findGroupByName(String name);

    Group getGroupByName(String name);

    Group findGroupById(Long id);

    Group findGroupWithCsoporttagsagokById(Long id);

    void deleteMembership(Membership ms);

    List<User> getMembersForGroup(Long csoportId);

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

    public List<Group> getParentGroups(Long id);
}
