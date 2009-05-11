package hu.sch.kp.web.authz;

import hu.sch.domain.Csoport;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import org.apache.wicket.Application;
import org.apache.wicket.Request;

/**
 * Fejlesztői teszt autorizációs modul.
 * 
 * @author hege
 */
public class DummyAuthorization implements UserAuthorization {

    public void init(Application wicketApplication) {
        if (wicketApplication.getConfigurationType().equals("PRODUCTION")) {
            throw new IllegalStateException("Do not use dummy authz module in production environment!");
        }
    }

    public Long getUserid(Request wicketRequest) {
        return 18925L;
    }

    public boolean hasRoleInGroup(Request wicketRequest, Csoport csoport, TagsagTipus tagsagTipus) {
        // KIR-DEV és SPOT
        if ((csoport.getId().equals(331L) || csoport.getId().equals(106L) || csoport.getId().equals(21L)) &&
                tagsagTipus.equals(TagsagTipus.KORVEZETO)) {
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

    public boolean hasRoleInSomeGroup(Request wicketRequest, TagsagTipus tagsagTipus) {
        if (tagsagTipus.equals(TagsagTipus.KORVEZETO)) {
            return true;
        } else {
            return false;
        }
    }

    public Felhasznalo getUserAttributes(Request wicketRequest) {
        return null;
    }
}
