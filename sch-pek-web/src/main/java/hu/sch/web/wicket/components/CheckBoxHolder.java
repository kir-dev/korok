package hu.sch.web.wicket.components;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * Egy olyan panel, amiben egyetlen CheckBox van, ami attól függően van bejelölve,
 * hogy az átadott IModel-be ágyazott objektum getSelected() függvénye mit ad vissza.
 *
 * @author  messo
 * @since   2.3.1
 */
public class CheckBoxHolder<T> extends Panel {

    /**
     * Létrehoz egy CheckBoxHolder panelt.
     *
     * @param id        a panel wicket idja
     * @param obj       objektum
     * @param property  a property neve, amivel bindoljuk a checkboxot
     * @see             Panel
     */
    public CheckBoxHolder(String id, T obj, String property) {
        super(id);
        add(new CheckBox("check", new PropertyModel<Boolean>(obj, property)));
    }
}
