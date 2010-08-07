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
package hu.sch.ejb;

import hu.sch.domain.GivenPoint;
import hu.sch.domain.ValuationData;
import hu.sch.domain.EntrantRequest;
import hu.sch.domain.EntrantType;
import hu.sch.domain.Group;
import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.ApprovedEntrant;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.ValuationStatus;
import hu.sch.domain.ValuationMessage;
import hu.sch.domain.User;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Semester;
import hu.sch.services.MailManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author hege
 */
@Stateless
@SuppressWarnings("unchecked")
public class ValuationManagerBean implements ValuationManagerLocal {

    private static final Logger logger = Logger.getLogger(ValuationManagerBean.class);
    @PersistenceContext
    EntityManager em;
    @EJB
    UserManagerLocal userManager;
    @EJB
    SystemManagerLocal systemManager;
    @EJB
    MailManagerLocal mailManager;

    @Override
    public void createErtekeles(Valuation ertekeles) {
        em.persist(ertekeles);
        em.flush();
    }

    @Override
    public Valuation findErtekeles(Group csoport, Semester szemeszter) {
        Query q = em.createNamedQuery(Valuation.findBySemesterAndGroup);
        q.setParameter("semester", szemeszter);
        q.setParameter("group", csoport);

        try {
            return (Valuation) q.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    @Override
    public List<ValuationStatistic> getStatisztikaForErtekelesek(List<Long> valIds) {
        Query q = em.createNamedQuery(Valuation.findStatisticByValuations);
        q.setParameter("ids", valIds);

        return q.getResultList();
    }

    @Override
    public ValuationStatistic getStatisticForValuation(Long valuationId) {
        Query q = em.createNamedQuery(Valuation.findStatisticByValuation);
        q.setParameter("valuationId", valuationId);

        return (ValuationStatistic) q.getSingleResult();
    }

    @Override
    public List<ValuationStatistic> findValuationStatisticForSemester() {
        Query q = em.createNamedQuery(Valuation.findStatisticBySemester);
        q.setParameter("semester", systemManager.getSzemeszter());

        return q.getResultList();
    }

    @Override
    public List<ValuationStatistic> findElbiralatlanErtekelesStatisztika() {
        Query q = em.createNamedQuery(Valuation.findStatisticBySemesterAndStatuses);
        q.setParameter("semester", systemManager.getSzemeszter());
        q.setParameter("pointStatus", ValuationStatus.ELBIRALATLAN);
        q.setParameter("entrantStatus", ValuationStatus.ELBIRALATLAN);

        return q.getResultList();
    }

    protected Valuation elbiralastElokeszit(Valuation ertekeles, User elbiralo) {
        Valuation e = findErtekeles(ertekeles.getGroup(), ertekeles.getSemester());
        e.setConsideredBy(elbiralo);
        e.setLastConsidered(new Date());

        return e;
    }

    private void PontIgenyElbiral(Valuation ertekeles, User elbiralo, boolean elfogad) {
        Valuation e = elbiralastElokeszit(ertekeles, elbiralo);
        if (elfogad) {
            e.setPointStatus(ValuationStatus.ELFOGADVA);
        } else {
            e.setPointStatus(ValuationStatus.ELUTASITVA);
        }

    }

    private void BelepoIgenyElbiral(Valuation ertekeles, User elbiralo, boolean elfogad) {
        Valuation e = elbiralastElokeszit(ertekeles, elbiralo);
        if (elfogad) {
            e.setEntrantStatus(ValuationStatus.ELFOGADVA);
        } else {
            e.setEntrantStatus(ValuationStatus.ELUTASITVA);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public boolean ertekeleseketElbiral(Collection<ConsideredValuation> elbiralas, User felhasznalo) {
        for (ConsideredValuation ee : elbiralas) {

            if ((ee.getPointStatus().equals(ValuationStatus.ELUTASITVA)
                    || ee.getEntrantStatus().equals(ValuationStatus.ELUTASITVA))
                    && ee.getExplanation() == null) {
                return false;
            } else {


                StringBuilder sb = new StringBuilder(140);
                sb.append((ee.getExplanation() == null ? "Nincs indoklás megadva." : ee.getExplanation()));
                sb.append("\n");
                sb.append("------------------\n");
                sb.append(ee.getValuation().getGroup());
                sb.append(" kör ");
                sb.append(ee.getValuation().getSemester());
                sb.append(" félévi értékelése megváltozott.\nAz új adatok:");
                if (ee.getEntrantStatus() == ee.getValuation().getEntrantStatus()) {
                    sb.append("\nBelépőpontozás: nincs módosítás");
                } else {
                    sb.append("\nBelépőpontozás: ");
                    sb.append(ee.getValuation().getEntrantStatus());
                    sb.append(" => ");
                    sb.append(ee.getEntrantStatus());
                }

                if (ee.getPointStatus() == ee.getValuation().getPointStatus()) {
                    sb.append("\nKözösségi pontok: nincs módosítás");
                } else {
                    sb.append("\nKözösségi pontok: ");
                    sb.append(ee.getValuation().getPointStatus());
                    sb.append(" => ");
                    sb.append(ee.getPointStatus());
                }

                ee.setExplanation(sb.toString());
            }

            if (ee.getPointStatus().equals(ValuationStatus.ELFOGADVA) || ee.getPointStatus().equals(ValuationStatus.ELUTASITVA)) {
                PontIgenyElbiral(ee.getValuation(), felhasznalo, ee.getPointStatus().equals(ValuationStatus.ELFOGADVA));
            }
            if (ee.getEntrantStatus().equals(ValuationStatus.ELFOGADVA) || ee.getEntrantStatus().equals(ValuationStatus.ELUTASITVA)) {
                BelepoIgenyElbiral(ee.getValuation(), felhasznalo, ee.getEntrantStatus().equals(ValuationStatus.ELFOGADVA));
            }
            if (ee.getExplanation() != null) {
                addMessageToValuation(ee.getValuation().getId(), felhasznalo, ee.getExplanation());
            }
        }
        return true;
    }

    @Override
    public void addMessageToValuation(Long ertekelesId, User uzeno, String uzenetStr) {
        Valuation ertekeles = em.find(Valuation.class, ertekelesId);

        ValuationMessage uzenet = new ValuationMessage();
        uzenet.setMessage(uzenetStr);
        uzenet.setSender(uzeno);
        uzenet.setDate(new Date());
        uzenet.setValuation(ertekeles);
        ertekeles.getMessages().add(uzenet);
        ertekeles.setLastModified(new Date());

        em.persist(uzenet);
        em.merge(ertekeles);

        // E-mail értesítés küldése az üzenetről
        String emailText = uzenet.toString() + "\n\n\n"
                + "Az értékeléseidet megtekintheted a https://korok.sch.bme.hu/korok/valuation link alatt.\n"
                + "Ez egy automatikusan generált e-mail.";

        // adott kör körezetőionek kigyűjtése és levelek kiküldése részükre
        User groupLeader = userManager.getGroupLeaderForGroup(ertekeles.getGroup().getId());
        if (groupLeader != null) {
            mailManager.sendEmail(groupLeader.getEmailAddress(), "Módosult értékelés", emailText);
        }
    }

    @Override
    public Valuation getErtekelesWithUzenetek(Long ertekelesId) {
        Query q = em.createNamedQuery(Valuation.findByIdMessageJoined);
        q.setParameter("id", ertekelesId);

        return (Valuation) q.getSingleResult();
    }

    private void add(Valuation ertekeles, ValuationMessage ertekelesUzenet) {
        //em.refresh(valuation);
        ertekeles.getMessages().add(ertekelesUzenet);
        ertekelesUzenet.setValuation(ertekeles);
        ertekeles.setLastModified(new Date());

        em.persist(ertekelesUzenet);
    }

    private void add(Valuation ertekeles, EntrantRequest belepoIgeny) {
        //em.refresh(valuation);
        ertekeles.getEntrantRequests().add(belepoIgeny);
        belepoIgeny.setValuation(ertekeles);
        ertekeles.setLastModified(new Date());

        em.persist(belepoIgeny);
    }

    private void add(Valuation ertekeles, PointRequest pontIgeny) {
        //em.refresh(valuation);
        ertekeles.getPointRequests().add(pontIgeny);
        pontIgeny.setValuation(ertekeles);
        ertekeles.setLastModified(new Date());

        em.persist(pontIgeny);
    }

    @Override
    public List<Valuation> findErtekeles(Group csoport) {
        Query q = em.createNamedQuery(Valuation.findByGroup);
        q.setParameter("group", csoport);

        return q.getResultList();
    }

    @Override
    public List<Valuation> findApprovedValuations(Group group) {
        Query q = em.createQuery("SELECT v FROM Valuation v WHERE v.group=:group "
                + "AND (v.pointStatus = :approved OR v.pointStatus = :none) "
                + "AND (v.entrantStatus = :approved OR v.entrantStatus = :none) "
                + "ORDER BY v.semester DESC");
        q.setParameter("group", group);
        q.setParameter("approved", ValuationStatus.ELFOGADVA);
        q.setParameter("none", ValuationStatus.NINCS);

        return q.getResultList();
    }

    @Override
    public void addNewValuation(Group group, User sender, String valuationText, String principle) {
        Valuation valuation = new Valuation();
        valuation.setSender(sender);
        try {
            valuation.setSemester(systemManager.getSzemeszter());
        } catch (NoSuchAttributeException ex) {
            throw new RuntimeException("Fatális hiba: szemeszter nincs beállítva?!", ex);
        }

        valuation.setGroup(group);
        valuation.setValuationText(valuationText);
        valuation.setPrinciple(principle);

        //TODO group flag alapján van-e joga rá?!

        em.persist(valuation);
    }

    @Override
    public boolean isErtekelesLeadhato(Group csoport) {
        try {
            if (systemManager.getErtekelesIdoszak()
                    != ValuationPeriod.ERTEKELESLEADAS) {

                return false;
            }

            // leadási időszakban akkor adhat le, ha még nem adott le
            Valuation e = findErtekeles(csoport, systemManager.getSzemeszter());
            if (e == null) {
                return true;
            }
        } catch (NoSuchAttributeException ex) {
        }

        return false;
    }

    @Override
    public void ujErtekelesUzenet(Long ertekelesId, User felado, String uzenet) {
        Valuation e = findErtekelesById(ertekelesId);
        ValuationMessage uz = new ValuationMessage();

        uz.setSender(felado);
        uz.setMessage(uzenet);
        uz.setDate(new Date());

        add(e, uz);

        // e-mail küldés a jetinek vagy az adott kör vezetőjének
        String emailTo = null;
        String emailText = null;

        try {
            if (isJETi(felado)) {
                // a JETI a feladó
                System.out.println("JETI a feladó");
                // az értékelt group körvezetőjének a mail címének kikeresése
                User groupLeader = userManager.getGroupLeaderForGroup(e.getGroup().getId());
                if (groupLeader != null) {
                    emailTo = groupLeader.getEmailAddress();
                }
                emailText = "Kedves Körvezető!\n\nA SVIE Választmány a következő üzenetet küldte Neked:\n" + uzenet.toString() + "\n\n\n"
                        + "Az értékeléseidet megtekintheted a https://korok.sch.bme.hu/korok/valuation link alatt.\n"
                        + "Ez egy automatikusan generált e-mail.";
            } else {
                // nem a JETI a feladó
                // jeti körvezetőjének a mail címének kikeresése
                User leader = userManager.getGroupLeaderForGroup(156L);
                if (leader != null) {
                    emailTo = leader.getEmailAddress();
                }
                emailText = "Kedves SVIE Választmány Elnök!\n\nA(z) " + e.getGroup().getName() + " a következő üzenetet küldte az értékelés kapcsán:\n" + uzenet.toString() + "\n\n\n"
                        + "A kör értékelését megtekintheted a https://korok.sch.bme.hu/korok/consider link alatt.\n"
                        + "Ez egy automatikusan generált e-mail.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(emailTo);
        if (emailTo != null) {
            try {
                mailManager.sendEmail(emailTo, "Új üzeneted érkezett", emailText); // emailTo
            } catch (Exception ex) {
                System.out.println("Nem sikerült elküldeni a levelet a következőnek: " + emailTo);
                ex.printStackTrace();
            }
        }
    }

    // megmondja, hogy az adott user JETis-e
    private boolean isJETi(User felhasznalo) {
        List<Group> csoportok = felhasznalo.getGroups();

        for (Group csoport : csoportok) {
            if (csoport.getId() == 156L) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void pontIgenyekLeadasa(Long ertekelesId, List<PointRequest> igenyek) {
        Valuation ertekeles = findErtekelesById(ertekelesId);
        if (ertekeles.getPointStatus().equals(ValuationStatus.ELFOGADVA)) {
            throw new RuntimeException("Elfogadott értékelésen nem változtathat");
        }
        for (PointRequest igeny : igenyek) {
            if (igeny.getPoint() == null) { // ha egy mező üresen marad, az olyan mintha 0 lenne ott!
                igeny.setPoint(0);
            }
            if (igeny.getValuation() == null) { //Új
                if (igeny.getPoint() > 0) {
                    add(ertekeles, igeny);
                }
            } else { //módosítás
                PointRequest ig = em.find(PointRequest.class, igeny.getId());
                if (igeny.getPoint().equals(0)) {
                    // lehet, hogy időközben már törölte helyettünk, akkor nincs gond.
                    if (ig != null) {
                        em.remove(ig);
                    }
                } else {
                    // mi van, ha időközben valaki törölte helyettünk? Akkor hozzuk létre!
                    if (ig != null) {
                        ig.setPoint(igeny.getPoint());
                    } else {
                        // időközben törölték az igényünket, ezért új igényt kell létrehozni
                        // lemásoljuk 1:1-ben az előző igeny-t.
                        PointRequest ujIgeny = new PointRequest(igeny.getUser(), igeny.getPoint());
                        add(ertekeles, ujIgeny);
                    }
                }
            }
        }
        ertekeles.setPointStatus(ValuationStatus.ELBIRALATLAN);
        ertekeles.setLastModified(new Date());
    }

    @Override
    public boolean belepoIgenyekLeadasa(Long ertekelesId, List<EntrantRequest> igenyek) {
        Valuation ertekeles = findErtekelesById(ertekelesId);
        if (ertekeles.getEntrantStatus().equals(ValuationStatus.ELFOGADVA)) {
            throw new RuntimeException("Elfogadott értékelésen nem változtathat");
        }
        for (EntrantRequest igeny : igenyek) {
            if ((igeny.getEntrantType().equals(EntrantType.AB)
                    || igeny.getEntrantType().equals(EntrantType.KB))
                    && (igeny.getValuationText() == null)) {
                return false;
            }
        }
        for (EntrantRequest igeny : igenyek) {
            if (igeny.getValuation() == null) { //új
                add(ertekeles, igeny);
            } else { //módosítás
                EntrantRequest ig = em.find(EntrantRequest.class, igeny.getId());
                ig.setEntrantType(igeny.getEntrantType());
                if (ig.getEntrantType() != EntrantType.KDO) {
                    ig.setValuationText(igeny.getValuationText());
                } else {
                    ig.setValuationText(null);
                }
            }
        }
        ertekeles.setEntrantStatus(ValuationStatus.ELBIRALATLAN);
        ertekeles.setLastModified(new Date());
        return true;
    }

    @Override
    public Valuation findErtekelesById(Long ertekelesId) {
        return em.find(Valuation.class, ertekelesId);
    }

    @Override
    public List<EntrantRequest> findBelepoIgenyekForErtekeles(Long ertekelesId) {
        Query q = em.createQuery("SELECT e FROM EntrantRequest e JOIN FETCH e.user "
                + "JOIN e.valuation WHERE e.valuation.id=:valuationId "
                + "ORDER BY e.user.lastName ASC, e.user.firstName ASC");
        q.setParameter("valuationId", ertekelesId);

        return q.getResultList();
    }

    @Override
    public List<PointRequest> findPontIgenyekForErtekeles(Long ertekelesId) {
        Query q = em.createQuery("SELECT p FROM PointRequest p JOIN FETCH p.user "
                + "JOIN p.valuation "
                + "WHERE p.valuation.id=:valuationId "
                + "ORDER BY p.point DESC, p.user.lastName ASC, p.user.firstName ASC");
        q.setParameter("valuationId", ertekelesId);

        return q.getResultList();
    }

    @Override
    public List<ValuationData> findRequestsForValuation(Long valuationId) {
        Query q = em.createQuery("SELECT v FROM Valuation v "
                + "LEFT JOIN FETCH v.pointRequestsAsSet p "
                + "LEFT JOIN FETCH p.user pu "
                + "LEFT JOIN FETCH v.entrantRequestsAsSet e "
                + "LEFT JOIN FETCH e.user eu "
                + "WHERE v.id = :valuationId ");
        q.setParameter("valuationId", valuationId);

        Valuation v = (Valuation) q.getSingleResult();

        // legjobb esetben ha a size != 0, akkor az összes felhasználónk meglesz
        // és nem kell a map méretén növelni
        int size = v.getEntrantRequestsAsSet().size();
        if (size == 0) {
            // ha nincsen belépő kérelem, akkor csak pontok vannak, és annak a mérete elég.
            size = v.getPointRequestsAsSet().size();
        }

        Map<Long, ValuationData> vDataMap = new HashMap<Long, ValuationData>(size);

        // belépőkkel kezdjük, mert ha van legalább 1 belépő, akkor van összes és így
        // lefedjük az összes felhasználót.
        for (EntrantRequest eReq : v.getEntrantRequestsAsSet()) {
            vDataMap.put(eReq.getUserId(), new ValuationData(eReq.getUser(), null, eReq));
        }

        ValuationData vData;
        for (PointRequest pReq : v.getPointRequestsAsSet()) {
            vData = vDataMap.get(pReq.getUserId());
            if (vData == null) {
                vDataMap.put(pReq.getUserId(), new ValuationData(pReq.getUser(), pReq, null));
            } else {
                vData.setPointRequest(pReq);
            }
        }

        return new ArrayList<ValuationData>(vDataMap.values());
    }

    @Override
    public List<ValuationData> findRequestsForUser(User u, Long groupId) {
        String whereAnd = "";
        if (groupId != null) {
            whereAnd = "AND v.groupId = :groupId ";
        }

        Query q = em.createQuery("SELECT pReq FROM PointRequest pReq "
                + "LEFT JOIN FETCH pReq.user u "
                + "LEFT JOIN FETCH pReq.valuation v "
                + "LEFT JOIN FETCH v.group g "
                + "WHERE pReq.userId = :userId " + whereAnd);
        q.setParameter("userId", u.getId());
        if (groupId != null) {
            q.setParameter("groupId", groupId);
        }
        List<PointRequest> pReqs = (List<PointRequest>) q.getResultList();

        q = em.createQuery("SELECT eReq FROM EntrantRequest eReq "
                + "LEFT JOIN FETCH eReq.user u "
                + "LEFT JOIN FETCH eReq.valuation v "
                + "LEFT JOIN FETCH v.group g "
                + "WHERE eReq.userId = :userId " + whereAnd);
        q.setParameter("userId", u.getId());
        if (groupId != null) {
            q.setParameter("groupId", groupId);
        }
        List<EntrantRequest> eReqs = (List<EntrantRequest>) q.getResultList();

        int size = 10;
        Map<Long, ValuationData> map = new HashMap<Long, ValuationData>(size);

        for (PointRequest p : pReqs) {
            map.put(p.getValuationId(), new ValuationData(p.getUser(), p, null));
        }

        ValuationData vd;
        for (EntrantRequest e : eReqs) {
            vd = map.get(e.getValuationId());
            if (vd == null) {
                map.put(e.getValuationId(), new ValuationData(e.getUser(), null, e));
            } else {
                vd.setEntrantRequest(e);
            }
        }

        return new ArrayList<ValuationData>(map.values());
    }

    @Override
    public List<ApprovedEntrant> findElfogadottBelepoIgenyekForSzemeszter(Semester szemeszter) {
        Query q = em.createQuery("SELECT new hu.sch.domain.ApprovedEntrant(e.user.neptunCode,"
                + "e.entrantType) FROM EntrantRequest e "
                + "WHERE e.valuation.semester = :semester AND e.valuation.entrantStatus=:status");

        q.setParameter("status", ValuationStatus.ELBIRALATLAN);
        q.setParameter("semester", szemeszter);

        return q.getResultList();
    }

    /**
     * A megadott id-hez tartozó értékelést adja vissza úgy, hogy az tartalmazza
     * a pontigényléseket és a belépőigényléseket is.
     * @param valuationId A keresendő értékelés azonosítója.
     * @return A keresett értékelés point -és belépőigényléssel együtt.
     */
    @Override
    public Valuation findValuations(Long valuationId) {
        Query q = em.createQuery("SELECT v FROM Valuation v "
                + "JOIN FETCH v.pointRequests "
                + "JOIN FETCH v.entrantRequests "
                + "WHERE e.id = :id");
        q.setParameter("id", valuationId);
        return (Valuation) q.getSingleResult();
    }

    @Override
    public void updateValuationText(Valuation valuation) {
        Valuation val = em.find(Valuation.class, valuation.getId());
        val.setValuationText(valuation.getValuationText());
        if (val.getPointStatus().equals(ValuationStatus.ELUTASITVA)) {
            val.setPointStatus(ValuationStatus.ELBIRALATLAN);
        }
        if (val.getEntrantStatus().equals(ValuationStatus.ELUTASITVA)) {
            val.setEntrantStatus(ValuationStatus.ELBIRALATLAN);
        }
        val.setLastModified(new Date());
        em.merge(val);
    }

    @Override
    public void updatePrinciple(Valuation valuation) {
        Valuation val = em.find(Valuation.class, valuation.getId());
        val.setPrinciple(valuation.getPrinciple());
        if (val.getPointStatus().equals(ValuationStatus.ELUTASITVA)) {
            val.setPointStatus(ValuationStatus.ELBIRALATLAN);
        }
        if (val.getEntrantStatus().equals(ValuationStatus.ELUTASITVA)) {
            val.setEntrantStatus(ValuationStatus.ELBIRALATLAN);
        }
        val.setLastModified(new Date());
        em.merge(val);
    }

    @Override
    public List<GivenPoint> getPointsForKfbExport(Semester semester) {
        Query q = em.createNamedQuery(GivenPoint.getDormitoryPoints);
        q.setParameter("semester", semester.getId());
        q.setParameter("prevSemester", semester.getPrevious().getId());

        return q.getResultList();
    }
}
