package hu.sch.web.wicket.util;

import hu.sch.domain.Membership;
import java.util.Locale;
import org.apache.wicket.util.convert.IConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
@SuppressWarnings("unchecked")
public class PostTypeConverter implements IConverter {

    private static Logger log = LoggerFactory.getLogger(PostTypeConverter.class);

    @Override
    public Object convertToObject(String value, Locale locale) {
        throw new UnsupportedOperationException("Tagságtípus nem konvertálható");
    }

    @Override
    public String convertToString(Object value, Locale locale) {
        if (value instanceof Membership) {
            Membership ms = (Membership) value;
            return ms.getPostsAsString();
        } else {
            log.error("Invalid input type for MembershipTypeConverter");
            return "invalid";
        }
    }
}
