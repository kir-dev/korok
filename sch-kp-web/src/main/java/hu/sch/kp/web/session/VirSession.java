package hu.sch.kp.web.session;

import hu.sch.domain.Csoport;
import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.UserManagerLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

/**
 *
 * @author hege
 */
public class VirSession extends WebSession {

    private Felhasznalo user;
    private Csoport csoport;

    private UserManagerLocal getUserManager() {
        try {
            InitialContext ic = new InitialContext();
            return (UserManagerLocal) ic.lookup("java:comp/env/ejb/UserManager");
        } catch (NamingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Unable to get reference to User Manager");
        }
    }

    public VirSession(Request request) {
        super(request);
    /*WebRequest wr = (WebRequest) request;
    HttpServletRequest hsr = wr.getHttpServletRequest();
    String firstname = (String) ((Set) hsr.getAttribute("firstname")).iterator().next();
    String lastname = (String) ((Set) hsr.getAttribute("lastname")).iterator().next();

    UserManagerLocal um = getUserManager();*/

    }

    public Csoport getCsoport() {
        return csoport;
    }

    public void setCsoport(Csoport csoport) {
        this.csoport = csoport;
    }

    public Felhasznalo getUser() {
        return user;
    }

    public void setUser(Felhasznalo user) {
        this.user = user;
    }
}
