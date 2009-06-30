/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import hu.sch.domain.MembershipType;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 * @author aldaris
 */
public class MembershipTypeConverter implements IConverter {

    public Object convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("BelepoTipus nem konvertálható");
    }

    public String convertToString(Object value, Locale locale) {
        MembershipType[] values = (MembershipType[]) value;
        String ret = new String();
        for (int i = 0; i < values.length; i++) {
            ret += (ret.length() == 0 ? actualConverter(values[i]) : (", " + actualConverter(values[i])));
        }
        return ret;
    }

    public String actualConverter(MembershipType value) {
        switch (value) {
            case TAG:
                return "tag";
            case KORVEZETO:
                return "körvezető";
            case VOLTKORVEZETO:
                return "volt körvezető";
            case GAZDASAGIS:
                return "gazdaságis";
            case PRMENEDZSER:
                return "PR menedzser";
            case VENDEGFOGADAS:
                return "vendégfogadó";
            case OREGTAG:
                return "öregtag";
            case JELENTKEZO:
                return "jelentkező";
            default:
                throw new ConversionException("TagsagTipus konverziós hiba");
        }
    }
}