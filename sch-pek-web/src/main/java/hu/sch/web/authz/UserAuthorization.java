package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.user.User;
import hu.sch.services.Role;
import org.apache.wicket.Application;
import org.apache.wicket.request.Request;

/**
 * Ez az interfész felelős a usernév és jogosultságok lekérdezéséért
 *
 * @author hege
 */
public interface UserAuthorization {

    /**
     * Autorizációs mód inicializálása. DummyAuthorization használatakor
     * ellenőrzi, hogy az alkalmazás development módban fut-e, ha nem abban van,
     * kivételt dob.
     *
     * @param wicketApplication Az alkalmazás objektumára mutató referencia.
     */
    void init(Application wicketApplication);

    /**
     * Az aktuálisan bejelentkezett felhasználó VIRID-ja.
     *
     * @param wicketRequest A Request objektum, amiből ki tudjuk nyerni a HTTP
     * változókat.
     * @return A távoli felhasználó VIRID-ja.
     */
    Long getUserid(Request wicketRequest);

    /**
     * Az aktuálisan bejelentkezett felhasználó körvezetőségét vizsgálja.
     *
     * @param wicketRequest wicketRequest
     * @param group group
     * @return Körvezető-e az adott csoportban a felhasználó
     */
    boolean isGroupLeaderInGroup(Request wicketRequest, Group group);

    /**
     * Az aktuálisan bejelentkezett felhasználó rendelkezik-e valamelyik
     * csoportban az adott jogosultsággal.
     *
     * @param wicketRequest
     * @return Körvezető-e valamelyik csoportban a felhasználó
     */
    boolean isGroupLeaderInSomeGroup(Request wicketRequest);

    /**
     * A felhasználó rendelkezik-e az adott szerepkörrel (ADMIN|JETI|SVIE)
     *
     * @param wicketRequest wicketRequest
     * @param role role
     * @return Rendelkezik-e a felhasználó az adott szereppel
     */
    boolean hasAbstractRole(Request wicketRequest, Role role);

    /**
     * Az aktuálisan bejelentkezett felhasználó attribútumait adja vissza.
     *
     * @param wicketRequest
     * @return Az Agent által átadott felhasználói attribútumok
     */
    User getUserAttributes(Request wicketRequest);

    /**
     * Az aktuálisan bejelentkezett felhasználó uid-jét adja vissza
     *
     * @param wicketRequest
     * @return A felhasználó egyedi azonosítója
     */
    String getRemoteUser(Request wicketRequest);

    /**
     * True-t ad vissza, ha van bejelentkezett felhasználó.
     *
     * @return
     */
    boolean isLoggedIn(Request wicketRequest);
}
