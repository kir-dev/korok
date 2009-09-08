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
import hu.sch.services.MailManagerLocal;
import hu.sch.services.PostManagerLocal;
import hu.sch.services.SvieManagerLocal;
import hu.sch.services.UserManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author aldaris
 */
@Stateless
@SuppressWarnings("unchecked")
public class SvieManagerBean implements SvieManagerLocal {

    @EJB(name = "PostManagerBean")
    PostManagerLocal postManager;
    @EJB(name = "MailManagerBean")
    MailManagerLocal mailManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    @PersistenceContext
    EntityManager em;
    private static final String mailSubject = "Elsődleges kört váltottak";

    @Override
    public void updateSvieInfos(List<User> users) {
        for (User user : users) {
            userManager.updateUser(user);
        }
    }

    @Override
    public void updateSvieGroupInfos(List<Group> groups) {
        for (Group group : groups) {
            userManager.updateGroup(group);
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
        mailManager.sendEmail(
                (postManager.getGroupLeaderForGroup(user.getSviePrimaryMembership().getGroup().getId()).getEmailAddress()),
                mailSubject, sb.toString());
    }

    @Override
    public void advocateToOrdinal(User user) {
        user.setSvieStatus(SvieStatus.FELDOLGOZASALATT);
        user.setSvieMembershipType(SvieMembershipType.RENDESTAG);
        em.merge(user);
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
    }

    public void endMembership(User user) {
        if (user.getSviePrimaryMembership() != null) {
            sendPrimaryMembershipChangedMail(user);
        }
        user.setSviePrimaryMembership(null);
        user.setSvieMembershipType(SvieMembershipType.NEMTAG);
        user.setSvieStatus(SvieStatus.NEMTAG);
        em.merge(user);
    }
}
