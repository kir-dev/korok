/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import hu.sch.domain.EntrantType;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 * @author hege
 */
public class EntrantTypeConverter implements IConverter {

    public Object convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("BelepoTipus nem konvertálható");
/*
        if (value.equals("KDO")) {
            return EntrantType.KDO;
        }
        if (value.equals("KB")) {
            return EntrantType.KB;
        }
        if (value.equals("ÁB")) {
            return EntrantType.AB;
        }
        throw new ConversionException("EntrantType konverziós hiba");*/
    }

    public String convertToString(Object value, Locale locale) {
        switch ((EntrantType) value) {
            case KDO:
                return "KDO";
            case KB:
                return "KB";
            case AB:
                return "ÁB";
            default:
                throw new ConversionException("BelepoTipus konverziós hiba");
        }
    }
}
