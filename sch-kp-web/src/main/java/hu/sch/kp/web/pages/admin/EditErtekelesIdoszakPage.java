/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.admin;

import hu.sch.domain.ErtekelesIdoszak;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class EditErtekelesIdoszakPage extends SecuredPageTemplate {

    ErtekelesIdoszak ertekelesIdoszak;

    public EditErtekelesIdoszakPage() {
        setErtekelesIdoszak(systemManager.getErtekelesIdoszak());

        Form ertekelesidoszakform = new Form("ertekelesidoszakform") {

            @Override
            protected void onSubmit() {
                systemManager.setErtekelesIdoszak(getErtekelesIdoszak());
                setResponsePage(Index.class);
                return;
            }
        };
        DropDownChoice ddc1 = new DropDownChoice("ertekelesidoszak",
                Arrays.asList(ErtekelesIdoszak.values()));
        ddc1.setRequired(true);
        ddc1.setModel(new PropertyModel(this, "ertekelesIdoszak"));

        ddc1.setChoiceRenderer(new IChoiceRenderer() {

            public Object getDisplayValue(Object object) {
                return getLocalizer().getString("ertekelesidoszak." + object.toString(), getParent());
            }

            public String getIdValue(Object object, int index) {
                return object.toString();
            }
        });

        ertekelesidoszakform.add(ddc1);
        add(ertekelesidoszakform);
    }

    public ErtekelesIdoszak getErtekelesIdoszak() {
        return ertekelesIdoszak;
    }

    public void setErtekelesIdoszak(ErtekelesIdoszak idoszak) {
        this.ertekelesIdoszak = idoszak;
    }
}
