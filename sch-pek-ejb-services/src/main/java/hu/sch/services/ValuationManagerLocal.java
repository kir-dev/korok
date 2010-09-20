/**
 * Copyright (c) 2008-2010, Peter Major
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
import hu.sch.domain.Valuation;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Semester;
import hu.sch.domain.Group;
import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.ApprovedEntrant;
import hu.sch.domain.GivenPoint;
import hu.sch.domain.ValuationData;
import hu.sch.domain.ValuationMessage;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.User;
import hu.sch.domain.rest.PointInfo;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.services.exceptions.valuation.NoExplanationException;
import hu.sch.services.exceptions.valuation.NothingChangedException;
import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hege
 * @author messo
 */
@Local
public interface ValuationManagerLocal {

    /**
     * Új értékelés mentése
     * 
     * @param ertekeles
     */
    void createValuation(Valuation ertekeles);

    /**
     * Értékelés keresése csoporthoz, adott szemeszterben
     * @param csoport
     * @param szemeszter
     * @return Adott csoporthoz, szemeszterhez tartozó értékelés
     */
    Valuation findLatestValuation(Group csoport, Semester szemeszter);

    /**
     * Értékelés keresés az ID-hez, úgy, hogy a kellő információkat is lekérjük mellé
     * @param valuationId
     * @return keresett értékelés
     */
    Valuation findValuationForDetails(long valuationId);

    /**
     * Egy csoport összes értékelését keresi ki
     * 
     * @param csoport
     * @return Csoporthoz tartozó értékelések listája
     */
    List<Valuation> findLatestValuationsForGroup(Group csoport);

    List<Valuation> findApprovedValuations(Group group);

    /**
     * Az adott szemeszterben elbírálatlan értékelések megkeresése.
     * Elbírálatlan az értékelés, amiben vagy a pontigénylés vagy a 
     * belépőigénylés elbírálatlan.
     * 
     * @return Elbírálatlan értékelések statisztikája
     */
    List<ValuationStatistic> findElbiralatlanErtekelesStatisztika();

    List<ApprovedEntrant> findElfogadottBelepoIgenyekForSzemeszter(Semester szemeszter);

    void considerValuations(Collection<ConsideredValuation> elbiralas)
            throws NoExplanationException, NothingChangedException, AlreadyModifiedException;

    void considerValuation(ConsideredValuation v)
            throws NoExplanationException, NothingChangedException, AlreadyModifiedException;

    Valuation updateValuation(Valuation valuation)
            throws NothingChangedException, AlreadyModifiedException;

    /**
     * Pontigényeket ment egy értékeléshez
     * Amennyiben a pontigények már léteztek, egyesével felülírja őket.
     *
     * @param valuation
     * @param igenyek
     * @return az értékelés, aminek a pontjait szerkesztettük, ha új verzió jött létre, akkor az
     */
    Valuation updatePointRequests(Valuation valuation, List<PointRequest> igenyek)
            throws AlreadyModifiedException;

    /**
     * Új belépőigényeket ment egy értékeléshez
     * Amennyiben a belépőigények már léteztek, egyesével felülírja őket.
     *
     * @param valuation
     * @param igenyek
     * @return az értékelés, aminek a belépőit szerkesztettük, ha új verzió jött létre, akkor az
     */
    Valuation updateEntrantRequests(Valuation valuation, List<EntrantRequest> igenyek)
            throws AlreadyModifiedException, NoExplanationException;

    /**
     * Üzenetek lekérése az adott csoport adott félévéhez tartozó értékeléséhez.
     * 
     * @param group
     * @param semester
     * @return üzenetek listája
     */
    List<ValuationMessage> getMessages(Group group, Semester semester);

    /**
     * Az előkészített üzenetet elmenti és értesítő emaileket küld ki.
     *
     * @param msg az üzenet, amit menteni szeretnénk
     */
    void addNewMessage(ValuationMessage msg);

    /**
     * Új értékelés létrehozása az aktuális szemeszterben
     * 
     * @param group Melyik csoporthoz adtak le értékelést
     * @param sender Az értékelés feladója
     * @param valuationText Szöveges értékelés
     * @param principle Pontozási elvek
     */
    void addNewValuation(Group group, User sender, String valuationText, String principle);

    /**
     * Az aktuális szemeszterben leadhat-e új értékelést az adott csoport.
     * Leadhat, ha leadási időszak van és még nem adott le.
     * 
     * @param group
     * @return Az adott kör leadhat-e értékelést
     */
    boolean isErtekelesLeadhato(Group group);

    /**
     * Értékelést ad vissza ID alapján (de nem adja vissza az igényléseket és az üzeneteket)
     * 
     * @param ertekelesId
     * @return ID-hoz tartozó értékelés
     */
    Valuation findErtekelesById(Long ertekelesId);

    /**
     * Adott értékeléshez kapcsolódó belépőigényeket adja vissza
     * 
     * @param ertekelesId
     * @return Értékeléshez tartozó belépőigények
     */
    List<EntrantRequest> findBelepoIgenyekForErtekeles(Long ertekelesId);

    /**
     * Adott értékeléshez kapcsolódó pontigényeket adja vissza
     * 
     * @param ertekelesId
     * @return Értékeléshez tartozó pontigények
     */
    List<PointRequest> findPontIgenyekForErtekeles(Long ertekelesId);

    /**
     * Adott értékeléshez kapcsolódó pont és belépőigényeket adja vissza
     *
     * @param valuationId
     * @return Értékeléshez tartozó kombinált igények (pont és belépő)
     */
    List<ValuationData> findRequestsForValuation(Long valuationId);

    List<ValuationData> findRequestsForUser(User u, Long groupId);

    /**
     * Az adott értékelésekhez kapcsolódó statisztikát adja vissza
     * (pontátlag, kiosztott belépők típusonként)
     * 
     * @param ertekelesId
     * @return Értékelésekhez tartozó statisztika
     */
    List<ValuationStatistic> getStatisztikaForErtekelesek(List<Long> ertekelesId);

    /**
     * Az adott értékeléshez kapcsolódó statisztikát adja vissza
     * (pontátlag, kiosztott belépők típusonként)
     *
     * @param valuationId az értékelés azonosítója
     * @return Értékeléshez tartozó statisztika
     */
    ValuationStatistic getStatisticForValuation(Long valuationId);

    /**
     * Az adott szemeszterhez tartozó értékelésstatisztikát adja vissza
     * (pontátlag, összpontszám, belépők típusonként)
     *
     * @return értékelésekhez tartozó statisztikák listája
     */
    List<ValuationStatistic> findValuationStatisticForSemester();

    List<ValuationStatistic> findValuationStatisticForVersions(Group group, Semester semester);

    List<GivenPoint> getPointsForKfbExport(Semester semester);

    /**
     * Törli a csoporthoz és az adott félévhez tartozó értékeléseket (egy értékelés több verzióját).
     * Nem ajánlott csak teszteléshez használni, éles DB-n inkább találjunk ki erre valamit backupot.
     *
     * @param group
     * @param semester
     */
    void deleteValuations(Group group, Semester semester);

    Long findLatestVersionsId(Group group, Semester semester);
    /**
     * Visszaadja egy adott uid-hez tartozó pontozásokat körre lebontva az aktuális
     * szemeszterre.
     *
     * @param uid A felhasználó azonosítója
     * @return A pontok listája
     */
    List<PointInfo> getPointInfoForUid(String uid);
}
