package hu.sch.services;

import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.EntrantRequest;
import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.GivenPoint;
import hu.sch.domain.Group;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Semester;
import hu.sch.domain.user.User;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationData;
import hu.sch.domain.ValuationMessage;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.rest.ApprovedEntrant;
import hu.sch.domain.rest.PointInfo;
import hu.sch.services.exceptions.UserNotFoundException;
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

     /**
     * Megkeresi azokat a felhasználókat, akik kaptak a megadott belépőből az adott
     * szemeszterben és egy CSV-nek formázott Stringgel tér vissza
     *
     * @param semester
     * @param entrantType
     * @param minEntrantNum csak az ennyi vagy ennél több belépőt kapott emberek
     * szerepeljenek
     * @return azon felhasználókról export String, akik legalább <pre>mitEntrantNum</pre>
     * db adott belépőt kaptak a félévben
     */
    String findApprovedEntrantsForExport(Semester semester,
            EntrantType entrantType, int mintEntrantNum);

    void considerValuations(Collection<ConsideredValuation> elbiralas)
            throws NoExplanationException, NothingChangedException, AlreadyModifiedException;

    void considerValuation(ConsideredValuation v)
            throws NoExplanationException, NothingChangedException, AlreadyModifiedException;

    Valuation updateValuation(Valuation valuation)
            throws AlreadyModifiedException;

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
    List<PointInfo> getPointInfoForUid(String uid, Semester semester);

    /**
     * Visszaadja a neptunnal azonosított felhasználó adott félévben kapott belépőit.
     *
     * @param neptun
     * @param semester
     * @return
     * @throws UserNotFoundException
     */
    List<ApprovedEntrant> getApprovedEntrants(final String neptun,
            final Semester semester) throws UserNotFoundException;
}
