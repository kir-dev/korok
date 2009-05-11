package hu.sch.kp.web.authz;

import hu.sch.domain.Csoport;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import org.apache.wicket.Application;
import org.apache.wicket.Request;

/**
 * Ez az interfész felelős a usernév és jogosultságok lekérdezéséért
 * @author hege
 */
public interface UserAuthorization {

    /**
     * Autorizációs mód inicializálása.
     * 
     * @param wicketApplication
     */
    void init(Application wicketApplication);

    /**
     * Az aktuálisan bejelentkezett felhasználó VIRID-ja.
     * 
     * @param wicketRequest
     * @return
     */
    Long getUserid(Request wicketRequest);

    /**
     * Az aktuálisan bejelentkezett felhasználó csoportbeli tagságát vizsgálja.
     * 
     * @param wicketRequest
     * @param csoport
     * @param tagsagTipus
     * @return
     */
    boolean hasRoleInGroup(Request wicketRequest, Csoport csoport, TagsagTipus tagsagTipus);

    /**
     * Az aktuálisan bejelentkezett felhasználó rendelkezik-e valamelyik csoportban
     * az adott jogosultsággal.
     * 
     * @param wicketRequest
     * @param tagsagTipus
     * @return
     */
    boolean hasRoleInSomeGroup(Request wicketRequest, TagsagTipus tagsagTipus);

    /**
     * A felhasználó tagja-e az absztrakt szerepnek.
     * 
     * @param wicketRequest
     * @param role
     * @return
     */
    boolean hasAbstractRole(Request wicketRequest, String role);

    /**
     * Az aktuálisan bejelentkezett felhasználó attribútumait adja vissza.
     *
     * @param wicketRequest
     * @return
     */
    Felhasznalo getUserAttributes(Request wicketRequest);
}
