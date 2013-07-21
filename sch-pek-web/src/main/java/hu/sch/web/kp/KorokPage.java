package hu.sch.web.kp;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.user.User;
import hu.sch.domain.ValuationPeriod;
import hu.sch.services.GroupManagerLocal;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.services.PostManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.services.SystemManagerLocal;
import hu.sch.web.common.PekPage;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hege
 */
public abstract class KorokPage extends PekPage {

    @EJB(name = "SystemManagerBean")
    protected SystemManagerLocal systemManager;
    @EJB(name = "PostManagerBean")
    protected PostManagerLocal postManager;
    @EJB(name="GroupManagerBean")
    protected GroupManagerLocal groupManager;
    @EJB(name="MemebershipManagerBean")
    protected MembershipManagerLocal membershipManager;

    private static final Logger log = LoggerFactory.getLogger(KorokPage.class);

    @Override
    protected String getTitle() {
        return "VIR Körök";
    }

    @Override
    protected String getCss() {
        return "korok-style.css";
    }

    @Override
    protected String getFavicon() {
        return "favicon-korok.ico";
    }

    @Override
    protected Panel getHeaderPanel(String id) {
        return new KorokHeaderPanel(id, isUserGroupLeaderInSomeGroup(),
                isCurrentUserJETI() && systemManager.getErtekelesIdoszak() == ValuationPeriod.ERTEKELESELBIRALAS,
                isCurrentUserJETI() || isCurrentUserSVIE() || isCurrentUserAdmin());
    }

    protected final Semester getSemester() {
        Semester sz = null;
        try {
            sz = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException ex) {
            log.warn("Attribute for semester isn't set in the database.", ex);
        }
        return sz;
    }

    protected final boolean isUserGroupLeader(Group group) {
        return getAuthorizationComponent().isGroupLeaderInGroup(getRequest(), group);
    }

    protected final boolean isUserGroupLeaderInSomeGroup() {
        return getAuthorizationComponent().isGroupLeaderInSomeGroup(getRequest());
    }

    /**
     * A beloginolt felhasználót ellenőrizzük, hogy van-e delegált posztja az adott csoportban
     *
     * @param group     melyik csoportban vizsgálódunk
     * @return          Van-e delegált posztja a csoportban?
     */
    protected final boolean hasUserDelegatedPostInGroup(Group group) {
        User user = getUser();
        if (user == null) {
            return false;
        }
        return postManager.hasUserDelegatedPostInGroup(group, user);
    }

    /**
     * Az adott felhasználót ellenőrizzük, hogy van-e delegált posztja az adott csoportban
     *
     * @param user      kérdéses felhasználó
     * @param group     melyik csoportban vizsgálódunk
     * @return          Van-e delegált posztja a csoportban?
     */
    protected final boolean hasUserDelegatedPostInGroup(User user, Group group) {
        if (user == null) {
            return false;
        }
        return postManager.hasUserDelegatedPostInGroup(group, user);
    }
}
