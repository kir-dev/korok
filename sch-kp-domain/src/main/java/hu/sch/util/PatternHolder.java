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

    public static Pattern mothersNamePattern;
    public static Pattern graduationYearPattern;
    public static Pattern groupNameOrPostTypePattern;
    public static Pattern phoneNumberPattern;
    public static Pattern imRegex;

    static {
        mothersNamePattern = Pattern.compile("[A-ZŰÁÉÚŐÓÜÖÍa-zéáűőúöüóí]+ [A-ZÉÁŰŐÚÖÜÓÍa-zéáűőúöüóí ]*");
        imRegex = Pattern.compile("^([a-zA-Z]+):(.*)");
        graduationYearPattern = Pattern.compile("[0-9]{8}/[0-9]");
        groupNameOrPostTypePattern = Pattern.compile("[^|:]*");
        phoneNumberPattern = Pattern.compile(".*\\d.*");
    }

    private PatternHolder() {
    }
}

