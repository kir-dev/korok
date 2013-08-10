package hu.sch.web.wicket.components.choosers;

import hu.sch.domain.enums.ValuationStatus;
import java.util.Arrays;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 *
 * @author hege
 */
public class ValuationStatusChooser extends DropDownChoice<ValuationStatus> {

    private static ValuationStatus[] valaszthatoLista = {ValuationStatus.ELBIRALATLAN, ValuationStatus.ELFOGADVA, ValuationStatus.ELUTASITVA};

    public ValuationStatusChooser(String id) {
        super(id, Arrays.asList(valaszthatoLista));

        setChoiceRenderer(new IChoiceRenderer<ValuationStatus>() {

            @Override
            public Object getDisplayValue(ValuationStatus object) {
                return getConverter(ValuationStatus.class).convertToString(object, getLocale());
            }

            @Override
            public String getIdValue(ValuationStatus object, int index) {
                return object.toString();
            }
        });
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        if (getModelObject() != null) {
            String style = tag.getAttribute("style");
            if (style == null) {
                style = "";
            }
            switch (getModelObject()) {
                case ELBIRALATLAN:
                    style += " background: #888888 none repeat;";
                    break;
                case ELFOGADVA:
                    style += " background: #00FF00 none repeat;";
                    break;
                case ELUTASITVA:
                    style += " background: #FF0000 none repeat;";
                    break;
                default:
                    break;
            }
            tag.getAttributes().put("style", style);
        }
    }
}
