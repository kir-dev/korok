package hu.sch.ejb.test.builder;

import hu.sch.domain.SvieMembershipType;
import hu.sch.domain.SvieStatus;
import hu.sch.domain.user.Gender;
import hu.sch.domain.user.StudentStatus;
import hu.sch.domain.user.User;

/**
 *
 * @author tomi
 */
public class UserBuilder implements Builder<User> {

    private String lastName = "Teszt";
    private String firstName = "Elek";
    private SvieMembershipType svieMembership = SvieMembershipType.NEMTAG;
    private SvieStatus svieStatus = SvieStatus.NEMTAG;
    private String screenName = "teszt.elek";
    private Gender gender = Gender.MALE;
    private StudentStatus studentStatus = StudentStatus.ACTIVE;

    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder withScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    public UserBuilder withSvieMemebership(SvieMembershipType svieMemebership) {
        this.svieMembership = svieMemebership;
        return this;
    }

    public UserBuilder withSvieStatus(SvieStatus status) {
        this.svieStatus = status;
        return this;
    }

    public UserBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public UserBuilder withStudentStatus(StudentStatus status) {
        this.studentStatus = status;
        return this;
    }

    @Override
    public User build() {
        User user = new User();
        user.setGender(gender);
        user.setStudentStatus(studentStatus);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setSvieMembershipType(svieMembership);
        user.setSvieStatus(svieStatus);
        user.setScreenName(screenName);

        return user;
    }

}
