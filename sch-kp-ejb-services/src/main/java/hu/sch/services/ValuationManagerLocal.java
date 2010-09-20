/**
 * Copyright (c) 2009-2010, Peter Major
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
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.User;
import hu.sch.domain.rest.PointInfo;
import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hege
 */
@Local
public interface ValuationManagerLocal {

    /**
     * Új értékelés mentése
     * 
     * @param ertekeles
     */
    void createErtekeles(Valuation ertekeles);

    /**
     * Egy teljes értékelést visszakeres, üzenetekkel együtt
     * 
     * @param ertekelesId
     * @return Értékelés üzenetekkel együtt
     */
    Valuation getErtekelesWithUzenetek(Long ertekelesId);

    /**
     * Értékelés keresése csoporthoz, adott szemeszterben
     * @param csoport
     * @param szemeszter
     * @return Adott csoporthoz, szemeszterhez tartozó értékelés
     */
    Valuation findErtekeles(Group csoport, Semester szemeszter);

    /**
     * Egy csoport összes értékelését keresi ki
     * 
     * @param csoport
     * @return Csoporthoz tartozó értékelések listája
     */
    List<Valuation> findErtekeles(Group csoport);

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

    boolean ertekeleseketElbiral(Collection<ConsideredValuation> elbiralas, User felhasznalo);

    /**
     * Új üzenet fűzése egy értékeléshez
     * 
     * @param ertekelesId
     * @param uzeno
     * @param uzenetStr
     */
    void addMessageToValuation(Long ertekelesId, User uzeno, String uzenetStr);

    /**
     * Új értékelés létrehozása az aktuális szemeszterben
     * 
     * @param csoport
     * @param felado
     * @param szovegesErtekeles
     */
    void ujErtekeles(Group csoport, User felado, String szovegesErtekeles);

    /**
     * Az aktuális szemeszterben leadhat-e új értékelést az adott csoport.
     * Leadhat, ha leadási időszak van és még nem adott le.
     * 
     * @param group
     * @return Az adott kör leadhat-e értékelést
     */
    boolean isErtekelesLeadhato(Group group);

    /**
     * Új üzenetet fűz egy értékeléshez
     * 
     * @param ertekelesId
     * @param felado
     * @param uzenet
     */
    void ujErtekelesUzenet(Long ertekelesId, User felado, String uzenet);

    /**
     * Pontigényeket ment egy értékeléshez
     * Amennyiben a pontigények már léteztek, egyesével felülírja őket.
     * 
     * @param ertekelesId
     * @param igenyek
     */
    void pontIgenyekLeadasa(Long ertekelesId, List<PointRequest> igenyek);

    /**
     * Új belépőigényeket ment egy értékeléshez
     * Amennyiben a belépőigények már léteztek, egyesével felülírja őket.
     * 
     * @param ertekelesId
     * @param igenyek false ha hibás formátumú az igénylés
     */
    boolean belepoIgenyekLeadasa(Long ertekelesId, List<EntrantRequest> igenyek);

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

    /**
     * A megadott id-hez tartozó értékelést adja vissza úgy, hogy az tartalmazza
     * a pontigényléseket és a belépőigényléseket is.
     * @param valuationId A keresendő értékelés azonosítója.
     * @return A keresett értékelés pont -és belépőigényléssel együtt.
     */
    Valuation findValuations(Long valuationId);

    void updateValuation(Long valuationId, String text);

    List<GivenPoint> getPointsForKfbExport(Semester semester);

    /**
     * Visszaadja egy adott uid-hez tartozó pontozásokat körre lebontva az aktuális
     * szemeszterre.
     *
     * @param uid A felhasználó azonosítója
     * @return A pontok listája
     */
    List<PointInfo> getPointInfoForUid(String uid);
}
