package hu.sch.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.sch.api.response.AbstractEntityView;
import hu.sch.domain.user.Gender;
import hu.sch.domain.user.StudentStatus;
import hu.sch.domain.user.User;
import java.util.Date;

/**
 * Simple POJO to create a view of the {@link User} entity for JSON
 * serialization.
 *
 * @author tomi
 */
public class UserView extends AbstractEntityView<User> {

    public UserView(User user) {
        super(user, User.class);
    }

    public Long getId() {
        return entity.getId();
    }

    public String getEmailAddress() {
        return entity.getEmailAddress();
    }

    public String getNeptunCode() {
        return entity.getNeptunCode();
    }

    public String getFirstName() {
        return entity.getFirstName();
    }

    public String getLastName() {
        return entity.getLastName();
    }

    public String getNickName() {
        return entity.getNickName();
    }

    public Date getDateOfBirth() {
        return entity.getDateOfBirth();
    }

    public Gender getGender() {
        return entity.getGender();
    }

    public StudentStatus getStudentStatus() {
        return entity.getStudentStatus();
    }

    @JsonProperty("hasAvatar")
    public boolean hasPhoto() {
        return entity.hasPhoto();
    }

    public String getCellPhone() {
        return entity.getCellPhone();
    }
}
