package hu.sch.domain.profile;

import org.slf4j.LoggerFactory;

/**
 *
 * ISO-5218 compliant gender definitions.
 *
 * 0 - unknown
 * 1 - male,
 * 2 - female,
 * 9 - not specified
 *
 * @author tomi
 */
public enum Gender {

    UNKNOWN(0),
    MALE(1),
    FEMALE(2),
    NOTSPECIFIED(9);

    private final int genderValue;

    private Gender(int genderValue) {
        this.genderValue = genderValue;
    }

    public String getValueString() {
        return String.valueOf(genderValue);
    }

    public static Gender fromString(String value) {
        if ("0".equals(value)) {
            return Gender.UNKNOWN;
        } else if ("1".equals(value)) {
            return Gender.MALE;
        } else if ("2".equals(value)) {
            return Gender.FEMALE;
        } else if ("9".equals(value)) {
            return Gender.NOTSPECIFIED;
        } else {
            LoggerFactory.getLogger(Gender.class).warn("Illegal gender value: {}. Returning default.", value);
            return Gender.NOTSPECIFIED;
        }
    }
}
