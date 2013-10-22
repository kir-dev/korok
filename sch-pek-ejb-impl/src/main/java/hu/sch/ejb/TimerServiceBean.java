package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.domain.logging.EventType;
import hu.sch.domain.logging.Log;
import hu.sch.services.AccountManager;
import hu.sch.services.GroupManagerLocal;
import hu.sch.services.SystemManagerLocal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
@Stateless(mappedName = "TimerService")
public class TimerServiceBean {

    private static final Logger logger = LoggerFactory.getLogger(TimerServiceBean.class);
    @EJB
    private MailManagerBean mailManager;
    @EJB(name = "SystemManagerBean")
    private SystemManagerLocal systemManager;
    @EJB
    private GroupManagerLocal groupManager;
    @EJB
    private AccountManager accountManager;
    @PersistenceContext
    private EntityManager em;
    private static final String MSG_SUBJECT = MailManagerBean.getMailString(MailManagerBean.MAIL_ADMIN_REPORT_SUBJECT);
    private static final String MSG_BODY_TEMPLATE = MailManagerBean.getMailString(MailManagerBean.MAIL_ADMIN_REPORT_BODY);

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
     * Notifies the group leaders about the membership changes. The query
     * returns log entries ordered by group, then event, then the last name of
     * the user whom membership changed. We iterate through the log entries and
     * group them by groups and events.
     *
     * @param lastUsedLogId
     * @param lastLogId
     */
    private void notifyGroupLeaders(final long lastUsedLogId, final long lastLogId) {
        final EventType[] events = {EventType.JELENTKEZES, EventType.TAGSAGTORLES};

        final TypedQuery<Log> q = em.createNamedQuery(Log.getFreshEventsForEventTypeByGroup,
                Log.class);

        q.setParameter("lastUsedLogId", lastUsedLogId);
        q.setParameter("lastLogId", lastLogId);
        q.setParameter("events", Arrays.asList(events));
        final List<Log> logs = q.getResultList();

        final Map<Group, List<Log>> logsByGroup = collectLogsByGroup(logs);
        for (Group group : logsByGroup.keySet()) {

            final List<Log> entries = logsByGroup.get(group);

            logger.debug("group={}, entries size={}", group.getName(), entries.size());

            if (!entries.isEmpty()) {
                final User leader = groupManager.findLeaderForGroup(group.getId());
                if (leader != null) {
                    final String text = String.format(MSG_BODY_TEMPLATE,
                            String.format("Körvezető (%s)", group.getName()),
                            getReportForEvents(entries));

                    logger.debug("send mail: {}\n{}", leader.getEmailAddress(), text);

                    sendEmail(leader.getEmailAddress(), text);
                }
            }
        }
    }

    /**
     * Iterates through the log entries and group them by groups.
     *
     * @param logs
     * @return
     */
    private Map<Group, List<Log>> collectLogsByGroup(final List<Log> logs) {
        final Map<Group, List<Log>> logsByGroup = new HashMap<>(logs.size());
        for (Log log : logs) {
            final Group group = log.getGroup();
            if (logsByGroup.get(group) == null) {
                logsByGroup.put(group, new LinkedList<Log>());
            }

            logsByGroup.get(group).add(log);
        }

        return logsByGroup;
    }

    /**
     * Generates the body of the mail from the logs. Groups the entries by the
     * type of the logs and display a profile link to the users.
     *
     * @param entries
     * @return
     */
    private String getReportForEvents(final List<Log> entries) {
        final StringBuilder mailBodyForUser = new StringBuilder();
        Log prev = null;
        for (int i = 0; i < entries.size(); i++) {
            final Log current = entries.get(i);

            if (prev == null || !current.getEvent().equals(prev.getEvent())) {
                //display the event summary
                mailBodyForUser.append(current.getEvent().toString());
                prev = current;
            }

            mailBodyForUser.append(current.getUser().getFullName()).append(" -> ");
            mailBodyForUser.append(SystemManagerBean.showUserLink).append(current.getUser().getId()).append("\n");
        }

        return mailBodyForUser.toString();
    }

    /**
     * Notifies the SVIE admins about SVIE state changes of users.
     *
     * @param lastUsedLogId
     * @param lastLogId
     */
    private void notifySvieAdmin(final long lastUsedLogId, final long lastLogId) {
        final EventType[] events = {EventType.PARTOLOVAVALAS,
            EventType.SVIE_JELENTKEZES, EventType.SVIE_TAGSAGTORLES,
            EventType.RENDESTAGGAVALAS};

        final TypedQuery<Log> q = em.createNamedQuery(Log.getFreshEventsForSvie,
                Log.class);

        q.setParameter("lastUsedLogId", lastUsedLogId);
        q.setParameter("lastLogId", lastLogId);
        q.setParameter("events", Arrays.asList(events));
        final List<Log> entries = q.getResultList();

        logger.debug("entries size={}", entries.size());

        if (!entries.isEmpty()) {
            final String text = String.format(MSG_BODY_TEMPLATE,
                    "SVIE Adminisztrátor",
                    getReportForEvents(entries));

            logger.debug("send mail with text={}", text);

            final List<User> svieAdmins = groupManager.findMembersByGroupAndPost(Group.SVIE,
                    "adminisztrátor");
            final List<String> emailAddresses = new LinkedList<>();
            final List<String> names = new LinkedList<>();

            for (User u : svieAdmins) {
                names.add(u.getFullName());
                emailAddresses.add(u.getEmailAddress());
            }

            sendEmail(StringUtils.join(emailAddresses, ","), text);
            logger.info("Mail sent to SVIE administrators ({}).",
                    StringUtils.join(names, ", "));
        }
    }

    /**
     * Notifies the SVIE president about {@link EventType#ELFOGADASALATT} events
     *
     * @param lastUsedLogId
     * @param lastLogId
     */
    private void notifySviePresident(final long lastUsedLogId, final long lastLogId) {
        final TypedQuery<Log> q = em.createNamedQuery(Log.getFreshEventsForSvie, Log.class);
        q.setParameter("lastUsedLogId", lastUsedLogId);
        q.setParameter("lastLogId", lastLogId);
        q.setParameter("events", Arrays.asList(EventType.ELFOGADASALATT));
        final List<Log> entries = q.getResultList();

        logger.debug("entries size={}", entries.size());

        if (!entries.isEmpty()) {
            final String text = String.format(MSG_BODY_TEMPLATE,
                    "Választmányi elnök",
                    getReportForEvents(entries));

            logger.debug("send mail with text={}", text);

            final User sviePresident = groupManager.findLeaderForGroup(Group.VALASZTMANY);
            sendEmail(sviePresident.getEmailAddress(), text);
            logger.info("Mail sent to SVIE president ({}).",
                    sviePresident.getFullName());
        }
    }

    private void sendEmail(String emailTo, String text) {
        mailManager.sendEmail(emailTo, MSG_SUBJECT, text);
    }

    private long getLastLogId() {
        final TypedQuery<Long> q = em.createNamedQuery(Log.getLastId, Long.class);
        q.setMaxResults(1);
        try {
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return 0;
        }
    }
}
