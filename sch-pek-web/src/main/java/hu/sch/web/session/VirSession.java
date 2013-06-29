package hu.sch.web.session;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

/**
 *
 * @author hege
 */
public class VirSession extends WebSession {

    private Long userId;

    public VirSession(Request request) {
        super(request);
    }

    public synchronized Long getUserId() {
        return userId;
    }

    public synchronized void setUserId(Long userId) {
        this.userId = userId;
        dirty();
    }
}
