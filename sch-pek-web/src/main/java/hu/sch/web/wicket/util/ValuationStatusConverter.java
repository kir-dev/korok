package hu.sch.web.wicket.util;

import hu.sch.domain.enums.ValuationStatus;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 * @author hege
 */
public class ValuationStatusConverter implements IConverter {

    @Override
    public Object convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("ErtekelesStatusz nem konvertálható");
    }

    @Override
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
