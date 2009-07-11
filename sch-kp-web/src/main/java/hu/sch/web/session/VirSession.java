package hu.sch.web.session;

import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

/**
 *
 * @author hege
 */
public class VirSession extends WebSession {

    private Long userId;
    private Long groupId;

    public VirSession(Request request) {
        super(request);
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
