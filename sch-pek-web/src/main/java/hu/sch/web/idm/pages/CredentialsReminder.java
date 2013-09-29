package hu.sch.web.idm.pages;

import hu.sch.services.exceptions.PekEJBException;
import hu.sch.web.kp.KorokPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.EmailAddressValidator;

/**
 * Page for password and username reminder.
 *
 * @author aldaris
 * @author balo
 */
public class CredentialsReminder extends KorokPage {

    private enum Page {

        USERNAME, PASSWORD;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
    //
    private static final String PAGE_PARAM = "p";
    private final Page currentPage;
    //
    private String mail;

    public CredentialsReminder() {
        this(new PageParameters().add(PAGE_PARAM, Page.USERNAME.toString()));
    }

    public CredentialsReminder(final PageParameters params) {
        if (getRemoteUser() != null) {
            getSession().error(getString("err.ReminderAlreadySignedIn"));
            throw new RestartResponseException(getApplication().getHomePage());
        }

        Page p;
        try {
            p = Page.valueOf(params.get(PAGE_PARAM).toString().toUpperCase());
        } catch (IllegalArgumentException ex) {
            p = Page.USERNAME; //default
        }

        currentPage = p;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setHeaderLabelText(getString("headerLabel." + currentPage));
        add(new Label("notifyLabel", getString("notifyLabel." + currentPage)));

        Form<Void> reminderForm = new StatelessForm<Void>("reminderForm") {
            @Override
            protected void onSubmit() {
                try {
                    if (sendReminder(currentPage)) {
                        info(getString("info.ReminderSent"));
                    } else {
                        error(getString("err.MailError"));
                    }
                } catch (PekEJBException ex) {
                    error(new StringResourceModel(ex.getErrorCode().getMessageKey(), null, ex.getParameters()));
                }
            }
        };

        final TextField<String> mailTF =
                new RequiredTextField<>("mail", new PropertyModel<String>(this, "mail"));
        mailTF.add(EmailAddressValidator.getInstance());
        reminderForm.add(mailTF);
        add(reminderForm);
    }

    private boolean sendReminder(final Page currentPage) throws PekEJBException {
        switch (currentPage) {
            case PASSWORD:
                return userManager.sendLostPasswordChangeLink(mail);
            default:
                return userManager.sendUserNameReminder(mail);
        }
    }
}
