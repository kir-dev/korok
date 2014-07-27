package hu.sch.services.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.Map;

public class OAuthUserInfo {

    @SerializedName("internal_id")
    private String authSchInternalId;

    @SerializedName("sn")
    private String lastName;

    @SerializedName("givenName")
    private String firstName;

    @SerializedName("mail")
    private String email;

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

    public Long getUserId() {
        String virIdString = linkedAccounts.get("vir");

        try {
            return Long.valueOf(virIdString);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public String getBmeId() {
        return linkedAccounts.get("bme");
    }

    public OAuthDormitory getDormitory() {
        return dormitory;
    }

    public void setDormitory(OAuthDormitory dormitory) {
        this.dormitory = dormitory;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
