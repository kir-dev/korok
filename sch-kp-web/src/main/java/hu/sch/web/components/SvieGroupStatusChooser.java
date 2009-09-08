/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import hu.sch.domain.Group;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public final class SvieGroupStatusChooser extends Panel {

    private final Group group;

    public SvieGroupStatusChooser(String id, Group group2) {
        super(id);
        group = group2;
        CheckBox checkBox = new CheckBox("svieStatus", new PropertyModel<Boolean>(group, "isSvie"));

        add(checkBox);
    }
}
