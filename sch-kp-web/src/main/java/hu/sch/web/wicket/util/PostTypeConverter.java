/**
 * Copyright (c) 2009, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.web.wicket.util;

import hu.sch.domain.Post;
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