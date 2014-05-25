package hu.sch.ejb;

import hu.sch.domain.Group;
import hu.sch.domain.Membership;
import hu.sch.domain.enums.SvieMembershipType;
import hu.sch.domain.enums.SvieStatus;
import hu.sch.domain.user.User;
import hu.sch.domain.logging.EventType;
import hu.sch.services.GroupManagerLocal;
import hu.sch.services.LogManagerLocal;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.services.SvieManagerLocal;
import hu.sch.services.SystemManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
@Stateless
@SuppressWarnings("unchecked")
public class SvieManagerBean implements SvieManagerLocal {

    private static Logger logger = LoggerFactory.getLogger(SvieManagerBean.class);
    @EJB
    private GroupManagerLocal groupManager;
    @EJB
    private MembershipManagerLocal membershipManager;
    @EJB
    private MailManagerBean mailManager;
    @EJB(name = "LogManagerBean")
    private LogManagerLocal logManager;
    @EJB
    private SystemManagerLocal systemManager;
    @PersistenceContext
    private EntityManager em;

    @Override
    public void updateSvieInfos(List<User> users) {
        for (User user : users) {
            User temp = em.find(User.class, user.getId());
            if (!temp.getSvieStatus().equals(SvieStatus.ELFOGADASALATT)
                    && user.getSvieStatus().equals(SvieStatus.ELFOGADASALATT)) {
                logManager.createLogEntry(null, user, EventType.ELFOGADASALATT);
            }
            em.merge(user);
        }
    }

    @Override
    public void applyToSvie(User user, SvieMembershipType msType) {
        user.setSvieMembershipType(msType);
        user.setSvieStatus(SvieStatus.FELDOLGOZASALATT);
        em.merge(user);
        logManager.createLogEntry(null, user, EventType.SVIE_JELENTKEZES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSvieGroupInfos(List<Group> groups) {
        for (Group group : groups) {
            Group temp = em.find(Group.class, group.getId());
            temp.setDelegateNumber(group.getDelegateNumber());
            temp.setIsSvie(group.getIsSvie());
            em.merge(temp);

            User user = groupManager.findLeaderForGroup(group.getId());
            if (group.getIsSvie() && user != null && user.getSvieMembershipType().equals(SvieMembershipType.NEMTAG)) {
                user.setSvieMembershipType(SvieMembershipType.RENDESTAG);
                user.setSvieStatus(SvieStatus.ELFOGADVA);
                user.setSviePrimaryMembership(membershipManager.findMembership(group.getId(), user.getId()));
                em.merge(user);
            }
        }
    }

    @Override
    public List<Membership> getActiveSvieMembershipsForUser(User user) {
        Query q = em.createNamedQuery(Membership.getActiveSvieMemberships);
        q.setParameter("user", user);
        return q.getResultList();
    }

    @Override
    public List<User> getSvieMembers() {
        Query q = em.createNamedQuery(Membership.getMembersWithSvieMembershipTypeNotEqual);
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

    private void sendPrimaryMembershipChangedMail(final User user) {
        final String profileLink = systemManager.getShowUserLink() + user.getId();
        final String subject =
                MailManagerBean.getMailString(MailManagerBean.MAIL_PRIMARYMEMBERSHIP_CHANGED_SUBJECT);

        final String msgTemplate =
                MailManagerBean.getMailString(MailManagerBean.MAIL_PRIMARYMEMBERSHIP_CHANGED_BODY);

        final User groupLeader =
                groupManager.findLeaderForGroup(user.getSviePrimaryMembership().getGroup().getId());

        if (groupLeader != null) {
            mailManager.sendEmail(groupLeader.getEmailAddress(), subject,
                    String.format(msgTemplate, user.getFullName(), profileLink));
        }
    }

    @Override
    public void advocateToOrdinal(User user) {
        user.setSvieStatus(SvieStatus.FELDOLGOZASALATT);
        user.setSvieMembershipType(SvieMembershipType.RENDESTAG);
        em.merge(user);
        logManager.createLogEntry(null, user, EventType.RENDESTAGGAVALAS);
    }

    @Override
    public void ordinalToAdvocate(User user) {
        if (user.getSviePrimaryMembership() != null) {
            sendPrimaryMembershipChangedMail(user);
        }
        user.setSviePrimaryMembership(null);
        user.setSvieMembershipType(SvieMembershipType.PARTOLOTAG);
        user.setSvieStatus(SvieStatus.ELFOGADVA);
        em.merge(user);
        logManager.createLogEntry(null, user, EventType.PARTOLOVAVALAS);

    }

    @Override
    public void endMembership(User user) {
        if (user.getSviePrimaryMembership() != null) {
            sendPrimaryMembershipChangedMail(user);
        }
        user.setSviePrimaryMembership(null);
        user.setSvieMembershipType(SvieMembershipType.NEMTAG);
        user.setSvieStatus(SvieStatus.NEMTAG);
        user.setDelegated(false);
        em.merge(user);
        logManager.createLogEntry(null, user, EventType.SVIE_TAGSAGTORLES);
    }

    public List<User> getDelegatedUsersForGroup(Long groupId) {
        Query q = em.createNamedQuery(Membership.getDelegatedMemberForGroup);
        q.setParameter("groupId", groupId);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getDelegatedUsers() {
        Query q = em.createNamedQuery(Membership.getAllDelegated);
        return q.getResultList();
    }
}
