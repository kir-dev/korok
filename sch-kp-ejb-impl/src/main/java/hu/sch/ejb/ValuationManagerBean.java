/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.EntrantType;
import hu.sch.domain.Group;
import hu.sch.domain.Membership;
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
import hu.sch.domain.MembershipType;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
    private static final String defaultSortColumnForErtekelesLista = "csoportNev";
    private static final Map<String, String> sortMapForErtekelesLista;
    private static final String statisztikaQuery = "SELECT new hu.sch.domain.ValuationStatistic(v, " +
            "(SELECT avg(p.point) FROM PointRequest p WHERE p.valuation = v AND p.point > 0) as averagePoint, " +
            "(SELECT sum(p.point) FROM PointRequest p WHERE p.valuation = v AND p.point > 0) as summaPoint, " +
            "(SELECT count(*) as numKDO FROM EntrantRequest as e WHERE e.valuation = v AND e.entrantType=\'KDO\') as givenKDO, " +
            "(SELECT count(*) as numKB FROM EntrantRequest as e WHERE e.valuation = v AND e.entrantType=\'KB\') as givenKB, " +
            "(SELECT count(*) as numAB FROM EntrantRequest as e WHERE e.valuation = v AND e.entrantType=\'AB\') as givenAB" +
            ") FROM Valuation v ";
    @Resource(name = "mail/korokMail")
    private Session mailSession;
    @PersistenceContext
    EntityManager em;
    @EJB
    UserManagerLocal userManager;
    @EJB
    SystemManagerLocal systemManager;


    static {
        /* Hibernate BUG:
         * http://opensource.atlassian.com/projects/hibernate/browse/HHH-1902
         */
        sortMapForErtekelesLista = new HashMap<String, String>();
        sortMapForErtekelesLista.put("csoportNev", "v.group.name ASC");
        sortMapForErtekelesLista.put("atlagPont", "col_1_0_ DESC");
        sortMapForErtekelesLista.put("kiosztottKDO", "col_2_0_ DESC");
        sortMapForErtekelesLista.put("kiosztottKB", "col_3_0_ DESC");
        sortMapForErtekelesLista.put("kiosztottAB", "col_4_0_ DESC");
        sortMapForErtekelesLista.put("pontStatusz", "v.pointStatus DESC");
        sortMapForErtekelesLista.put("belepoStatusz", "v.entrantStatus DESC");
    }

    public void createErtekeles(Valuation ertekeles) {
        em.persist(ertekeles);
        em.flush();
    }

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

    public List<ValuationStatistic> getStatisztikaForErtekelesek(List<Long> ertekelesId) {
        String ids = Arrays.toString(ertekelesId.toArray());
        ids = ids.substring(1, ids.length() - 1);
        Query q = em.createQuery(statisztikaQuery + "WHERE v.id in (" + ids + ")");

        return q.getResultList();
    }

    public List<ValuationStatistic> findErtekelesStatisztikaForSzemeszter(Semester szemeszter) {
        return findErtekelesStatisztikaForSzemeszter(szemeszter, defaultSortColumnForErtekelesLista);
    }

    public List<ValuationStatistic> findErtekelesStatisztikaForSzemeszter(Semester szemeszter, String sortColumn) {
        String sc = sortMapForErtekelesLista.get(sortColumn);
        if (sc == null) {
            throw new RuntimeException("Az eredményt nem lehet a megadott attribútum alapján rendezni");
        }
        Query q = em.createQuery(statisztikaQuery + "WHERE v.semester=:semester ORDER BY " + sc);
        q.setParameter("semester", systemManager.getSzemeszter());

        return q.getResultList();
    }

    public List<ValuationStatistic> findElbiralatlanErtekelesStatisztika() {
        Query q = em.createQuery(statisztikaQuery + "WHERE v.semester=:semester " +
                "AND (v.pointStatus=:pointStatus OR v.entrantStatus=:entrantStatus)");

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
    public boolean ErtekeleseketElbiral(Collection<ConsideredValuation> elbiralas, User felhasznalo) {
        for (ConsideredValuation ee : elbiralas) {

            if ((ee.getPointStatus().equals(ValuationStatus.ELUTASITVA) ||
                    ee.getEntrantStatus().equals(ValuationStatus.ELUTASITVA)) &&
                    ee.getExplanation() == null) {
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
                Uzen(ee.getValuation().getId(), felhasznalo, ee.getExplanation());
            }
        }
        return true;
    }

    public void Uzen(Long ertekelesId, User uzeno, String uzenetStr) {
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
        String emailText = uzenet.toString() + "\n\n\n" +
                "Az értékeléseidet megtekintheted a https://idp.sch.bme.hu/korok/valuation link alatt.\n" +
                "Ez egy automatikusan generált e-mail.";

        // adott kör körezetőionek kigyűjtése és levelek kiküldése részükre
        User groupLeader = null;
        List<Membership> tagsag = ertekeles.getGroup().getActiveMemberships();
        for (Membership cst : tagsag) {
            if (MembershipType.hasJogInGroup(cst, MembershipType.KORVEZETO)) {
                groupLeader = cst.getUser();
                break;
            }
        }
        if (groupLeader != null) {
            sendEmail(groupLeader.getEmailAddress(), emailText);
        }
    }

    // E-mailt küld
    private void sendEmail(String to, String message) {
        logger.info("E-mail küldése\n" +
                "Címzett: " + to + "\n" +
                "Üzenet: " + message);
        System.out.println("E-mail küldése " + to + "-nak.");
        try {
            Message msg = new MimeMessage(mailSession);

            // teszt címzés
            msg.setRecipients(RecipientType.TO, InternetAddress.parse("halacs@sch.bme.hu", false));
            msg.setRecipients(RecipientType.TO, InternetAddress.parse("majorpetya@sch.bme.hu", false));

            // rendes címzés
            //msg.setRecipients(RecipientType.TO, InternetAddress.parse(to, false));

            msg.setSubject("[VIR KÖRÖK] Új üzeneted érkezett");
            msg.setText(message);
            msg.setSentDate(new Date());
            Transport.send(msg, msg.getRecipients(Message.RecipientType.TO));
            logger.info("Levél sikeresen elküldve.");
        } catch (Exception ex) {
            logger.error("Hiba az e-mail elküldése közben.");
            ex.printStackTrace();
        }

    }

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

    public List<Valuation> findErtekeles(Group csoport) {
        Query q = em.createNamedQuery(Valuation.findByGroup);
        q.setParameter("group", csoport);

        return q.getResultList();
    }

    public List<Valuation> findApprovedValuations(Group group) {
        Query q = em.createQuery("SELECT v FROM Valuation v WHERE v.group=:group " +
                "AND (v.pointStatus = :approved OR v.pointStatus = :none) " +
                "AND (v.entrantStatus = :approved OR v.entrantStatus = :none) " +
                "ORDER BY v.semester DESC");
        q.setParameter("group", group);
        q.setParameter("approved", ValuationStatus.ELFOGADVA);
        q.setParameter("none", ValuationStatus.NINCS);

        return q.getResultList();
    }

    public void ujErtekeles(Group csoport, User felado, String szovegesErtekeles) {
        Valuation e = new Valuation();
        e.setSender(felado);
        try {
            e.setSemester(systemManager.getSzemeszter());
        } catch (NoSuchAttributeException ex) {
            throw new RuntimeException("Fatális hiba: szemeszter nincs beállítva?!", ex);
        }

        e.setGroup(csoport);
        e.setValuationText(szovegesErtekeles);

        //TODO group flag alapján van-e joga rá?!

        em.persist(e);
    }

    public boolean isErtekelesLeadhato(Group csoport) {
        try {
            if (systemManager.getErtekelesIdoszak() !=
                    ValuationPeriod.ERTEKELESLEADAS) {

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
                User groupLeader = null;
                List<Membership> tagsag = e.getGroup().getActiveMemberships();
                for (Membership cst : tagsag) {
                    if (MembershipType.hasJogInGroup(cst, MembershipType.KORVEZETO)) {
                        groupLeader = cst.getUser();
                        break;
                    }
                }
                if (groupLeader != null) {
                    emailTo = groupLeader.getEmailAddress();
                }
                emailText = "Kedves Körvezető!\n\nA JETi a következő üzenetet küldte Neked:\n" + uzenet.toString() + "\n\n\n" +
                        "Az értékeléseidet megtekintheted a https://idp.sch.bme.hu/korok/valuation link alatt.\n" +
                        "Ez egy automatikusan generált e-mail.";
            } else {
                System.out.println("nem JETI");
                // nem a JETI a feladó

                // jeti körvezetőjének a mail címének kikeresése
                emailTo = userManager.findKorvezetoForCsoport(156L).getEmailAddress();
                emailText = "Kedves JETi körvezető!\n\nA(z) " + e.getGroup().getName() + " a következő üzenetet küldte az értékelés kapcsán:\n" + uzenet.toString() + "\n\n\n" +
                        "A kör értékelését megtekintheted a https://idp.sch.bme.hu/korok/consider link alatt.\n" +
                        "Ez egy automatikusan generált e-mail.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(emailTo);
        if (emailTo != null) {
            try {
                sendEmail(emailTo, emailText); // emailTo
            } catch (Exception ex) {
                System.out.println("Nem sikerült elküldeni a levelet a következőnek: " + emailTo);
                ex.printStackTrace();
            }
        }
    }

    // megmondja, hogy az adott user JETis-e
    public boolean isJETi(User felhasznalo) {
        List<Group> csoportok = felhasznalo.getGroups();

        for (Group csoport : csoportok) {
            if (csoport.getId() == 156L) {
                return true;
            }
        }

        return false;
    }

    public void pontIgenyekLeadasa(Long ertekelesId, List<PointRequest> igenyek) {
        Valuation ertekeles = findErtekelesById(ertekelesId);
        if (ertekeles.getPointStatus().equals(ValuationStatus.ELFOGADVA)) {
            throw new RuntimeException("Elfogadott értékelésen nem változtathat");
        }
        for (PointRequest igeny : igenyek) {
            if (igeny.getValuation() == null) { //Új
                if (igeny.getPoint() > 0) {
                    add(ertekeles, igeny);
                }
            } else { //módosítás
                PointRequest ig = em.find(PointRequest.class, igeny.getId());
                if (igeny.getPoint().equals(0)) {
                    em.remove(ig);
                } else {
                    ig.setPoint(igeny.getPoint());
                }
            }
        }
        ertekeles.setPointStatus(ValuationStatus.ELBIRALATLAN);
        ertekeles.setLastModified(new Date());
    }

    public boolean belepoIgenyekLeadasa(Long ertekelesId, List<EntrantRequest> igenyek) {
        Valuation ertekeles = findErtekelesById(ertekelesId);
        if (ertekeles.getEntrantStatus().equals(ValuationStatus.ELFOGADVA)) {
            throw new RuntimeException("Elfogadott értékelésen nem változtathat");
        }
        for (EntrantRequest igeny : igenyek) {
            if ((igeny.getEntrantType().equals(EntrantType.AB) ||
                    igeny.getEntrantType().equals(EntrantType.KB)) &&
                    (igeny.getValuationText() == null)) {
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

    public Valuation findErtekelesById(Long ertekelesId) {
        return em.find(Valuation.class, ertekelesId);
    }

    public List<EntrantRequest> findBelepoIgenyekForErtekeles(Long ertekelesId) {
        Query q = em.createQuery("SELECT e FROM EntrantRequest e JOIN FETCH e.user " +
                "JOIN e.valuation WHERE e.valuation.id=:valuationId " +
                "ORDER BY e.user.lastName ASC, e.user.firstName ASC");
        q.setParameter("valuationId", ertekelesId);

        return q.getResultList();
    }

    public List<PointRequest> findPontIgenyekForErtekeles(Long ertekelesId) {
        Query q = em.createQuery("SELECT p FROM PointRequest p JOIN FETCH p.user " +
                "JOIN p.valuation " +
                "WHERE p.valuation.id=:valuationId " +
                "ORDER BY p.point DESC, p.user.lastName ASC, p.user.firstName ASC");
        q.setParameter("valuationId", ertekelesId);

        return q.getResultList();
    }

    public List<ApprovedEntrant> findElfogadottBelepoIgenyekForSzemeszter(Semester szemeszter) {
        Query q = em.createQuery("SELECT new hu.sch.domain.ApprovedEntrant(e.user.neptunCode," +
                "e.entrantType) FROM EntrantRequest e " +
                "WHERE e.valuation.semester = :semester AND e.valuation.entrantStatus=:status");

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
    public Valuation findValuations(Long valuationId) {
        Query q = em.createQuery("SELECT v FROM Valuation v " +
                "JOIN FETCH v.pointRequests " +
                "JOIN FETCH v.entrantRequests " +
                "WHERE e.id = :id");
        q.setParameter("id", valuationId);
        return (Valuation) q.getSingleResult();
    }
}