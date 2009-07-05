/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.consider;

import hu.sch.domain.ConsideredValuation;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ConsiderExplainPage extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;

    public ConsiderExplainPage(final List<ConsideredValuation> underConsider) {
        add(new FeedbackPanel("pagemessages"));
        setHeaderLabelText("Elbírálás indoklása");
        Form considerForm = new Form("considerExplainForm") {

            @Override
            protected void onSubmit() {
                if (valuationManager.ErtekeleseketElbiral(underConsider, getUser())) {
                    getSession().info("Az elbírálás sikeres volt.");
                    setResponsePage(ConsiderPage.class);

                } else {
                    getSession().info("Minden elutasított értékeléshez kell indoklást mellékelni!");
                    setResponsePage(new ConsiderExplainPage(underConsider));
                    return;
                }
            }
        };

        add(considerForm);
        considerForm.add(new ListView<ConsideredValuation>("consideredValuation", underConsider) {

            @Override
            protected void populateItem(ListItem<ConsideredValuation> item) {
                final ConsideredValuation cv = item.getModelObject();
                item.setModel(new CompoundPropertyModel<ConsideredValuation>(cv));
                item.add(new Label("valuation.group.name"));
                item.add(new Label("pointStatus"));
                item.add(new Label("entrantStatus"));
                FormComponent ta = new TextArea("explanation");
                item.add(ta);
            }
        });

    }
}
