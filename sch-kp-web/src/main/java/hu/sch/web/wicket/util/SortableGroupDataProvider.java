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

import hu.sch.domain.Group;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

/**
 *
 * @author aldaris
 */
public class SortableGroupDataProvider extends SortableDataProvider<Group> {

    private List<Group> groups;
    private static final Collator huCollator = Collator.getInstance(new Locale("hu"));

    public SortableGroupDataProvider(List<Group> group) {
        groups = group;
        setSort("name", true);
    }

    @Override
    public Iterator<Group> iterator(int first, int count) {
        SortParam sp = getSort();
        return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
    }

    @Override
    public int size() {
        return groups.size();
    }

    public List<Group> getIndex(String prop, boolean asc) {
        if (prop == null) {
            return groups;
        }
        if (prop.equals("name")) {
            if (asc) {
                Collections.sort(groups, new Comparator<Group>() {

                    @Override
                    public int compare(Group o1, Group o2) {
                        return huCollator.compare(o1.getName(), o2.getName());
                    }
                });
            } else {
                Collections.sort(groups, new Comparator<Group>() {

                    @Override
                    public int compare(Group o1, Group o2) {
                        return huCollator.compare(o2.getName(), o1.getName());
                    }
                });
            }
        } else {
            throw new RuntimeException("uknown sort option [" + prop
                    + "]. valid options: [name] , [svieMembershipType]");
        }
        return groups;
    }

    private List<Group> find(int first, int count, String property, boolean ascending) {
        List<Group> ret = getIndex(property, ascending).subList(first, first + count);
        return ret;
    }

    @Override
    public IModel<Group> model(Group object) {
        return new LoadableDetachableGroupModel(object);
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
