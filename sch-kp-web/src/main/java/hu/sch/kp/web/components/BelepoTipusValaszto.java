/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.BelepoTipus;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 *
 * @author hege
 */
public class BelepoTipusValaszto extends DropDownChoice {

    public BelepoTipusValaszto(String id) {
        super(id, Arrays.asList(BelepoTipus.values()));

        setChoiceRenderer(new IChoiceRenderer() {

            public Object getDisplayValue(Object object) {
                return getConverter(BelepoTipus.class).convertToString(object, getLocale());
            }

            public String getIdValue(Object object, int index) {
                return object.toString();
            }
        });
    }
}
