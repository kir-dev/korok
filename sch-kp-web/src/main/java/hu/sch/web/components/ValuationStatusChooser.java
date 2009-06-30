/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.ValuationStatus;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 *
 * @author hege
 */
public class ValuationStatusChooser extends DropDownChoice {
    private static ValuationStatus[] valaszthatoLista = {ValuationStatus.ELBIRALATLAN, ValuationStatus.ELFOGADVA, ValuationStatus.ELUTASITVA};
    
    public ValuationStatusChooser(String id) {
        super(id, Arrays.asList(valaszthatoLista));

        setChoiceRenderer(new IChoiceRenderer() {
            public Object getDisplayValue(Object object) {
                return getConverter(ValuationStatus.class).convertToString(object, getLocale());
            }

            public String getIdValue(Object object, int index) {
                return object.toString();
            }
        });
    }
}
