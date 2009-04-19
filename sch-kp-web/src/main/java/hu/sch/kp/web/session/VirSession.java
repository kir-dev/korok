package hu.sch.kp.web.session;

import hu.sch.domain.Csoport;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

/**
 *
 * @author hege
 */
public class VirSession extends WebSession {

    private Felhasznalo user;
    private Csoport csoport;
    private Map<Long, TagsagTipus> jogosultsagok;

    public VirSession(Request request) {
        super(request);
    }

    public Csoport getCsoport() {
        //TODO safe-copy
        return csoport;
    }

    public void setCsoport(Csoport csoport) {
        //TODO safe-copy
        this.csoport = csoport;
    }

    public Felhasznalo getUser() {
        //TODO safe-copy
        return user;
    }

    public void setUser(Felhasznalo user) {
        //TODO safe-copy
        this.user = user;
    }

    public Map<Long, TagsagTipus> getJogosultsagok() {
        return new HashMap<Long, TagsagTipus>(jogosultsagok);
    }

    public void setJogosultsagok(Map<Long, TagsagTipus> jogosultsagok) {
        this.jogosultsagok = new HashMap<Long, TagsagTipus>(jogosultsagok);
    }
}
