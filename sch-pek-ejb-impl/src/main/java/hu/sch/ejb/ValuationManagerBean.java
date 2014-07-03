package hu.sch.ejb;

import hu.sch.domain.ConsideredValuation;
import hu.sch.domain.EntrantRequest;
import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.GivenPoint;
import hu.sch.domain.Group;
import hu.sch.domain.PointHistory;
import hu.sch.domain.PointRequest;
import hu.sch.domain.Semester;
import hu.sch.domain.user.User;
import hu.sch.domain.Valuation;
import hu.sch.domain.ValuationData;
import hu.sch.domain.ValuationMessage;
import hu.sch.domain.enums.ValuationPeriod;
import hu.sch.domain.ValuationStatistic;
import hu.sch.domain.enums.ValuationStatus;
import hu.sch.domain.rest.ApprovedEntrant;
import hu.sch.domain.rest.PointInfo;
import hu.sch.domain.util.MapUtils;
import hu.sch.services.GroupManagerLocal;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.services.exceptions.PekException;
import hu.sch.services.exceptions.PekErrorCode;
import hu.sch.services.exceptions.UserNotFoundException;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.services.exceptions.valuation.NoExplanationException;
import hu.sch.services.exceptions.valuation.NothingChangedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hege
 * @author messo
 * @author balo
 */
@Stateless
public class ValuationManagerBean implements ValuationManagerLocal {

    private static final Logger logger = LoggerFactory.getLogger(ValuationManagerBean.class);
    @PersistenceContext
    EntityManager em;
    @Inject
    private UserManagerLocal userManager;
    @Inject
    private SystemManagerLocal systemManager;
    @Inject
    private MailManagerBean mailManager;
    @Inject
    private GroupManagerLocal groupManager;

    @Override
    public void createValuation(Valuation ertekeles) {
        em.persist(ertekeles);
    }

