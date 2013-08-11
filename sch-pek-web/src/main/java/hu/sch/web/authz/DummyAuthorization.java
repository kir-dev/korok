package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fejlesztői teszt autorizációs modul. Lényegében meghazudja nekünk, hogy mely
 * körökben vagyunk körvezetők.
 *
 * @author hege
 */
public final class DummyAuthorization implements UserAuthorization {

    /**
     * A logoláshoz szükséges logger.
     */
    private static Logger log = LoggerFactory.getLogger(DummyAuthorization.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Application wicketApplication) {
        if (wicketApplication.getConfigurationType() == RuntimeConfigurationType.DEPLOYMENT) {
            throw new IllegalStateException("Do not use dummy authz module in production environment!");
        }
        log.warn("Dummy authorization mode initiated successfully");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getUserid(Request wicketRequest) {
        return 18925L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemoteUser(Request wicketRequest) {
        return "konvergal";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroupLeaderInGroup(Request wicketRequest, Group group) {
        // Kir-Dev, Teaház és 17.szint körvezetői tagsága
        if ((group.getId().equals(331L) || group.getId().equals(106L)
                || group.getId().equals(21L) || group.getId().equals(Group.SVIE)) || group.getId().equals(26L)) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAbstractRole(Request wicketRequest, String role) {
        switch (role) {
            case "ADMIN":
                return true;
            case "JETI":
                return true;
            case "SVIE":
                return true;
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGroupLeaderInSomeGroup(Request wicketRequest) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserAttributes(Request wicketRequest) {
        return null;
    }
}
