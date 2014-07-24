package hu.sch.services.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.Map;

public class OAuthUserInfo {

    @SerializedName("internal_id")
    private String authSchInternalId;

    @SerializedName("linkedAccounts")
    private Map<String, String> linkedAccounts;

    @SerializedName("roomNumber")
    private OAuthDormitory dormitory;

    public String getAuthSchInternalId() {
        return authSchInternalId;
    }

    public void setAuthSchInternalId(String authSchInternalId) {
        this.authSchInternalId = authSchInternalId;
    }

    public Map<String, String> getLinkedAccounts() {
        return Collections.unmodifiableMap(linkedAccounts);
    }

    public void setLinkedAccounts(Map<String, String> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public OAuthDormitory getDormitory() {
        return dormitory;
    }

    public void setDormitory(OAuthDormitory dormitory) {
        this.dormitory = dormitory;
    }
}
