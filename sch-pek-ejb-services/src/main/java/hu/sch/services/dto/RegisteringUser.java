package hu.sch.services.dto;

import static hu.sch.domain.Group_.name;
import java.io.Serializable;

/**
 * User registration DTO
 *
 * @author balo
 * @author tomi
 */
public class RegisteringUser implements Serializable {

    private String screenName;
    private String mail;
    private String firstName;
    private String lastName;
    private String dormitory;
    private String roomNumber;
    private String authSchId;
    private String bmeId;

    public RegisteringUser(OAuthUserInfo userInfo) {
        mail = userInfo.getEmail();
        firstName = userInfo.getFirstName();
        lastName = userInfo.getLastName();
        authSchId = userInfo.getAuthSchInternalId();
        bmeId = userInfo.getBmeId();
        if (userInfo.getDormitory() != null) {
            roomNumber = userInfo.getDormitory().getRoom();
            dormitory = userInfo.getDormitory().getBuildingName();
        }
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(final String mail) {
        this.mail = mail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(final String uid) {
        this.screenName = uid;
    }

    public String getAuthSchId() {
        return authSchId;
    }

    public String getBmeId() {
        return bmeId;
    }
}
