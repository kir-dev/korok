package hu.sch.web.kp.valuation;

import hu.sch.domain.Group;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.TinyMCEContainer;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 *
 * @author hege
 */
public class NewValuation extends KorokPage {

    @Inject
    ValuationManagerLocal valuationManager;
    private String valuationText;
    private String principle;

    public NewValuation(PageParameters params) {
        final Group group;
        Long groupId = null;
        try {
            groupId = params.get("id").toLong();
        } catch (StringValueConversionException ex) {
        }

        if (groupId == null || (group = groupManager.findGroupById(groupId)) == null) {
            getSession().error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        if (!isUserGroupLeader(group)) {
            // csak körvezető adhat le új értékelést
            getSession().error(getLocalizer().getString("err.NincsJog", null));
            throw new RestartResponseException(getApplication().getHomePage());
        }
        if (!valuationManager.isErtekelesLeadhato(group)) {
            getSession().info(getLocalizer().getString("err.UjErtekelesNemAdhatoLe", this));
            setResponsePage(Valuations.class);
            return;
        }

        setHeaderLabelText(group.getName());
        Form<Void> newValuationForm = new Form<Void>("newValuationForm") {

            @Override
            protected void onSubmit() {
                valuationManager.addNewValuation(group, getUser(), valuationText, principle);
                getSession().info(getLocalizer().getString("info.ErtekelesMentve", this));
                setResponsePage(Valuations.class);
            }
        };
        newValuationForm.add(new KeepAliveBehavior());

        newValuationForm.add(new TinyMCEContainer("valuationText", new PropertyModel<String>(this, "valuationText"), true));
        newValuationForm.add(new TinyMCEContainer("principle", new PropertyModel<String>(this, "principle"), true));
        add(newValuationForm);
    }
}
