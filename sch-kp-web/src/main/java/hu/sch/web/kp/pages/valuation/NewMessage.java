/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.Valuation;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
class NewMessage extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ValuationManagerLocal ertekelesManager;
    String message = "";

    public NewMessage(final Long valuationId) {
        setHeaderLabelText("Új üzenet küldése");
        Valuation val = ertekelesManager.findErtekelesById(valuationId);
        Form form = new Form("newMessageForm") {

            @Override
            protected void onSubmit() {
                ertekelesManager.ujErtekelesUzenet(valuationId, getUser(), getMessage());
                getSession().info(getLocalizer().getString("info.UzenetMentve", this));
                setResponsePage(new ValuationMessages(valuationId));
            }
        };

        TextArea msgField = new TextArea("message", new PropertyModel(this, "message"));
        msgField.setRequired(true);

        form.add(msgField);
        add(form);
        add(new Label("groupName", val.getGroup().getName()));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
