/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.User;
import hu.sch.domain.logging.Event;
import hu.sch.domain.logging.EventType;
import hu.sch.domain.logging.Log;
import hu.sch.services.MailManagerLocal;
import hu.sch.services.PostManagerLocal;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.TimerServiceLocal;
import hu.sch.util.TimedEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author aldaris
 */
@Stateless(mappedName = "TimerService")
public class TimerServiceBean implements TimerServiceLocal {

    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;
    @EJB(name = "SystemManagerBean")
    SystemManagerLocal systemManager;
    @EJB(name = "PostManagerBean")
    PostManagerLocal postManager;
    @Resource(name = "jdbc/__TimerPool")
    private TimerService timerService;
    @PersistenceContext
    EntityManager em;
    private static Logger log = Logger.getLogger(TimerServiceBean.class);
    private static Event DELETEMEMBERSHIP_EVENT;
    private static Event CREATEMEMBERSHIP_EVENT;

    @PostConstruct
    public void initialize() {
        if (DELETEMEMBERSHIP_EVENT == null) {
            Query q = em.createNamedQuery(Event.getEventForEventType);
            q.setParameter("evt", EventType.TAGSAGTORLES);
            DELETEMEMBERSHIP_EVENT = (Event) q.getSingleResult();
            q.setParameter("evt", EventType.JELENTKEZES);
            CREATEMEMBERSHIP_EVENT = (Event) q.getSingleResult();
        }
    }

    public void scheduleTimers() {
        for (Object timerObj : timerService.getTimers()) {
            Timer timer = (Timer) timerObj;
            if (timer.getInfo() instanceof TimedEvent) {
                timer.cancel();
            }
        }
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.HOUR_OF_DAY) >= 3) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 2);
        cal.clear(Calendar.MINUTE);

        timerService.createTimer(cal.getTime(), TimedEvent.MEMBERSHIP_NOTIFIER.getInterval(), TimedEvent.MEMBERSHIP_NOTIFIER);
//        timerService.createTimer(60000L, 60000L, TimedEvent.MEMBERSHIP_NOTIFIER);
    }

    @Timeout
    @SuppressWarnings("unchecked")
    public void timerFired(Timer timer) {
        System.out.println("event fired");
        if (timer.getInfo() instanceof TimedEvent) {
            TimedEvent evt = (TimedEvent) timer.getInfo();
            switch (evt) {
                case MEMBERSHIP_NOTIFIER:
                    membershipNotifier();
                    break;
            }
        }
        System.out.println("end of event");
    }

    /**
     * A tagságváltoztatásokról értesíti az egyes körök vezetőit. Ha a problémára
     * tudsz egy szebb megoldást, hát hajrá :)
     */
    private void membershipNotifier() {
        Query q = em.createNamedQuery(Log.getFreshEvents);
        q.setParameter("date", systemManager.getLastLogsDate());
        List<Log> entrys = (List<Log>) q.getResultList();

        Long groupId = null;
        List<String> deletedNames = new ArrayList<String>();
        List<String> newNames = new ArrayList<String>();

        for (Log logEntry : entrys) {
            //groupId inicializálása
            if (groupId == null) {
                groupId = logEntry.getGroup().getId();
            }
            //ha a régi groupId nem egyezik meg az új groupId-val, akkor már küldhetjük
            //is a levelet a körvezetőnek.
            if (groupId != logEntry.getGroup().getId()) {
                createEmail(groupId, deletedNames, newNames);
                deletedNames.clear();
                newNames.clear();
                groupId = logEntry.getGroup().getId();
            }
            //az evtType-ok szétválogatása, így két külön listánk lesz az eseményekről
            EventType evtType = logEntry.getEvent().getEventType();
            if (evtType.equals(EventType.JELENTKEZES)) {
                newNames.add(logEntry.getUser().getName());
                System.out.println("new: " + logEntry.getUser().getName());
            } else if (evtType.equals(EventType.TAGSAGTORLES)) {
                deletedNames.add(logEntry.getUser().getName());
                System.out.println("deleted: " + logEntry.getUser().getName());
            }
        }
        if (!deletedNames.isEmpty() || !newNames.isEmpty()) {
            createEmail(groupId, deletedNames, newNames);
        }
    }

    private void createEmail(Long groupId, List<String> deletedNames, List<String> newNames) {
        StringBuilder sb = new StringBuilder(300);
        sb.append("Kedves Körvezető!\n\nAz elmúlt időszakban a következő módosítások ");
        sb.append("történtek a körtagságok terén:\n\n");

        if (!deletedNames.isEmpty()) {
            sb.append("Kilépett körtagok:\n");
            for (String string : deletedNames) {
                sb.append(string).append("\n");
            }
        }
        if (!newNames.isEmpty()) {
            sb.append("\nKörbe jelentkeztek:\n");
            for (String string : newNames) {
                sb.append(string).append("\n");
            }
        }

        sb.append("\nA körtagságokat a https://idp.sch.bme.hu/korok/showgroup/id/").append(groupId);
        sb.append(" oldalon tudod menedzselni.\n\n\nÜdvözlettel:\nKir-Dev");

        System.out.println("Ennek a csoportnak keresem a gazdáját: " + groupId);
        User user = postManager.getGroupLeaderForGroup(groupId);
        if (user != null) {
            mailManager.sendEmail(user.getEmailAddress(), "Körtagságok megváltozása", sb.toString());
        }
    }
}
