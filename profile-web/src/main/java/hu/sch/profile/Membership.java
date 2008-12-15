package hu.sch.profile;

import java.io.Serializable;

/**
 *
 * @author Adam Lantos
 */
public class Membership implements Serializable {
    private String groupName;
    private String status;

    public Membership(String groupName, String status) {
        this.groupName = groupName;
        this.status = status;
    }
    
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
