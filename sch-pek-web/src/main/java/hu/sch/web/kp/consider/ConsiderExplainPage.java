package hu.sch.web.kp.consider;

import hu.sch.domain.ConsideredValuation;
import hu.sch.services.PointHistoryManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.services.exceptions.valuation.NoExplanationException;
import hu.sch.services.exceptions.valuation.NothingChangedException;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ConsiderExplainPage extends KorokPage {

    @Inject
    private ValuationManagerLocal valuationManager;

    @Inject
    private PointHistoryManagerLocal pointHistoryManager;

    public ConsiderExplainPage(final List<ConsideredValuation> underConsider) {
        setHeaderLabelText("Elbírálás indoklása");

        Form considerForm = new Form("considerExplainForm") {

            @Override
            protected void onSubmit() {
                try {
                    valuationManager.considerValuations(underConsider);
                    pointHistoryManager.generateForSemesterAsync(getSemester());
                    getSession().info("Az elbírálás sikeres volt.");
                    setResponsePage(ConsiderPage.class);
                } catch (NoExplanationException ex) {
                    getSession().error("Minden elutasított értékeléshez kell indoklást mellékelni!");
                } catch (NothingChangedException ex) {
                    getSession().error("Valamelyik értékelésen nem változtattál semmit, akkor azt miért akarod elbírálni?");
                } catch (AlreadyModifiedException ex) {
                    getSession().error("Valaki már módosított az egyik értékelésen!");
                }
            }
        };
        considerForm.add(new KeepAliveBehavior());

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

        add(considerForm);
    }
}
