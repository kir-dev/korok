package hu.sch.util;

import java.util.regex.Pattern;

/**
 *
 * @author aldaris
 */
public class PatternHolder {

    public static final Pattern ENTITLEMENT_PATTERN;
    public static final Pattern GRADUATION_YEAR_PATTERN;
    public static final Pattern GROUP_NAME_OR_POSTTYPE_PATTERN;
    public static final Pattern IM_PATTERN;
    public static final Pattern NAME_PATTERN;
    public static final Pattern NEPTUN_PATTERN;
    public static final Pattern PHONE_NUMBER_PATTERN;
    public static final Pattern UID_PATTERN;
    public static final Pattern VIRID_PATTERN;
    public static final Pattern EDUCATION_ID_PATTERN;

    static {
        //                                                         jog:csoportnév:csoportid
        ENTITLEMENT_PATTERN = Pattern.compile("^.*:entitlement:([^:]+):([^:]+):([0-9]+)$");
        GRADUATION_YEAR_PATTERN = Pattern.compile("[0-9]{8}/[1-2]");
        GROUP_NAME_OR_POSTTYPE_PATTERN = Pattern.compile("[^|:]*");
        IM_PATTERN = Pattern.compile("^([a-zA-Z]+):(.*)");
        NAME_PATTERN = Pattern.compile("^[A-ZÁÉÍÓÖŐÚÜŰ][a-záéíóöőúüű]+([ \\-][A-ZÁÉÍÓÖŐÚÜŰ][a-záéíóöőúüű]+)*$");
        NEPTUN_PATTERN = Pattern.compile("^.*:([A-Za-z0-9]{6,7})$");
        PHONE_NUMBER_PATTERN = Pattern.compile(".*\\d.*");
        UID_PATTERN = Pattern.compile("^[a-z0-9]*$");
        VIRID_PATTERN = Pattern.compile("^.*:([0-9]+)$");
        EDUCATION_ID_PATTERN = Pattern.compile("^[0-9]{11}$");
    }

    private PatternHolder() {
    }
}

