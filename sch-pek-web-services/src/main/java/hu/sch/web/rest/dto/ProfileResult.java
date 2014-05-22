package hu.sch.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.sch.domain.user.User;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author balo
 * @author tomi
 */
public class ProfileResult {

    private static final String VIRID_PREFIX = "urn:mace:terena.org:schac:personalUniqueID:hu:BME-SCH-VIR:person:";
    private static final String STUDENT_STATUS_PREFIX = "urn:mace:terena.org:schac:status:sch.hu:student_status:";

    private final User user;
    private final String entitlement;

    public ProfileResult(User user, String entitlement) {
        this.user = user;
        this.entitlement = entitlement;
    }

    public String getUid() {
        return user.getScreenName();
    }

    @JsonProperty("schacPersonalUniqueId")
    public String getVirId() {
        return VIRID_PREFIX + user.getId();
    }

    public String getNeptun() {
        return user.getNeptunCode();
    }

    @JsonProperty("cn")
    public String getFullName() {
        return user.getFullName();
    }

    @JsonProperty("givenName")
    public String getFirstName() {
        return user.getFirstName();
    }

    @JsonProperty("sn")
    public String getLastName() {
        return user.getLastName();
    }

    @JsonProperty("eduPersonNickName")
    public String getNick() {
        return user.getNickName();
    }

    @JsonProperty("mail")
    public String getEmail() {
        return user.getEmailAddress();
    }

    @JsonProperty("schacUserStatus")
    public String getUserStatus() {
        return STUDENT_STATUS_PREFIX + user.getStudentStatus();
    }

    @JsonProperty("roomNumber")
    public String getRoom() {
        final String room = user.getFullRoomNumber();
        if (StringUtils.isBlank(room)) {
            return null;
        }
        return room;
    }

    @JsonProperty("eduPersonEntitlement")
    public String getEntitlement() {
        return entitlement;
    }

    public String getDisplayName() {
        return user.getFullName();
    }
}
