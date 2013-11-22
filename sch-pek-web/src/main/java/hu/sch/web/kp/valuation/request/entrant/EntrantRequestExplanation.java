package hu.sch.web.kp.valuation.request.entrant;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.Valuation;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.services.exceptions.valuation.NoExplanationException;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author hege
 */
public class EntrantRequestExplanation extends KorokPage {

    @Inject
    ValuationManagerLocal valuationManager;

    public EntrantRequestExplanation(final Valuation ert, final List<EntrantRequest> igenyek) {
        List<EntrantRequest> indoklando = kellIndoklas(igenyek);
        setHeaderLabelText("Színes belépők indoklása");
        Form<Valuation> indoklasform = new Form<Valuation>("indoklasform", new Model<Valuation>(ert)) {

            @Override
            protected void onSubmit() {
                final Valuation ert = getModelObject();
                try {
                    Valuation v = valuationManager.updateEntrantRequests(ert, igenyek);
                    getSession().info(getLocalizer().getString("info.BelepoIgenylesMentve", this));
                    setResponsePage(ValuationDetails.class, new PageParameters().add("id", v.getId()));
                } catch (NoExplanationException ex) {
                    getSession().error(getLocalizer().getString("info.BelepoIgenylesNincsIndoklas", this));
                    setResponsePage(new EntrantRequestExplanation(ert, igenyek));
                } catch (AlreadyModifiedException ex) {
                    getSession().error("Valaki már módosított az értékelésen, így lehet, hogy a belépőkön is!");
                    setResponsePage(ValuationDetails.class, new PageParameters().add("id", ert.getId()));
                }
            }
        };
        indoklasform.add(new KeepAliveBehavior());

        indoklasform.add(new ListView<EntrantRequest>("indoklas", indoklando) {

            @Override
            protected void populateItem(ListItem<EntrantRequest> item) {
                item.setModel(new CompoundPropertyModel<EntrantRequest>(item.getModelObject()));
                item.add(new Label("user.fullName"));
                item.add(new Label("user.nickName"));
                item.add(new Label("entrantType"));
                item.add(new TextArea<String>("valuationText"));
            }
        });

        add(indoklasform);
    }

    private List<EntrantRequest> kellIndoklas(List<EntrantRequest> igenyek) {
        List<EntrantRequest> indoklando = new ArrayList<EntrantRequest>();
        for (EntrantRequest i : igenyek) {
            if (!i.getEntrantType().equals(EntrantType.KDO)) {
                indoklando.add(i);
            }
        }
        return indoklando;
    }
}
