package hu.sch.domain.profile;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author hege
 */
public class IMAccount implements Serializable {

    private IMProtocol protocol;
    private String presenceID;
    private UUID uuid;

    public IMAccount(IMProtocol protocol, String presenceID) {
        this.protocol = protocol;
        this.presenceID = presenceID;
        uuid = UUID.randomUUID();
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

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns protocol:presenceID
     * @return
     */
    @Override
    public String toString() {
        return protocol.toString() + ":" + presenceID;
    }

    @Override
    public boolean equals(Object obj) {
        IMAccount o2 = (IMAccount) obj;
        return o2.getUuid().equals(uuid);
    }
}
