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

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.logging.EventType;
import hu.sch.domain.logging.Log;
import hu.sch.services.MailManagerLocal;
import hu.sch.services.SystemManagerLocal;
import hu.sch.services.UserManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author aldaris
 */
@Stateless(mappedName = "TimerService")
@SuppressWarnings("unchecked")
public class TimerServiceBean {

    @EJB(name = "MailManagerBean")
    private MailManagerLocal mailManager;
    @EJB(name = "SystemManagerBean")
    private SystemManagerLocal systemManager;
    @EJB(name = "UserManagerBean")
    private UserManagerLocal userManager;
    @PersistenceContext
    EntityManager em;
    private static Logger logger = Logger.getLogger(TimerServiceBean.class);
    private static String welcome = "Kedves %s!\n\nAz elmúlt időszakban a következő módosítások "
            + "történtek a körtagságok terén:\n\n";
    private static final String showUserLink = "https://korok.sch.bme.hu/korok/showuser/id/";
    private static final Long VALASZTMANY_ID = 370L;

    @Schedule(hour = "1")
    protected void dailyEvent() {
        logger.info("daily event fired");
        // lekérjük, hogy melyik ID volt az, amit legutoljára kiküldtünk
        long lastUsedLogId = systemManager.getLastLogId();
        // lekérjük a legfrisebb ID-t, ez és az előbbi közti logokat dolgozzuk fel
        long lastLogId = getLastLogId();
        if (lastUsedLogId == lastLogId) {
            logger.info("Nincs új log, amit feldolgozhatnánk");
        } else {
            logger.info("A következő logokat dolgozzuk fel: (" + lastUsedLogId + ", " + lastLogId + "]");
            notifyGroupLeaders(lastUsedLogId, lastLogId);
            notifySvieAdmin(lastUsedLogId, lastLogId);
            notifySviePresident(lastUsedLogId, lastLogId);
            // elmentjük, hogy legközelebb ezeket a logokat már ne dolgozzuk fel
            systemManager.setLastLogId(lastLogId);
        }
        logger.info("end of daily event");
    }

    /**
     * A tagságváltoztatásokról értesíti az egyes körök vezetőit. Ha a problémára
     * tudsz egy szebb megoldást, hát hajrá :)
     */
    private void notifyGroupLeaders(long lastUsedLogId, long lastLogId) {
        Query q = em.createNamedQuery(Log.getGroupsForFreshEntries);
        q.setParameter("lastUsedLogId", lastUsedLogId);
        q.setParameter("lastLogId", lastLogId);
        List<Group> groups = q.getResultList();
        EventType[] events = {EventType.JELENTKEZES, EventType.TAGSAGTORLES};

        Query q2 = em.createNamedQuery(Log.getFreshEventsForEventTypeByGroup);
        q2.setParameter("lastUsedLogId", lastUsedLogId);
        q2.setParameter("lastLogId", lastLogId);
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
                    sb.append(showUserLink).append(log.getUser().getId()).append("\n");
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

    private void notifySvieAdmin(long lastUsedLogId, long lastLogId) {
        Query q = em.createNamedQuery(Log.getFreshEventsForSvie);
        StringBuilder sb = new StringBuilder(welcome.replace("%s", "SVIE Adminisztrátor"));
        q.setParameter("lastUsedLogId", lastUsedLogId);
        q.setParameter("lastLogId", lastLogId);
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
                sb.append(showUserLink).append(log.getUser().getId()).append("\n");
            }
            if (!logs.isEmpty()) {
                sb.append("\n\n");
            }
        }
        if (wasRecord) {
            sendEmail("katalin@sch.bme.hu", sb);
        }
    }

    private void notifySviePresident(long lastUsedLogId, long lastLogId) {
        Query q = em.createNamedQuery(Log.getFreshEventsForSvie);
        StringBuilder sb = new StringBuilder(welcome.replace("%s", "Választmányi elnök"));
        q.setParameter("lastUsedLogId", lastUsedLogId);
        q.setParameter("lastLogId", lastLogId);
        q.setParameter("evtType", EventType.ELFOGADASALATT);
        List<Log> logs = q.getResultList();
        if (!logs.isEmpty()) {
            sb.append(EventType.ELFOGADASALATT.toString());
        }
        for (Log log : logs) {
            sb.append(log.getUser().getName()).append(" -> ");
            sb.append(showUserLink).append(log.getUser().getId()).append("\n");
        }
        if (!logs.isEmpty()) {
            sb.append("\n\n");
            sendEmail(userManager.getGroupLeaderForGroup(VALASZTMANY_ID).getEmailAddress(), sb);
        }
    }

    private void sendEmail(String emailTo, StringBuilder sb) {
        sb.append("Üdvözlettel:\nKir-Dev");

        mailManager.sendEmail(emailTo, "Események", sb.toString());
    }

    private long getLastLogId() {
        Query q = em.createNamedQuery(Log.getLastId);
        q.setMaxResults(1);
        try {
            return (Long) q.getSingleResult();
        } catch (NoResultException ex) {
            return 0;
        }
    }
}