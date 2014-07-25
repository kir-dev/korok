package hu.sch.web.dev;

import hu.sch.web.PhoenixApplication;
import hu.sch.web.authz.SessionBasedAuthorization;
import hu.sch.web.authz.SettableAuthorization;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

public class DevSettingsPage extends WebPage {

    private DevSettingsFormModel model;
    private String status = "";

    public DevSettingsPage() {
        model = new DevSettingsFormModel();
        addSettingsForm();

        add(new Label("statusSpan", new PropertyModel(this, "status")));
    }

    public String getStatus() {
        return status;
    }

    private void addSettingsForm() {
        Form<DevSettingsFormModel> form = new Form<DevSettingsFormModel>("devSettingsForm", new CompoundPropertyModel<>(model)) {

            @Override
            protected void onSubmit() {
                PhoenixApplication app = (PhoenixApplication) getApplication();
                app.setAuthorizationComponent(new SettableAuthorization(model.getUserId()));
                status = "Sikerült! Mostantól a " + model.getUserId() + " id-jú felhasználóval vagy bejelentkezve.";
            }

        };

        form.add(new RequiredTextField("userId"));
        addResetButton(form);
        add(form);
    }

    private void addResetButton(Form<?> form) {
        Button resetButton = new Button("btnDeactivate") {

            @Override
            public void onSubmit() {
                PhoenixApplication app = (PhoenixApplication) getApplication();
                app.setAuthorizationComponent(new SessionBasedAuthorization());
                getSession().invalidate();
                throw new RestartResponseException(app.getHomePage());
            }

        };
        resetButton.setDefaultFormProcessing(false);
        form.add(resetButton);
    }
}
