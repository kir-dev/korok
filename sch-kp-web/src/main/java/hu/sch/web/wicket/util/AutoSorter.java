/**
 * Copyright (c) 2009-2010, Peter Major
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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.model.PropertyModel;

/**
 * Automatikusan rendezhetünk vele listákat, felhasználva a {@link PropertyModel}t.
 *
 * @author  messo
 * @since   2.3.1
 */
public class AutoSorter {

    private static final Collator huCollator = Collator.getInstance(new Locale("hu"));

    public static <T> void sort(final List<T> list, SortParam sortParam) {
        final String prop = sortParam.getProperty();
        final int r = sortParam.isAscending() ? 1 : -1;

        // ne rendezzünk.
        if (prop == null) {
            return;
        }

        Collections.sort(list, new Comparator<T>() {

            @Override
            public int compare(T o1, T o2) {
                Comparable m1 = (Comparable) new PropertyModel<T>(o1, prop).getObject();
                Comparable m2 = (Comparable) new PropertyModel<T>(o2, prop).getObject();

                if (m1 == null && m2 == null) {
                    return 0;
                } else if (m1 == null) {
                    return -r;
                } else if (m2 == null) {
                    return r;
                } else {
                    if( m1 instanceof String && m2 instanceof String ) {
                        return r * huCollator.compare(m1, m2);
                    } else {
                        return r * m1.compareTo(m2);
                    }
                }
            }
        });
    }
}
