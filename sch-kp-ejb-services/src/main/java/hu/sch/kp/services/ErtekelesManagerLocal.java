/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.services;

import hu.sch.domain.BelepoIgeny;
import hu.sch.domain.Ertekeles;
import hu.sch.domain.PontIgeny;
import hu.sch.domain.Szemeszter;
import hu.sch.domain.Csoport;
import hu.sch.domain.ElbiraltErtekeles;
import hu.sch.domain.ElfogadottBelepo;
import hu.sch.domain.ErtekelesStatisztika;
import hu.sch.domain.Felhasznalo;
import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author hege
 */
@Local
public interface ErtekelesManagerLocal {

    /**
     * Új értékelés mentése
     * 
     * @param ertekeles
     */
    void createErtekeles(Ertekeles ertekeles);
        
    /**
     * Egy teljes értékelést visszakeres, üzenetekkel együtt
     * 
     * @param ertekelesId
     * @return
     */
    Ertekeles getErtekelesWithUzenetek(Long ertekelesId);

    /**
     * Értékelés keresése csoporthoz, adott szemeszterben
     * @param csoport
     * @param szemeszter
     * @return
     */
    Ertekeles findErtekeles(Csoport csoport, Szemeszter szemeszter);

    /**
     * Egy csoport összes értékelését keresi ki
     * 
     * @param csoport
     * @return
     */
    List<Ertekeles> findErtekeles(Csoport csoport);
    
    /**
     * Az adott szemeszterben elbírálatlan értékelések megkeresése.
     * Elbírálatlan az értékelés, amiben vagy a pontigénylés vagy a 
     * belépőigénylés elbírálatlan.
     * 
     * @param szemeszter
     * @return
     */
    public List<ErtekelesStatisztika> findElbiralatlanErtekelesStatisztika();
    
    public List<ElfogadottBelepo> findElfogadottBelepoIgenyekForSzemeszter(Szemeszter szemeszter);

    void ErtekeleseketElbiral(Collection<ElbiraltErtekeles> elbiralas, Felhasznalo felhasznalo);

    /**
     * Új üzenet fűzése egy értékeléshez
     * 
     * @param ertekelesId
     * @param uzeno
     * @param uzenetStr
     */
    void Uzen(Long ertekelesId, Felhasznalo uzeno, String uzenetStr);

    /**
     * Új értékelés létrehozása az aktuális szemeszterben
     * 
     * @param csoport
     * @param felado
     * @param szovegesErtekeles
     */
    void ujErtekeles(Csoport csoport, Felhasznalo felado, String szovegesErtekeles);

    /**
     * Az aktuális szemeszterben leadhat-e új értékelést az adott csoport.
     * Leadhat, ha leadási időszak van és még nem adott le.
     * 
     * @param csoport
     * @return
     */
    boolean isErtekelesLeadhato(Csoport csoport);

    /**
     * Új üzenetet fűz egy értékeléshez
     * 
     * @param ertekelesId
     * @param felado
     * @param uzenet
     */
    void ujErtekelesUzenet(Long ertekelesId, Felhasznalo felado, String uzenet);

    /**
     * Pontigényeket ment egy értékeléshez
     * Amennyiben a pontigények már léteztek, egyesével felülírja őket.
     * 
     * @param ertekelesId
     * @param igenyek
     */
    void pontIgenyekLeadasa(Long ertekelesId, List<PontIgeny> igenyek);
    
    /**
     * Új belépőigényeket ment egy értékeléshez
     * Amennyiben a belépőigények már léteztek, egyesével felülírja őket.
     * 
     * @param ertekelesId
     * @param igenyek
     */
    void belepoIgenyekLeadasa(Long ertekelesId, List<BelepoIgeny> igenyek);

    /**
     * Értékelést ad vissza ID alapján (de nem adja vissza az igényléseket és az üzeneteket)
     * 
     * @param ertekelesId
     * @return
     */
    Ertekeles findErtekelesById(Long ertekelesId);
    
    /**
     * Adott értékeléshez kapcsolódó belépőigényeket adja vissza
     * 
     * @param ertekelesId
     * @return
     */
    List<BelepoIgeny> findBelepoIgenyekForErtekeles(Long ertekelesId);
    
    /**
     * Adott értékeléshez kapcsolódó pontigényeket adja vissza
     * 
     * @param ertekelesId
     * @return
     */
    List<PontIgeny> findPontIgenyekForErtekeles(Long ertekelesId);

    /**
     * Az adott értékelésekhez kapcsolódó statisztikát adja vissza
     * (pontátlag, kiosztott belépők típusonként)
     * 
     * @param ertekelesId
     * @return
     */
    List<ErtekelesStatisztika> getStatisztikaForErtekelesek(List<Long> ertekelesId);

    List<ErtekelesStatisztika> findErtekelesStatisztikaForSzemeszter(Szemeszter szemeszter, String sortColumn);
    
    List<ErtekelesStatisztika> findErtekelesStatisztikaForSzemeszter(Szemeszter szemeszter);
    
}
