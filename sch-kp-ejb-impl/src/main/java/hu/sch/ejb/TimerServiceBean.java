/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.logging.EventType;
import hu.sch.domain.logging.Log;
import hu.sch.services.MailManagerLocal;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.TimerServiceLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.util.TimedEvent;
import java.util.Calendar;
import java.util.List;
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
@SuppressWarnings("unchecked")
public class TimerServiceBean implements TimerServiceLocal {

    @EJB(name = "MailManagerBean")
    private MailManagerLocal mailManager;
    @EJB(name = "SystemManagerBean")
    private SystemManagerLocal systemManager;
    @EJB(name = "UserManagerBean")
    private UserManagerLocal userManager;
    @Resource(name = "jdbc/__TimerPool")
    private TimerService timerService;
    @PersistenceContext
    EntityManager em;
    private static Logger logger = Logger.getLogger(TimerServiceBean.class);
    private static String welcome = "Kedves %s!\n\nAz elmúlt időszakban a következő módosítások " +
            "történtek a körtagságok terén:\n\n";
    private static final String showUserLink = "https://idp.sch.bme.hu/korok/showuser/id/";

    public void scheduleTimers() {
        for (Object timerObj : timerService.getTimers()) {
            Timer timer = (Timer) timerObj;
            if (timer.getInfo() instanceof TimedEvent) {
                timer.cancel();
            }
        }
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.HOUR_OF_DAY) >= 1) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.clear(Calendar.MINUTE);

//        timerService.createTimer(cal.getTime(), TimedEvent.DAILY_EVENT.getInterval(), TimedEvent.DAILY_EVENT);
        timerService.createTimer(60000L, 60000L, TimedEvent.DAILY_EVENT);
    }

    @Timeout
    public void timerFired(Timer timer) {
        logger.info("event fired");
        if (timer.getInfo() instanceof TimedEvent) {
            TimedEvent evt = (TimedEvent) timer.getInfo();
            switch (evt) {
                case DAILY_EVENT: {
                    notifyGroupLeaders();
                    notifySvieAdmin();
                    notifySviePresident();
                    systemManager.setLastLogsDate();
                    break;
                }
            }
        }
        logger.info("end of event");
    }

    /**
     * A tagságváltoztatásokról értesíti az egyes körök vezetőit. Ha a problémára
     * tudsz egy szebb megoldást, hát hajrá :)
     */
    private void notifyGroupLeaders() {
        Query q = em.createNamedQuery(Log.getGroupsForFreshEntries);
        q.setParameter("date", systemManager.getLastLogsDate());
        List<Group> groups = q.getResultList();
        EventType[] events = {EventType.JELENTKEZES, EventType.TAGSAGTORLES};

        Query q2 = em.createNamedQuery(Log.getFreshEventsForEventTypeByGroup);
        q2.setParameter("date", systemManager.getLastLogsDate());
        for (Group group : groups) {
            StringBuilder sb = new StringBuilder(welcome.replace("%s", "Körvezető"));
            q2.setParameter("group", group);
            for (EventType evtType : events) {
                q2.setParameter("evtType", evtType);
                List<Log> logs = q2.getResultList();

                if (!logs.isEmpty()) {
                    sb.append(evtType.toString());
                }
                for (Log log : logs) {
                    sb.append(log.getUser().getName()).append(" -> ");
                    sb.append(showUserLink + log.getUser().getId()).append("\n");
                }
                if (!logs.isEmpty()) {
                    sb.append("\n\n");
                }
            }
            User user = userManager.getGroupLeaderForGroup(group.getId());
            if (user != null) {
                sendEmail(user.getEmailAddress(), sb);
            }
        }
    }

    private void notifySvieAdmin() {
        Query q = em.createNamedQuery(Log.getFreshEventsForSvie);
        StringBuilder sb = new StringBuilder(welcome.replace("%s", "SVIE Adminisztrátor"));
        q.setParameter("date", systemManager.getLastLogsDate());
        boolean wasRecord = false;
        EventType[] events = {EventType.PARTOLOVAVALAS,
            EventType.SVIE_JELENTKEZES, EventType.SVIE_TAGSAGTORLES,
            EventType.RENDESTAGGAVALAS};
        for (EventType eventType : events) {
            q.setParameter("evtType", eventType);
            List<Log> logs = q.getResultList();
            if (!logs.isEmpty()) {
                wasRecord = true;
                sb.append(eventType.toString());
            }
            for (Log log : logs) {
                sb.append(log.getUser().getName()).append(" -> ");
                sb.append(showUserLink + log.getUser().getId()).append("\n");
            }
            if (!logs.isEmpty()) {
                sb.append("\n\n");
            }
        }
        if (wasRecord) {
            sendEmail("sviadmin@svieadmin.hu", sb);
        }
    }

    private void notifySviePresident() {
        Query q = em.createNamedQuery(Log.getFreshEventsForSvie);
        StringBuilder sb = new StringBuilder(welcome.replace("%s", "Választmányi elnök"));
        q.setParameter("date", systemManager.getLastLogsDate());
        q.setParameter("evtType", EventType.ELFOGADASALATT);
        List<Log> logs = q.getResultList();
        if (!logs.isEmpty()) {
            sb.append(EventType.ELFOGADASALATT.toString());
        }
        for (Log log : logs) {
            sb.append(log.getUser().getName()).append(" -> ");
            sb.append(showUserLink + log.getUser().getId()).append("\n");
        }
        if (!logs.isEmpty()) {
            sb.append("\n\n");
            sendEmail("valasztmany@valasztmany.hu", sb);
        }
    }

    private void sendEmail(String emailTo, StringBuilder sb) {
        sb.append("Üdvözlettel:\nKir-Dev");

        mailManager.sendEmail(emailTo, "Események", sb.toString());
    }
}
