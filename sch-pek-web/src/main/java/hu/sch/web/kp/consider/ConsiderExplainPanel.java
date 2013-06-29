package hu.sch.web.kp.consider;

import hu.sch.domain.ConsideredValuation;
import hu.sch.web.kp.valuation.message.ValuationMessages;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.choosers.ValuationStatusChooser;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * Elbíráló panel (pont/belépő elfogadás/elutasítás, indoklással)
 *
 * @author  messo
 * @since   2.3.1
 */
public abstract class ConsiderExplainPanel extends Panel {

    ConsideredValuation underConsider;

    public ConsiderExplainPanel(String id, ConsideredValuation underConsider) {
        super(id);
        this.underConsider = underConsider;

        initComponents();
    }

    private void initComponents() {
        Form<ConsideredValuation> considerForm;

        // Mentés
        add(considerForm = new Form<ConsideredValuation>("considerExplainForm",
                new CompoundPropertyModel<ConsideredValuation>(underConsider)) {

            @Override
            protected void onSubmit() {
                ConsiderExplainPanel.this.onSubmit(underConsider);
            }
        });
        considerForm.add(new KeepAliveBehavior());

        // Üzenetek megtekintése
        considerForm.add(ValuationMessages.getLink("messages", underConsider.getValuation()));

        // Elbírálás - indoklás
        considerForm.add(new ValuationStatusChooser("pointStatus"));
        considerForm.add(new ValuationStatusChooser("entrantStatus"));
        considerForm.add(new TextArea<String>("explanation"));
    }

    public abstract void onSubmit(ConsideredValuation underConsider);
}
