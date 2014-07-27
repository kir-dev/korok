package hu.sch.web.rest.dto;

import hu.sch.domain.user.User;
import hu.sch.domain.user.UserStatus;
import org.codehaus.jackson.annotate.JsonProperty;

public class ProfileView {

    private final Properties userProperties;

    public ProfileView(User user) {
        this.userProperties = new Properties(user);
    }

    public boolean isSuccess() {
        return true;
    }

    @JsonProperty("user")
    public Properties getUserProperties() {
        return userProperties;
    }

    public static class Properties {

        private final User user;

        public Properties(User user) {
            this.user = user;
        }

        @JsonProperty("schacPersonalUniqueId")
        public Long getId() {
            return user.getId();
        }

        @JsonProperty("mail")
        public String getEmailAddress() {
            return user.getEmailAddress();
        }

        @JsonProperty("givenName")
        public String getFirstName() {
            return user.getFirstName();
        }

        @JsonProperty("sn")
        public String getLastName() {
            return user.getLastName();
        }

        @JsonProperty("uid")
        public String getScreenName() {
            return user.getScreenName();
        }

        @JsonProperty("eduPersonNickName")
        public String getNickName() {
            return user.getNickName();
        }

        public String getDisplayName() {
            return user.getFullName();
        }

        @JsonProperty("mobile")
        public String getCellPhone() {
            return user.getCellPhone();
        }
    }
}
