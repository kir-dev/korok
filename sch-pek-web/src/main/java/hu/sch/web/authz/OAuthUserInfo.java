package hu.sch.web.authz;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class OAuthUserInfo {

    @SerializedName("internal_id")
    private String authSchInternalId;

    @SerializedName("linkedAccounts")
    private Map<String, String> linkedAccounts;

    @SerializedName("roomNumber")
    private String roomNumber;

    public String getAuthSchInternalId() {
        return authSchInternalId;
    }

    public void setAuthSchInternalId(String authSchInternalId) {
        this.authSchInternalId = authSchInternalId;
    }

    public Map<String, String> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(Map<String, String> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }


}
