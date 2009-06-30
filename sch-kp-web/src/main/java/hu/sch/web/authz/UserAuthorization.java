package hu.sch.web.authz;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.MembershipType;
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
    boolean hasRoleInGroup(Request wicketRequest, Group csoport, MembershipType tagsagTipus);

    /**
     * Az aktuálisan bejelentkezett felhasználó rendelkezik-e valamelyik csoportban
     * az adott jogosultsággal.
     * 
     * @param wicketRequest
     * @param tagsagTipus
     * @return
     */
    boolean hasRoleInSomeGroup(Request wicketRequest, MembershipType tagsagTipus);

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
    User getUserAttributes(Request wicketRequest);
}
