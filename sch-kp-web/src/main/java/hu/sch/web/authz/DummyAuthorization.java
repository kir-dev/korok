package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebApplication;

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
    private static Logger log = Logger.getLogger(DummyAuthorization.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Application wicketApplication) {
        if (wicketApplication.getConfigurationType().equals(WebApplication.DEPLOYMENT)) {
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
    public boolean isGroupLeaderInGroup(Request wicketRequest, Group group) {
        // Kir-Dev, Teaház és 17.szint körvezetői tagsága
        if ((group.getId().equals(331L) || group.getId().equals(106L) ||
                group.getId().equals(21L) || group.getId().equals(369L))) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAbstractRole(Request wicketRequest, String role) {
        if (role.equals("ADMIN")) {
            return true;
        } else if (role.equals("JETI")) {
            return true;
        } else if (role.equals("SVIE")) {
            return true;
        } else {
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
    public User getUserAttributes(
            Request wicketRequest) {
        return null;
    }
}
