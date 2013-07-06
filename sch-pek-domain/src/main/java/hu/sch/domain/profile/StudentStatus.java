package hu.sch.domain.profile;

/**
 * Student statuses.
 * @author tomi
 */
public enum StudentStatus {

    ACTIVE,
    /**
     * Gólya
     */
    NEWBIE,
    GRADUATED,
    OTHER;

    public String getStatus() {
        return name().toLowerCase();
    }

    public static StudentStatus fromString(String value) {
        return StudentStatus.valueOf(value.toUpperCase());
    }
}
