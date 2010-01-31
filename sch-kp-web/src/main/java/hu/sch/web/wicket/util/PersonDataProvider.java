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

import hu.sch.domain.profile.Person;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author konvergal
 */
public class PersonDataProvider extends SortableDataProvider<Person> {

    private final List<Person> persons = new ArrayList<Person>();
    //private final List<Person> sortedPersons = new ArrayList<Person>();

    public PersonDataProvider(List<Person> personList) {
        super();

        setSort("fullName", true);
        setPersons(personList);
    }

    public Iterator<Person> iterator(int first, int count) {
        List<Person> newList = new ArrayList<Person>();
        sortPersons();
        //newList.addAll(sortedPersons.subList(first, first + count));
        newList.addAll(persons.subList(first, first + count));

        return newList.iterator();
    }

    public void setPersons(List<Person> persons) {
        this.persons.clear();
        this.persons.addAll(persons);

        this.sortPersons();
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void sortPersons() {
//        sortedPersons.clear();
//        sortedPersons.addAll(persons);

        final String sortColumn = this.getSort().getProperty();
        final boolean ascending = this.getSort().isAscending();

        Collections.sort(persons, new Comparator<Person>() {

            public int compare(Person obj1, Person obj2) {
                PropertyModel<Person> model1 = new PropertyModel<Person>(obj1, sortColumn);
                PropertyModel<Person> model2 = new PropertyModel<Person>(obj2, sortColumn);

                Object modelObject1 = model1.getObject();
                Object modelObject2 = model2.getObject();

                int compare;
                if (modelObject1 == null && modelObject2 == null) {
                    compare = 0;
                } else if (modelObject1 == null) {
                    compare = -1;
                } else if (modelObject2 == null) {
                    compare = 1;
                } else {
                    Collator huCollator = Collator.getInstance(new Locale("hu"));
                    compare = huCollator.compare(modelObject1, modelObject2);
//                    compare = ((Comparable) modelObject1).compareTo(modelObject2);
                }

                if (!ascending) {
                    compare *= -1;
                }
                return compare;
            }
        });
    }

    public int size() {
        return persons.size();
    }

    public IModel<Person> model(final Person object) {
        return new AbstractReadOnlyModel<Person>() {

            @Override
            public Person getObject() {
                return object;
            }
        };
    }
}
