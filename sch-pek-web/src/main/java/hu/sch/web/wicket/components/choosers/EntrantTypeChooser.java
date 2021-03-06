package hu.sch.web.wicket.components.choosers;

import hu.sch.domain.enums.EntrantType;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 *
 * @author hege
 */
public class EntrantTypeChooser extends DropDownChoice<EntrantType> {

    public EntrantTypeChooser(String id) {
        super(id, Arrays.asList(EntrantType.values()));

        setChoiceRenderer(new IChoiceRenderer<EntrantType>() {

            @Override
            public Object getDisplayValue(EntrantType object) {
                return getConverter(EntrantType.class).convertToString(object, getLocale());
            }

            @Override
            public String getIdValue(EntrantType object, int index) {
                return object.toString();
            }
        });
    }
}
