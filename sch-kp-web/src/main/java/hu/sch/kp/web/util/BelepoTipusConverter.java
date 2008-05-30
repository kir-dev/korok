/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.util;

import hu.sch.domain.BelepoTipus;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 * @author hege
 */
public class BelepoTipusConverter implements IConverter {

    public Object convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("BelepoTipus nem konvertálható");
/*
        if (value.equals("KDO")) {
            return BelepoTipus.KDO;
        }
        if (value.equals("KB")) {
            return BelepoTipus.KB;
        }
        if (value.equals("ÁB")) {
            return BelepoTipus.AB;
        }
        throw new ConversionException("BelepoTipus konverziós hiba");*/
    }

    public String convertToString(Object value, Locale locale) {
        switch ((BelepoTipus) value) {
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
