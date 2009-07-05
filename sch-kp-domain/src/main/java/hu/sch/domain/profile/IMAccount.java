package hu.sch.domain.profile;

import java.io.Serializable;

/**
 *
 * @author hege
 */
public class IMAccount implements Serializable {
    private IMProtocol protocol;
    private String presenceID;

    public IMAccount(IMProtocol protocol, String presenceID) {
        this.protocol = protocol;
        this.presenceID = presenceID;
    }

    public IMProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(IMProtocol protocol) {
        this.protocol = protocol;
    }

    public String getPresenceID() {
        return presenceID;
    }

    public void setPresenceID(String presenceID) {
        this.presenceID = presenceID;
    }

    /**
     * Returns protocol:presenceID
     * @return
     */
    @Override
    public String toString() {
        return protocol.toString() + ":" + presenceID;
    }
}
