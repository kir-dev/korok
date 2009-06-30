/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.admin;

import hu.sch.domain.ValuationPeriod;
import hu.sch.domain.Semester;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.web.kp.pages.group.GroupHierarchy;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 *
 * @author aldaris
 */
public class EditSettings extends SecuredPageTemplate {

    private Semester semester;
    ValuationPeriod valuationPeriod;

    public EditSettings() {
        super();
        if (!isCurrentUserJETI()) {
            info("Nincs jogod a megadott művelethez");
            setResponsePage(GroupHierarchy.class);
            return;
        }
        setHeaderLabelText("Beállítások");
        add(new FeedbackPanel("pagemessages"));
        try {
            semester = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException e) {
            semester = new Semester();
        }


        setValuationPeriod(systemManager.getErtekelesIdoszak());

        Form beallitasForm = new Form("settingsForm") {

            @Override
            public void onSubmit() {
                try {
                    systemManager.setSzemeszter(getSemester());
                    systemManager.setErtekelesIdoszak(getValuationPeriod());
                    getSession().info(getLocalizer().getString("info.BeallitasokMentve", this));
                } catch (Exception e) {
                    getSession().error(getLocalizer().getString("err.BeallitasokFailed", this));
                    e.printStackTrace();
                }
            }
        };
        beallitasForm.setModel(new CompoundPropertyModel(semester));
        final TextField firstYear = new TextField("firstYear");
        beallitasForm.add(firstYear.add(new RangeValidator(2000, 2030)));
        final TextField secondYear = new TextField("secondYear");
        beallitasForm.add(secondYear.add(new RangeValidator(2000, 2030)));
        beallitasForm.add(new CheckBox("isAutumn"));
        beallitasForm.add(new AbstractFormValidator() {

            public FormComponent[] getDependentFormComponents() {
                return new FormComponent[]{firstYear, secondYear};
            }

            public void validate(Form form) {
                if (Integer.parseInt(firstYear.getValue()) + 1 !=
                        Integer.parseInt(secondYear.getValue())) {
                    error(firstYear, "err.SzemeszterEvKulonbseg");
                }
            }
        });
        DropDownChoice ddc1 = new DropDownChoice("periodSelector",
                Arrays.asList(ValuationPeriod.values()));
        ddc1.setRequired(true);
        ddc1.setModel(new PropertyModel(this, "valuationPeriod"));

        ddc1.setChoiceRenderer(new IChoiceRenderer() {

            public Object getDisplayValue(Object object) {
                return getLocalizer().getString("ertekelesidoszak." + object.toString(), getParent());
            }

            public String getIdValue(Object object, int index) {
                return object.toString();
            }
        });
        beallitasForm.add(ddc1);
        add(beallitasForm);
    }

    public ValuationPeriod getValuationPeriod() {
        return valuationPeriod;
    }

    public void setValuationPeriod(ValuationPeriod period) {
        this.valuationPeriod = period;
    }

    @Override
    public Semester getSemester() {
        return semester;
    }
}
