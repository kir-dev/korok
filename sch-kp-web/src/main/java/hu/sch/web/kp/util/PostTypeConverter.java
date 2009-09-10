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
                if (post.getMembership().getEnd() != null) {
                    return "öregtag";
                }
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
    }

    public String actualConverter(Post value) {
        return value.getPostType().toString();
    }
}
