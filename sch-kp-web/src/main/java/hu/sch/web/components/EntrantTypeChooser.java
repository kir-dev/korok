/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.EntrantType;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 *
 * @author hege
 */
public class EntrantTypeChooser extends DropDownChoice {

    public EntrantTypeChooser(String id) {
        super(id, Arrays.asList(EntrantType.values()));

        setChoiceRenderer(new IChoiceRenderer() {

            public Object getDisplayValue(Object object) {
                return getConverter(EntrantType.class).convertToString(object, getLocale());
            }

            public String getIdValue(Object object, int index) {
                return object.toString();
            }
        });
    }
}
