/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.Csoport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author aldaris
 */
public class SearchAutoCompleteTextField extends AutoCompleteTextField {

    final String[] csoportok;

    public SearchAutoCompleteTextField(String id, IModel object, String[] csoportok) {
        super(id, object);
        this.csoportok = csoportok;
    }

    protected Iterator getChoices(String input) {
        if (Strings.isEmpty(input)) {
            return Collections.EMPTY_LIST.iterator();
        }

        List choices = new ArrayList(10);

        for (int i = 0; i < csoportok.length; i++) {
            final String csoport = csoportok[i];

            if (csoport.toUpperCase().contains(input.toUpperCase())) {
                choices.add(csoport);
                if (choices.size() == 10) {
                    break;
                }
            }
        }

        return choices.iterator();
    }
}
