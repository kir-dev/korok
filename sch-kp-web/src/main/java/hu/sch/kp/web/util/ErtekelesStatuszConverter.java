/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.util;

import hu.sch.domain.ErtekelesStatusz;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 * @author hege
 */
public class ErtekelesStatuszConverter implements IConverter {

    public Object convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("ErtekelesStatusz nem konvertálható");
    }

    public String convertToString(Object value, Locale locale) {
        switch ((ErtekelesStatusz) value) {
            case NINCS:
                return "Nincs leadva";
            case ELBIRALATLAN:
                return "Elbírálatlan";
            case ELFOGADVA:
                return "Elfogadva";
            case ELUTASITVA:
                return "Elutasítva";
            default:
                throw new ConversionException("ErtekelesStatusz konverziós hiba");
        }
    }
}
