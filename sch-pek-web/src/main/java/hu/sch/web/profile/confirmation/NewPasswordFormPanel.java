package hu.sch.web.profile.confirmation;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.StringValidator;

/**
 *
 * @author balo
 */
abstract class NewPasswordFormPanel extends Panel {

    //these fields used by the form's compoundpropertymodel
    private String password;
    private String passwordConfirm;

    public NewPasswordFormPanel(final String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addPasswordFields();
    }

    public abstract void onPanelSubmit();

    private void addPasswordFields() {
        final Form<Void> form = new StatelessForm<Void>("passwordForm",
                new CompoundPropertyModel(this)) {

                    @Override
                    protected void onSubmit() {
                        onPanelSubmit();
                    }
                };

        final TextField passwordTF = new PasswordTextField("password");
        //, new PropertyModel<String>(this, "password"));
        passwordTF
                .setLabel(new ResourceModel("passwordTF"))
                .add(StringValidator.minimumLength(6));

        final FormComponentLabel passwordLabel = new SimpleFormComponentLabel("passwordLabel", passwordTF);
        form.add(passwordLabel, passwordTF);

        final TextField passwordConfirmTF = new PasswordTextField("passwordConfirm");
        //new PropertyModel<String>(this, "passwordConfirm"));
        passwordConfirmTF.setLabel(new ResourceModel("passwordConfirmTF"));

        final FormComponentLabel passwordConfirmLabel = new SimpleFormComponentLabel("passwordConfirmLabel", passwordConfirmTF);

        form
                .add(passwordConfirmLabel, passwordConfirmTF)
                .add(new EqualPasswordInputValidator(passwordTF, passwordConfirmTF));

        add(form);
    }

    public String getPassword() {
        return password;
    }
}
