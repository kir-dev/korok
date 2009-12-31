/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.services;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.Valuation;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Semester;
import hu.sch.domain.Group;
import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.ApprovedEntrant;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.User;
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
     * @return
     */
    Valuation getErtekelesWithUzenetek(Long ertekelesId);

    /**
     * Értékelés keresése csoporthoz, adott szemeszterben
     * @param csoport
     * @param szemeszter
     * @return
     */
    Valuation findErtekeles(Group csoport, Semester szemeszter);

    /**
     * Egy csoport összes értékelését keresi ki
     * 
     * @param csoport
     * @return
     */
    List<Valuation> findErtekeles(Group csoport);

    List<Valuation> findApprovedValuations(Group group);

    /**
     * Az adott szemeszterben elbírálatlan értékelések megkeresése.
     * Elbírálatlan az értékelés, amiben vagy a pontigénylés vagy a 
     * belépőigénylés elbírálatlan.
     * 
     * @param szemeszter
     * @return
     */
    List<ValuationStatistic> findElbiralatlanErtekelesStatisztika();

    List<ApprovedEntrant> findElfogadottBelepoIgenyekForSzemeszter(Semester szemeszter);

    boolean ErtekeleseketElbiral(Collection<ConsideredValuation> elbiralas, User felhasznalo);

    /**
     * Új üzenet fűzése egy értékeléshez
     * 
     * @param ertekelesId
     * @param uzeno
     * @param uzenetStr
     */
    void Uzen(Long ertekelesId, User uzeno, String uzenetStr);

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
     * @param csoport
     * @return
     */
    boolean isErtekelesLeadhato(Group csoport);

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
     * @param igenyek
     */
    boolean belepoIgenyekLeadasa(Long ertekelesId, List<EntrantRequest> igenyek);

    /**
     * Értékelést ad vissza ID alapján (de nem adja vissza az igényléseket és az üzeneteket)
     * 
     * @param ertekelesId
     * @return
     */
    Valuation findErtekelesById(Long ertekelesId);

    /**
     * Adott értékeléshez kapcsolódó belépőigényeket adja vissza
     * 
     * @param ertekelesId
     * @return
     */
    List<EntrantRequest> findBelepoIgenyekForErtekeles(Long ertekelesId);

    /**
     * Adott értékeléshez kapcsolódó pontigényeket adja vissza
     * 
     * @param ertekelesId
     * @return
     */
    List<PointRequest> findPontIgenyekForErtekeles(Long ertekelesId);

    /**
     * Az adott értékelésekhez kapcsolódó statisztikát adja vissza
     * (pontátlag, kiosztott belépők típusonként)
     * 
     * @param ertekelesId
     * @return
     */
    List<ValuationStatistic> getStatisztikaForErtekelesek(List<Long> ertekelesId);

    List<ValuationStatistic> findErtekelesStatisztikaForSzemeszter(Semester szemeszter, String sortColumn);

    List<ValuationStatistic> findErtekelesStatisztikaForSzemeszter(Semester szemeszter);

    /**
     * A megadott id-hez tartozó értékelést adja vissza úgy, hogy az tartalmazza
     * a pontigényléseket és a belépőigényléseket is.
     * @param valuationId A keresendő értékelés azonosítója.
     * @return A keresett értékelés pont -és belépőigényléssel együtt.
     */
    Valuation findValuations(Long valuationId);

    void updateValuation(Valuation valuation);
}
