/**
 * Copyright (c) 2008-2010, Peter Major
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

import hu.sch.domain.User;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public class SortableUserDataProvider extends SortableDataProvider<User> {

    private List<User> users;
    private static final Collator huCollator = Collator.getInstance(new Locale("hu"));

    public SortableUserDataProvider(List<User> user) {
        users = user;
        setSort("name", true);
    }

    @Override
    public Iterator<User> iterator(int first, int count) {
        SortParam sp = getSort();
        return find(first, count, sp.getProperty(), sp.isAscending()).iterator();
    }

    @Override
    public int size() {
        return users.size();
    }

    public List<User> getIndex(String prop, boolean asc) {
        if (prop == null) {
            return users;
        }
        if (prop.equals("name")) {
            if (asc) {
                Collections.sort(users, new Comparator<User>() {

                    @Override
                    public int compare(User o1, User o2) {
                        return huCollator.compare(o1.getName(), o2.getName());
                    }
                });
            } else {
                Collections.sort(users, new Comparator<User>() {

                    @Override
                    public int compare(User o1, User o2) {
                        return huCollator.compare(o2.getName(), o1.getName());
                    }
                });
            }
        } else if (prop.equals("svieMembershipType")) {
            if (asc) {
                Collections.sort(users, new Comparator<User>() {

                    @Override
                    public int compare(User o1, User o2) {
                        if (o1.getSvieMembershipType().equals(o2.getSvieMembershipType())) {
                            return huCollator.compare(o1.getName(), o2.getName());
                        } else {
                            return o1.getSvieMembershipType().compareTo(o2.getSvieMembershipType());
                        }
                    }
                });
            } else {
                Collections.sort(users, new Comparator<User>() {

                    @Override
                    public int compare(User o1, User o2) {
                        if (o1.getSvieMembershipType().equals(o2.getSvieMembershipType())) {
                            return huCollator.compare(o2.getName(), o1.getName());
                        } else {
                            return o2.getSvieMembershipType().compareTo(o1.getSvieMembershipType());
                        }
                    }
                });
            }
        } else {
            throw new RuntimeException("uknown sort option [" + prop
                    + "]. valid options: [name] , [svieMembershipType]");
        }
        return users;
    }

    @Override
    public IModel<User> model(User object) {
        return new Model<User>(object);
    }

    private List<User> find(int first, int count, String property, boolean ascending) {
        List<User> ret = getIndex(property, ascending).subList(first, first + count);
        return ret;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
