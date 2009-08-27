/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import java.util.regex.Pattern;

/**
 *
 * @author aldaris
 */
public class PatternHolder {

    public static Pattern mothersNamePattern;
    public static Pattern graduationYearPattern;

    static {
        mothersNamePattern = Pattern.compile("[A-ZŰÁÉÚŐÓÜÖÍa-zéáűőúöüóí]+ [A-ZÉÁŰŐÚÖÜÓÍa-zéáűőúöüóí ]*");
        graduationYearPattern = Pattern.compile("[0-9]{8}/[0-9]");
    }

    private PatternHolder() {
    }
}

