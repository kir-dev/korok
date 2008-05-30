/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

import hu.sch.domain.ErtekelesStatusz;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 *
 * @author hege
 */
public class ErtekelesStatuszValaszto extends DropDownChoice {
    private static ErtekelesStatusz[] valaszthatoLista = {ErtekelesStatusz.ELBIRALATLAN, ErtekelesStatusz.ELFOGADVA, ErtekelesStatusz.ELUTASITVA};
    
    public ErtekelesStatuszValaszto(String id) {
        super(id, Arrays.asList(valaszthatoLista));

        setChoiceRenderer(new IChoiceRenderer() {
            public Object getDisplayValue(Object object) {
                return getConverter(ErtekelesStatusz.class).convertToString(object, getLocale());
            }

            public String getIdValue(Object object, int index) {
                return object.toString();
            }
        });
    }
}
