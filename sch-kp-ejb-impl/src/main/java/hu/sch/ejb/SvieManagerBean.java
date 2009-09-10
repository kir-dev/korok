/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import hu.sch.domain.logging.Event;
import hu.sch.domain.logging.EventType;
import hu.sch.services.LogManagerLocal;
import hu.sch.services.MailManagerLocal;
import hu.sch.services.SvieManagerLocal;
import hu.sch.services.UserManagerLocal;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author aldaris
 */
@Stateless
@SuppressWarnings("unchecked")
public class SvieManagerBean implements SvieManagerLocal {

    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    @EJB(name = "LogManagerBean")
    LogManagerLocal logManager;
    @PersistenceContext
    EntityManager em;
    private static Logger log = Logger.getLogger(SvieManagerBean.class);
    private static final String mailSubject = "Elsődleges kört váltottak";
    private static Event ADVOCATE_EVENT;
    private static Event ORDINAL_EVENT;
    private static Event APPLY_EVENT;
    private static Event RESIGN_EVENT;
    private static Event INPROGRESS_EVENT;

    @PostConstruct
    public void initialize() {
        if (ADVOCATE_EVENT == null) {
            Query q = em.createNamedQuery(Event.getEventForEventType);
            q.setParameter("evt", EventType.PARTOLOVAVALAS);
            ADVOCATE_EVENT = (Event) q.getSingleResult();
            q.setParameter("evt", EventType.RENDESTAGGAVALAS);
            ORDINAL_EVENT = (Event) q.getSingleResult();
            q.setParameter("evt", EventType.SVIE_JELENTKEZES);
            APPLY_EVENT = (Event) q.getSingleResult();
            q.setParameter("evt", EventType.SVIE_TAGSAGTORLES);
            RESIGN_EVENT = (Event) q.getSingleResult();
            q.setParameter("evt", EventType.ELFOGADASALATT);
            INPROGRESS_EVENT = (Event) q.getSingleResult();
        }
    }

    @Override
    public void updateSvieInfos(List<User> users) {
        for (User user : users) {
            User temp = em.find(User.class, user.getId());
            if (!temp.getSvieStatus().equals(SvieStatus.ELFOGADASALATT) &&
                    user.getSvieStatus().equals(SvieStatus.ELFOGADASALATT)) {
                logManager.createLogEntry(null, user, INPROGRESS_EVENT);
            }
            em.merge(user);
        }
    }

    @Override
    public void applyToSvie(User user, SvieMembershipType msType) {
        user.setSvieMembershipType(msType);
        user.setSvieStatus(SvieStatus.FELDOLGOZASALATT);
        em.merge(user);
        logManager.createLogEntry(null, user, APPLY_EVENT);
    }

    @Override
    public void updateSvieGroupInfos(List<Group> groups) {
        for (Group group : groups) {
            Group temp = em.find(Group.class, group.getId());
            temp.setDelegateNumber(group.getDelegateNumber());
            temp.setIsSvie(group.getIsSvie());
        }
    }

    @Override
    public List<Membership> getSvieMembershipsForUser(User user) {
        Query q = em.createQuery("SELECT ms FROM Membership ms WHERE ms.user = :user AND ms.group.isSvie = true");
        q.setParameter("user", user);
        return q.getResultList();
    }

    @Override
    public List<User> getSvieMembers() {
        Query q = em.createQuery("SELECT u FROM User u WHERE u.svieMembershipType <> :msType");
        q.setParameter("msType", SvieMembershipType.NEMTAG);
        return q.getResultList();
    }

    @Override
    public void updatePrimaryMembership(User user) {
        User temp = em.find(User.class, user.getId());
        Membership oMs = temp.getSviePrimaryMembership();
        if (oMs != null && !oMs.equals(user.getSviePrimaryMembership())) {
            sendPrimaryMembershipChangedMail(user);
            temp.setDelegated(false);
        }
        temp.setSviePrimaryMembership(user.getSviePrimaryMembership());
    }

    private void sendPrimaryMembershipChangedMail(User user) {
        StringBuilder sb = new StringBuilder(200);
        sb.append("Kedves Körvezető!\n\nAz egyik körtagod, ");
        sb.append(user.getName());
        sb.append(" az előbb változtatta meg elsődleges körét.\n");
        sb.append("Link a felhasználó profiljára:\n");
        sb.append("https://idp.sch.bme.hu/korok/showuser/id/").append(user.getId());
        sb.append("\n\nÜdvözlettel:\nKir-Dev");
        log.info("Erről a csoportról van szó: " + user.getSviePrimaryMembership().getGroup().getName());
        mailManager.sendEmail(
                (userManager.getGroupLeaderForGroup(user.getSviePrimaryMembership().getGroup().getId()).getEmailAddress()),
                mailSubject, sb.toString());
    }

    @Override
    public void advocateToOrdinal(User user) {
        user.setSvieStatus(SvieStatus.FELDOLGOZASALATT);
        user.setSvieMembershipType(SvieMembershipType.RENDESTAG);
        em.merge(user);
        logManager.createLogEntry(null, user, ORDINAL_EVENT);
    }

    @Override
    public void OrdinalToAdvocate(User user) {
        if (user.getSviePrimaryMembership() != null) {
            sendPrimaryMembershipChangedMail(user);
        }
        user.setSviePrimaryMembership(null);
        user.setSvieMembershipType(SvieMembershipType.PARTOLOTAG);
        user.setSvieStatus(SvieStatus.ELFOGADVA);
        em.merge(user);
        logManager.createLogEntry(null, user, ADVOCATE_EVENT);

    }

    @Override
    public void endMembership(User user) {
        if (user.getSviePrimaryMembership() != null) {
            sendPrimaryMembershipChangedMail(user);
        }
        user.setSviePrimaryMembership(null);
        user.setSvieMembershipType(SvieMembershipType.NEMTAG);
        user.setSvieStatus(SvieStatus.NEMTAG);
        em.merge(user);
        logManager.createLogEntry(null, user, RESIGN_EVENT);
    }

    public List<User> getDelegatedUsersForGroup(Long groupId) {
        Query q = em.createQuery("SELECT ms.user FROM Membership ms " +
                "WHERE ms.group.id=:groupId AND ms.user.sviePrimaryMembership = ms AND ms.user.delegated = true");
        q.setParameter("groupId", groupId);
        return q.getResultList();
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public List<User> getDelegatedUsers() {
        Query q = em.createQuery("SELECT u FROM User u WHERE u.delegated = true " +
                "ORDER BY u.lastName, u.firstName");
        return q.getResultList();
    }
}
