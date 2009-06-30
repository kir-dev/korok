/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import hu.sch.domain.ValuationStatus;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 * @author hege
 */
public class ValuationStatusConverter implements IConverter {

    public Object convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("ErtekelesStatusz nem konvertálható");
    }

    public String convertToString(Object value, Locale locale) {
        switch ((ValuationStatus) value) {
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
