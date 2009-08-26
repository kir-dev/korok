/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.util;

import hu.sch.domain.Post;
import hu.sch.domain.PostType;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 * @author aldaris
 */
public class PostTypeConverter implements IConverter {

    private static Logger log = Logger.getLogger(PostTypeConverter.class);

    @Override
    public Object convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("Tagságtípus nem konvertálható");
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public String convertToString(Object value, Locale locale) {
        if (value instanceof List) {
            List<Post> posts = ((List<Post>) value);
            StringBuilder sb = new StringBuilder(posts.size() * 16);
            for (Post post : posts) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(actualConverter(post));
            }
            if (sb.length() == 0) {
                sb.append("tag");
            }
            return sb.toString();
        } else {
            log.error("Invalid input type for MembershipTypeConverter");
            return "invalid";
        }
//        if (value instanceof MembershipType) {
//            return actualConverter((MembershipType) value);
//        }
//
//        MembershipType[] values = (MembershipType[]) value;
//        StringBuilder ret = new StringBuilder(values.length * 16);
//        for (MembershipType membershipType : values) {
//            if (ret.length() != 0) {
//                ret.append(", ");
//            }
//
//            ret.append(actualConverter(membershipType));
//        }
//        return ret.toString();
    }

    public String actualConverter(Post value) {
        //úgy tűnik, hogy a db-ben ki vannak pótolva a nem használt karakterek
        //szóközökkel, ezért kell a trim()
        return value.getPostType().toString();
        /*
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
        }*/
    }
}
