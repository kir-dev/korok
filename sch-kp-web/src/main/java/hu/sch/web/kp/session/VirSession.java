package hu.sch.web.kp.session;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.domain.MembershipType;
import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

/**
 *
 * @author hege
 */
public class VirSession extends WebSession {

    private User user;
    private Group csoport;
    private Map<Long, MembershipType> jogosultsagok;

    public VirSession(Request request) {
        super(request);
    }

    public Group getCsoport() {
        //TODO safe-copy
        return csoport;
    }

    public void setCsoport(Group csoport) {
        //TODO safe-copy
        this.csoport = csoport;
    }

    public User getUser() {
        //TODO safe-copy
        return user;
    }

    public void setUser(User user) {
        //TODO safe-copy
        this.user = user;
    }

    public Map<Long, MembershipType> getJogosultsagok() {
        return new HashMap<Long, MembershipType>(jogosultsagok);
    }

    public void setJogosultsagok(Map<Long, MembershipType> jogosultsagok) {
        this.jogosultsagok = new HashMap<Long, MembershipType>(jogosultsagok);
    }
}
