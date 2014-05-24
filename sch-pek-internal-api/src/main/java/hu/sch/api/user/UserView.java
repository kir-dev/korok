package hu.sch.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import hu.sch.api.response.EntityView;
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
public class UserView implements EntityView {
    private final User user;

    public UserView(User user) {
        this.user = user;
    }

    @Override
    public boolean hasEntity() {
        return user != null;
    }

    @Override
    public String getEntityName() {
        return User.class.getSimpleName();
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmailAddress() {
        return user.getEmailAddress();
    }

    public String getNeptunCode() {
        return user.getNeptunCode();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getNickName() {
        return user.getNickName();
    }

    public Date getDateOfBirth() {
        return user.getDateOfBirth();
    }

    public Gender getGender() {
        return user.getGender();
    }

    public StudentStatus getStudentStatus() {
        return user.getStudentStatus();
    }

    @JsonProperty("hasAvatar")
    public boolean hasPhoto() {
        return user.hasPhoto();
    }

    public String getCellPhone() {
        return user.getCellPhone();
    }
}
