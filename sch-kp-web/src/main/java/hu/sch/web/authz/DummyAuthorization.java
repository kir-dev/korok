package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.MembershipType;
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
public class DummyAuthorization implements UserAuthorization {

    /**
     * A logoláshoz szükséges logger.
     */
    private static Logger log = Logger.getLogger(DummyAuthorization.class);

    public void init(Application wicketApplication) {
        if (wicketApplication.getConfigurationType().equals(WebApplication.DEPLOYMENT)) {
            throw new IllegalStateException("Do not use dummy authz module in production environment!");
        }
        log.warn("Dummy authorization mode initiated successfully");
    }

    public Long getUserid(Request wicketRequest) {
        return 18925L;
    }

    public boolean hasRoleInGroup(Request wicketRequest, Group group, MembershipType membershipType) {
        // Kir-Dev, Teaház és 17.szint körvezetői tagsága
        if ((group.getId().equals(331L) || group.getId().equals(106L) || group.getId().equals(21L)) &&
                membershipType.equals(MembershipType.KORVEZETO)) {
            return true;
        }
        return false;
    }

    public boolean hasAbstractRole(Request wicketRequest, String role) {
        if (role.equals("ADMIN")) {
            return true;
        } else if (role.equals("JETI")) {
            return true;
        } else {
            return false;
        }

    }

    public boolean hasRoleInSomeGroup(Request wicketRequest, MembershipType membershipType) {
        if (membershipType.equals(MembershipType.KORVEZETO)) {
            return true;
        } else {
            return false;
        }
    }

    public User getUserAttributes(Request wicketRequest) {
        return null;
    }
}
