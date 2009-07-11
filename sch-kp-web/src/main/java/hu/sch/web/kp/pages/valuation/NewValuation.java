/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.Group;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class NewValuation extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    String valuationText = "";

    public NewValuation() {
        final Group group = getGroup();
        if (group == null) {
            getSession().error("Nincs csoport kiválasztva");
            throw new RestartResponseException(Valuations.class);
        }
        setHeaderLabelText(group.getName());
        FeedbackPanel feedbackPanel = new FeedbackPanel("pagemessages");
        add(feedbackPanel);
        if (!valuationManager.isErtekelesLeadhato(group)) {
            getSession().info(getLocalizer().getString("err.UjErtekelesNemAdhatoLe", this));
            setResponsePage(Valuations.class);
            return;
        }
        Form newValuationForm = new Form("newValuationForm") {

            @Override
            protected void onSubmit() {
                if (getValuationText().isEmpty()) {
                    getSession().error(getLocalizer().getString("err.NincsBeszamolo", this));
                    return;
                }
                valuationManager.ujErtekeles(group, getUser(), getValuationText());
                getSession().info(getLocalizer().getString("info.ErtekelesMentve", this));
                setResponsePage(Valuations.class);
                return;
            }
        };

        TextArea valuationTextArea = new TextArea("valuationText", new PropertyModel(this, "valuationText"));
        valuationTextArea.setRequired(true);

        newValuationForm.add(valuationTextArea);
        add(newValuationForm);
    }

    public String getValuationText() {
        return valuationText;
    }

    public void setValuationTextArea(String valuationText) {
        this.valuationText = valuationText;
    }
}