    @Override
    public Valuation findLatestValuation(Group csoport, Semester szemeszter) {
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
    public List<ValuationStatistic> findValuationStatisticForVersions(Group group, Semester semester) {
        Query q = em.createNamedQuery(Valuation.findStatisticBySemesterAndGroup);
        q.setParameter("group", group);
        q.setParameter("semester", semester);

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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public void considerValuations(Collection<ConsideredValuation> elbiralas)
            throws NoExplanationException, NothingChangedException, AlreadyModifiedException {
        for (ConsideredValuation ee : elbiralas) {
            considerValuation(ee);
        }
    }

    @Override
    public void considerValuation(ConsideredValuation cv)
            throws NoExplanationException, NothingChangedException, AlreadyModifiedException {
        // ha valamelyik kérelem elutasításra került, akkor legyen indoklás!
        if ((cv.getPointStatus().equals(ValuationStatus.ELUTASITVA)
                || cv.getEntrantStatus().equals(ValuationStatus.ELUTASITVA))
                && cv.getExplanation() == null) {
            throw new NoExplanationException();
        }

        // ha nem változott semelyik elbírálás, akkor ne akarjunk elbírálni
        if (cv.getPointStatus().equals(cv.getValuation().getPointStatus())
                && cv.getEntrantStatus().equals(cv.getValuation().getEntrantStatus())) {
            throw new NothingChangedException();
        }

        // kérjük le a db-ből a példányt
        Valuation v = em.find(Valuation.class, cv.getValuation().getId());

        if (v.getOptLock() != cv.getValuation().getOptLock()) {
            throw new AlreadyModifiedException();
        }

        v.setExplanation(cv.getExplanation());
        v.setConsideredBy(cv.getUser());
        v.setLastConsidered(new Date());
        // innentől, ha szerkeszt a körvezető, akkor új verzió jön létre
        v.setConsidered(true);

        // mentsük el, az előző állapotokat.
        ValuationStatus prevPS = v.getPointStatus();
        ValuationStatus prevES = v.getEntrantStatus();

        if (cv.getPointStatus() == ValuationStatus.ELFOGADVA
                || cv.getPointStatus() == ValuationStatus.ELUTASITVA) {
            v.setPointStatus(cv.getPointStatus());
        }
        if (cv.getEntrantStatus() == ValuationStatus.ELFOGADVA
                || cv.getEntrantStatus() == ValuationStatus.ELUTASITVA) {
            v.setEntrantStatus(cv.getEntrantStatus());
        }

        try {
            //System.err.println(cv.getExplanation() + ": flush");
            em.flush();
            //System.err.println(cv.getExplanation() + ": sikeres");
        } catch (OptimisticLockException ex) {
            //System.err.println(cv.getExplanation() + ": sikertelen (optlock)");
            throw new AlreadyModifiedException();
        }

        // sikeres az elbírálás, menjen a rendszer által generált üzenet.
        StringBuilder sb = new StringBuilder();
        sb.append("Az értékelés elbírálva:");
        sb.append("\nPontkérelem státusza: ");
        if (prevPS == v.getPointStatus()) {
            sb.append("nem változott");
        } else {
            sb.append(prevPS);
            sb.append(" => ");
            sb.append(v.getPointStatus());
        }
        sb.append("\nBelépőkérelem státusza: ");
        if (prevES == v.getEntrantStatus()) {
            sb.append("nem változott");
        } else {
            sb.append(prevES);
            sb.append(" => ");
            sb.append(v.getEntrantStatus());
        }
        sb.append("\nIndoklás:\n");
        sb.append(v.getExplanation());
        addNewSystemGeneratedMessage(v, sb.toString(), true);
    }

    @Override
    public Valuation updateValuation(Valuation valuation) throws AlreadyModifiedException {
        // lekérjük a db-ből az értékelést.
        Valuation v = em.find(Valuation.class, valuation.getId());

        if (v.getOptLock() != valuation.getOptLock()) {
            throw new AlreadyModifiedException();
        }

        // itt tároljuk arra az értékelésre a referenciát, amit ténylegesen
        // szerkesztünk
        Valuation valuationForUpdate = null;

        if (v.isConsidered()) {
            // ha kezdeni kell egy új verziót:
            Valuation newVersion = createNewVersion(v);
            // az újon akarunk változtatni
            valuationForUpdate = newVersion;
            em.persist(newVersion);
        } else {
            // nem kell új verziót indítani, a régit szerkesztjük.
            valuationForUpdate = v;
        }

        valuationForUpdate.setValuationText(valuation.getValuationText());
        valuationForUpdate.setPrinciple(valuation.getPrinciple());
        valuationForUpdate.setLastModified(new Date());

        try {
            em.flush();
        } catch (OptimisticLockException ex) {
            throw new AlreadyModifiedException();
        }

        addNewSystemGeneratedMessage(v, "A szöveges értékelés és/vagy a pontozási elvek megváltoztak.", false);

        return valuationForUpdate;
    }

    @Override
    public Valuation updatePointRequests(Valuation v, List<PointRequest> igenyek)
            throws AlreadyModifiedException {
        Valuation valuation = findErtekelesById(v.getId());
        if (valuation.getOptLock() != v.getOptLock()) {
            throw new AlreadyModifiedException();
        }
        if (valuation.getPointStatus().equals(ValuationStatus.ELFOGADVA)) {
            throw new RuntimeException("Elfogadott értékelésen nem változtathat");
        }
        if (valuation.isConsidered()) {
            // hozzunk létre új verziót, mostantól azt szerkesztjük.
            valuation = createNewVersion(valuation);
        }
        valuation.setPointStatus(ValuationStatus.ELBIRALATLAN);
        valuation.setLastModified(new Date());

        // nah most jön a magic, gyártunk az igenyekről egy olyan Map-et, ahol
        // a kulcs a felhasználó ID-ja, hogy könnyen vissza kereshessük.
        Map<Long, PointRequest> form = MapUtils.createMapWithUserIdKey(igenyek);

        Iterator<PointRequest> it = valuation.getPointRequestsAsSet().iterator();
        while (it.hasNext()) {
            // eddig ismert értékelés
            PointRequest dbPr = it.next();
            // formban lévő értékelés
            PointRequest formPr = form.get(dbPr.getUserId());
            if (formPr != null) {
                if (formPr.getPoint() == null || formPr.getPoint().equals(0)) {
                    // ha 0-át kapott, akkor töröljük.
                    //System.out.println("Töröljük a DB-ből: " + dbPr.getUser() + " " + dbPr.getPoint());
                    // ha null, vagy 0, akkor töröljük
                    if (em.contains(dbPr)) {
                        em.remove(dbPr);
                    }
                    it.remove();
                } else {
                    // már van ilyen sor, akkor csak állítsuk át a pontot.
                    dbPr.setPoint(formPr.getPoint());
                    //System.out.println("Beállítjuk: " + dbPr.getUser() + " " + dbPr.getPoint());
                }
                // lekezeltük ezt az elemet, töröljük a mapből
                form.remove(dbPr.getUserId());
            } else {
                // nincs is ilyen személy az űrlapban, akkor ő már nincs a körben.
                if (em.contains(dbPr)) {
                    em.remove(dbPr);
                }
                it.remove();
            }
        }

        // a maradék pontok nem szerepelnek a DB-ben, ez azt jelenti, hogy őket
        // el kell menteni!
        for (PointRequest pr : form.values()) {
            if (pr.getPoint() == null || pr.getPoint().equals(0)) {
                continue;
            }
            // lehet, hogy új verzióval dolgozunk, ezért írjuk felül az értékelést.
            pr.setValuation(valuation);
            em.merge(pr);
            //System.out.println("Beszúrjuk: " + pr.getUser() + " " + pr.getPoint());
        }

        try {
            //System.out.println("[FLUSH]");
            em.merge(valuation);
            em.flush();
            //System.out.println("[SUCCESS]]");
        } catch (OptimisticLockException ex) {
            throw new AlreadyModifiedException();
        }

        addNewSystemGeneratedMessage(v, "A pontkérelmek megváltoztak.", false);

        return valuation;
    }

    @Override
    public Valuation updateEntrantRequests(Valuation v, List<EntrantRequest> igenyek)
            throws AlreadyModifiedException, NoExplanationException {
        Valuation valuation = findErtekelesById(v.getId());
        if (valuation.getOptLock() != v.getOptLock()) {
            throw new AlreadyModifiedException();
        }

        if (valuation.getEntrantStatus().equals(ValuationStatus.ELFOGADVA)) {
            throw new RuntimeException("Elfogadott értékelésen nem változtathat");
        }
        // nézzük meg, hogy mindegyik érvényes-e
        for (EntrantRequest igeny : igenyek) {
            if (!igeny.isValid()) {
                throw new NoExplanationException();
            }
        }

        // nézzük meg, hogy kell-e új verzió, ha igen, akkor hozzuk létre
        if (valuation.isConsidered()) {
            // hozzunk létre új verziót, mostantól azt szerkesztjük.
            valuation = createNewVersion(valuation);
        }
        valuation.setEntrantStatus(ValuationStatus.ELBIRALATLAN);
        valuation.setLastModified(new Date());

        Map<Long, EntrantRequest> form = MapUtils.createMapWithUserIdKey(igenyek);

        Iterator<EntrantRequest> it = valuation.getEntrantRequestsAsSet().iterator();
        while (it.hasNext()) {
            // eddig ismert értékelés
            EntrantRequest dbEr = it.next();
            // formban lévő értékelés
            EntrantRequest formEr = form.get(dbEr.getUserId());
            if (formEr != null) {
                dbEr.setEntrantType(formEr.getEntrantType());
                dbEr.setValuationText(dbEr.getEntrantType() == EntrantType.KDO ? null : formEr.getValuationText());
                //System.out.println("Beállítjuk: " + dbEr.getUser() + " " + dbEr.getEntrantType());
                form.remove(dbEr.getUserId());
            } else {
                // nincs is ilyen személy az űrlapban, akkor ő már nincs a körben.
                if (em.contains(dbEr)) {
                    em.remove(dbEr);
                    it.remove();
                }
            }
        }

        // a maradék pontok nem szerepelnek a DB-ben, ez azt jelenti, hogy őket
        // el kell menteni!
        for (EntrantRequest er : form.values()) {
            // lehet, hogy új verzióval dolgozunk, ezért írjuk felül az értékelést.
            er.setValuation(valuation);
            em.merge(er);
            //System.out.println("Beszúrjuk: " + er.getUser() + " " + er.getEntrantType());
        }

        try {
            //System.out.println("[FLUSH]");
            em.merge(valuation);
            em.flush();
            //System.out.println("[SUCCESS]]");
        } catch (OptimisticLockException ex) {
            throw new AlreadyModifiedException();
        }

        addNewSystemGeneratedMessage(v, "A belépőkérelmek megváltoztak.", false);

        return valuation;
    }

    private Valuation createNewVersion(Valuation prev) {
        // új példányt hozzunk létre az előző mintából, nincs semmi frappanság dolog
        Valuation newVersion = prev.copy();

        // az előző verzión, csak annyit változtatunk, hogy beállítjuk a következő verzió mezőt
        // a most létrehozott értékelésre.
        prev.setNextVersion(newVersion);

        // pont- és belépőkérelmeket is le kell másolni.
        prev.copyPointRequests(newVersion);
        prev.copyEntrantRequests(newVersion);

        em.persist(newVersion);
        return newVersion;
    }

    @Override
    public List<ValuationMessage> getMessages(Group group, Semester semester) {
        Query q = em.createNamedQuery(ValuationMessage.listMessages);
        q.setParameter("group", group);
        q.setParameter("semester", semester);

        return q.getResultList();
    }

    @Override
    public void addNewMessage(final ValuationMessage msg) {
        em.persist(msg);

        // e-mail küldés a jetinek vagy az adott kör vezetőjének
        if (isJETi(msg.getSender())
                && systemManager.getErtekelesIdoszak() == ValuationPeriod.ERTEKELESELBIRALAS) {
            // a JETI a feladó
            sendValuationMessageToGroupLeader(msg);
        } else {
            // nem a JETI a feladó
            sendValuationMessageToJeti(msg);
        }
    }

    private void sendValuationMessageToGroupLeader(final ValuationMessage msg) {
        final String subject =
                MailManagerBean.getMailString(MailManagerBean.MAIL_VALUATIONMESSAGE_SUBJECT);

        // az értékelt group körvezetőjének a mail címének kikeresése
        final User groupLeader = groupManager.findLeaderForGroup(msg.getGroupId());
        if (groupLeader != null) {
            final String recipient = groupLeader.getEmailAddress();

            final String emailTemplate =
                    MailManagerBean.getMailString(MailManagerBean.MAIL_VALUATIONMESSAGE_TO_GROUPLEADER_BODY);
            final String emailText = String.format(emailTemplate,
                    msg.getGroup().getName(), msg.getMessage(), systemManager.getValuationLink());

            mailManager.sendEmail(recipient, subject, emailText);
        }
    }

    private void sendValuationMessageToJeti(final ValuationMessage msg) {
        final String subject =
                MailManagerBean.getMailString(MailManagerBean.MAIL_VALUATIONMESSAGE_SUBJECT);

        // jeti körvezetőjének a mail címének kikeresése
        final User leader = groupManager.findLeaderForGroup(Group.JET);
        if (leader != null) {
            final String recipient = leader.getEmailAddress();
            final String emailTemplate =
                    MailManagerBean.getMailString(MailManagerBean.MAIL_VALUATIONMESSAGE_TO_JETI_BODY);

            final String emailText = String.format(emailTemplate,
                    msg.getGroup().getName(), msg.getMessage(), systemManager.getConsiderLink());

            mailManager.sendEmail(recipient, subject, emailText);
        }
    }

    @Override
    public List<Valuation> findLatestValuationsForGroup(Group csoport) {
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
    //TODO?: a sender származtatott érték, biztos, hogy át kell ezt adni, elvégre
    //       csak körvezető adhat le értékelést...
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

        // check if there is an existing valuation already
        if (findLatestValuation(valuation.getGroup(), valuation.getSemester()) != null) {
            logger.warn("Tried to create valuation twice for the same group ({}) and semester ({})", group.getId(), valuation.getSemester());
            throw new PekException(PekErrorCode.ENTITY_DUPLICATE,
                    "Could not create valuation for the same semester twice.");
        }

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
            Valuation e = findLatestValuation(csoport, systemManager.getSzemeszter());
            if (e == null) {
                return true;
            }
        } catch (NoSuchAttributeException ex) {
        }

        return false;
    }

    // megmondja, hogy az adott user JETis-e
    private boolean isJETi(User felhasznalo) {
        List<Group> csoportok = felhasznalo.getGroups();

        for (Group csoport : csoportok) {
            if (csoport.getId() == Group.JET) {
                return true;
            }
        }

        return false;
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
        TypedQuery<PointRequest> pQ = em.createQuery("SELECT p FROM PointRequest p "
                + "LEFT JOIN FETCH p.user pu "
                + "LEFT JOIN FETCH p.valuation v "
                + "WHERE p.valuationId = :valuationId ", PointRequest.class);
        pQ.setParameter("valuationId", valuationId);

        List<PointRequest> pReqs = pQ.getResultList();

        TypedQuery<EntrantRequest> eQ = em.createQuery("SELECT e FROM EntrantRequest e "
                + "LEFT JOIN FETCH e.user pu "
                + "LEFT JOIN FETCH e.valuation v "
                + "WHERE e.valuationId = :valuationId ", EntrantRequest.class);
        eQ.setParameter("valuationId", valuationId);

        List<EntrantRequest> eReqs = eQ.getResultList();

        // legjobb esetben ha a size != 0, akkor az összes felhasználónk meglesz
        // és nem kell a map méretén növelni
        int size = eReqs.size();
        if (size == 0) {
            // ha nincsen belépő kérelem, akkor csak pontok vannak, és annak a mérete elég.
            size = pReqs.size();
        }

        Map<Long, ValuationData> vDataMap = new HashMap<Long, ValuationData>(size);

        // belépőkkel kezdjük, mert ha van legalább 1 belépő, akkor van összes és így
        // lefedjük az összes felhasználót.
        for (EntrantRequest eReq : eReqs) {
            vDataMap.put(eReq.getUserId(), new ValuationData(eReq.getUser(), null, eReq));
        }

        ValuationData vData;
        for (PointRequest pReq : pReqs) {
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
                + "WHERE v.nextVersion IS NULL AND pReq.userId = :userId " + whereAnd);
        q.setParameter("userId", u.getId());
        if (groupId != null) {
            q.setParameter("groupId", groupId);
        }
        List<PointRequest> pReqs = (List<PointRequest>) q.getResultList();

        q = em.createQuery("SELECT eReq FROM EntrantRequest eReq "
                + "LEFT JOIN FETCH eReq.user u "
                + "LEFT JOIN FETCH eReq.valuation v "
                + "LEFT JOIN FETCH v.group g "
                + "WHERE v.nextVersion IS NULL AND eReq.userId = :userId " + whereAnd);
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
    public final String findApprovedEntrantsForExport(final Semester semester,
            final EntrantType entrantType, final int minEntrantNum) {
        EntrantExporter exporter = new EntrantExporter(em, semester, entrantType, minEntrantNum);

        return exporter.toCSV();
    }

    @Override
    public List<GivenPoint> getPointsForKfbExport(final Semester semester) {
        logger.info("KFB export initiated for {}", semester);
        TypedQuery<PointHistory> q = em.createNamedQuery(PointHistory.findBySemester, PointHistory.class);
        q.setParameter("semester", semester);

        List<GivenPoint> result = new LinkedList<>();
        for (PointHistory ph : q.getResultList()) {
            result.add(new GivenPoint(ph.getUser().getNeptunCode(), ph.getPoint()));
        }

        logger.info("KFB export done, generated point for {} users", result.size());
        return result;
    }

    @Override
    public void deleteValuations(Group group, Semester semester) {
        Query q = em.createNamedQuery(Valuation.delete);
        q.setParameter("group", group);
        q.setParameter("semester", semester);
        q.executeUpdate();
    }

    @Override
    public Long findLatestVersionsId(Group group, Semester semester) {
        Query q = em.createNamedQuery(Valuation.findIdBySemesterAndGroup);
        q.setParameter("semester", semester);
        q.setParameter("group", group);

        try {
            return (Long) q.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    /**
     * Creates a {@link ValuationMessage} and saves with the given message. It
     * can send an email notification with the message.
     *
     * @param v valuation
     * @param msg message for valuation
     * @param sendMailToGL send the message in mail
     */
    private void addNewSystemGeneratedMessage(final Valuation v, final String msg,
            final boolean sendMailToGL) {

        final ValuationMessage vm = new ValuationMessage();
        vm.setFromSystem(true);
        vm.setGroup(v.getGroup());
        vm.setSemester(v.getSemester());
        vm.setMessage(msg);
        em.persist(vm);

        final User groupLeader = groupManager.findLeaderForGroup(vm.getGroupId());
        if (sendMailToGL && groupLeader != null) {
            final String emailTemplate =
                    MailManagerBean.getMailString(MailManagerBean.MAIL_VALUATIONMESSAGE_SYSTEM_TO_GROUP_LEADER_BODY);
            final String emailText = String.format(emailTemplate,
                    vm.getGroup().getName(), vm.getMessage(), systemManager.getValuationLink());

            mailManager.sendEmail(groupLeader.getEmailAddress(),
                    MailManagerBean.getMailString(MailManagerBean.MAIL_VALUATIONMESSAGE_SUBJECT),
                    emailText);
        }
    }

    @Override
    public Valuation findValuationForDetails(long valuationId) {
        Query q = em.createNamedQuery(Valuation.findForDetails);
        q.setParameter("id", valuationId);
        try {
            return (Valuation) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    // TODO: review specs for KBME
    @Override
    public List<PointInfo> getPointInfoForUid(String uid, Semester semester) {
        User user = userManager.findUserByScreenName(uid);
        Query q = em.createQuery("SELECT new hu.sch.domain.rest.PointInfo (p.valuation.groupId, p.point) "
                + "FROM PointRequest p "
                + "WHERE p.valuation.semester = :semester AND p.userId = :userid AND "
                + "p.valuation.pointStatus = :status AND "
                + "p.valuation.nextVersion IS NULL");
        q.setParameter("semester", semester);
        q.setParameter("userid", user.getId());
        q.setParameter("status", ValuationStatus.ELFOGADVA);
        return q.getResultList();
    }

    @Override
    public List<ApprovedEntrant> getApprovedEntrants(final String neptun,
            final Semester semester) throws UserNotFoundException {

        final User user = userManager.findUserByNeptun(neptun);

        if (user == null) {
            throw new UserNotFoundException(String.format("User cannot be found with %s neptun.", neptun));
        }

        final List<ApprovedEntrant> results = new LinkedList<>();

        final Query query =
                em.createQuery("SELECT new hu.sch.domain.rest.ApprovedEntrant("
                + "entrantReq.valuation.groupId, entrantReq.valuation.group.name, "
                + "entrantReq.entrantType) "
                + "FROM EntrantRequest entrantReq "
                + "WHERE entrantReq.userId = :virId AND "
                + "entrantReq.valuation.semester = :semester AND "
                + "entrantReq.valuation.entrantStatus = hu.sch.domain.enums.ValuationStatus.ELFOGADVA AND "
                + "entrantReq.valuation.nextVersion = null");
        query.setParameter("semester", semester);
        query.setParameter("virId", user.getId());

        results.addAll(query.getResultList());

        return results;
    }
}
