package hu.sch.web.kp.admin;

import hu.sch.domain.Semester;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

public class SemesterForm extends Form<Semester> {

    private Semester semester;

    public SemesterForm(String id) {
        super(id);

        setSemester(new Semester());

        CheckBox isAutumnCheckbox = new CheckBox("isAutumn");
        FormComponentLabel autumnLabel = new FormComponentLabel("autumnLbl", isAutumnCheckbox);
        autumnLabel.add(isAutumnCheckbox);
        add(autumnLabel);

        addValidator(addIntegerTextField("firstYear"), addIntegerTextField("secondYear"));
    }

    private TextField<Integer> addIntegerTextField(String id) {
        TextField<Integer> tf = new TextField<>(id);
        tf.add(new RangeValidator(2000, 2030));
        tf.setRequired(true);
        add(tf);
        return tf;
    }

    private void addValidator(final TextField<Integer> firstYear, final TextField<Integer> secondYear) {
        add(new AbstractFormValidator() {
            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent[]{firstYear, secondYear};
            }

            @Override
            public void validate(Form<?> form) {
                if (Integer.parseInt(firstYear.getValue()) + 1 != Integer.parseInt(secondYear.getValue())) {
                    error(firstYear, "err.SzemeszterEvKulonbseg");
                }
            }
        });
    }

    public final Semester getSemester() {
        return semester;
    }

    public final void setSemester(Semester semester) {
        this.semester = semester;
        setModel(new CompoundPropertyModel<>(semester));
    }



}
