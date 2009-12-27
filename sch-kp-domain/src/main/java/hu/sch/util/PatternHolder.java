/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.util;

import java.util.regex.Pattern;

/**
 *
 * @author aldaris
 */
public class PatternHolder {

    public static final Pattern MOTHER_NAME_PATTERN;
    public static final Pattern GRADUATION_YEAR_PATTERN;
    public static final Pattern GROUP_NAME_OR_POSTTYPE_PATTERN;
    public static final Pattern PHONE_NUMBER_PATTERN;
    public static final Pattern IM_PATTERN;
    public static final Pattern VIRID_PATTERN;
    public static final Pattern NEPTUN_PATTERN;
    public static final Pattern ENTITLEMENT_PATTERN;

    static {
        MOTHER_NAME_PATTERN = Pattern.compile("[A-ZŰÁÉÚŐÓÜÖÍa-zéáűőúöüóí]+ [A-ZÉÁŰŐÚÖÜÓÍa-zéáűőúöüóí ]*");
        IM_PATTERN = Pattern.compile("^([a-zA-Z]+):(.*)");
        GRADUATION_YEAR_PATTERN = Pattern.compile("[0-9]{8}/[0-9]");
        GROUP_NAME_OR_POSTTYPE_PATTERN = Pattern.compile("[^|:]*");
        PHONE_NUMBER_PATTERN = Pattern.compile(".*\\d.*");
        VIRID_PATTERN = Pattern.compile("^.*:([0-9]+)$");
        NEPTUN_PATTERN = Pattern.compile("^.*:([A-Za-z0-9]{6,7})$");
        //                                                         jog:csoportnév:csoportid
        ENTITLEMENT_PATTERN = Pattern.compile("^.*:entitlement:([^:]+):([^:]+):([0-9]+)$");
    }

    private PatternHolder() {
    }
}

