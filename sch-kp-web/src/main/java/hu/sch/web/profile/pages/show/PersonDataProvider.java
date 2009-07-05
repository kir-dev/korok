/*
 *  Copyright 2008 konvergal.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package hu.sch.web.profile.pages.show;

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
                }
                else if (modelObject1 == null) {
                    compare = -1;
                }
                else if (modelObject2 == null) {
                    compare = 1;
                }
                else {
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
