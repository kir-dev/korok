package hu.sch.domain.user;

/**
 * User status in SSO service.
 * Could be "active" or "inactive".
 *
 * @author tomi
 */
public enum UserStatus {

    ACTIVE,
    INACTIVE;

    /**
     * Returns the name of the user status as used in OpenDj.
     */
    public String getStatus() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static UserStatus fromString(String value) {
        return UserStatus.valueOf(value.toUpperCase());
    }
}
