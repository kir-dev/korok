package hu.sch.web.kp.valuation;

import hu.sch.domain.Group;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.PekEJBException;
import hu.sch.services.exceptions.PekErrorCode;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.TinyMCEContainer;
import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
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

    private static final JavaScriptResourceReference NEW_VALUATION_JS = new JavaScriptResourceReference(NewValuation.class, "NewValuation.js");

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
                try {
                    valuationManager.addNewValuation(group, getUser(), valuationText, principle);
                } catch (PekEJBException ex) {
                    // on duplicate craete error do nothing
                    if (ex.getErrorCode() != PekErrorCode.DATABASE_CREATE_VALUATION_DUPLICATE) {
                        getSession().error(getLocalizer().getString("err.ErtekelesSikertelenMentes", this));
                        return;
                    }
                }
                getSession().info(getLocalizer().getString("info.ErtekelesMentve", this));
                setResponsePage(Valuations.class);
            }
        };
        newValuationForm.add(new KeepAliveBehavior());

        newValuationForm.add(new TinyMCEContainer("valuationText", new PropertyModel<String>(this, "valuationText"), true));
        newValuationForm.add(new TinyMCEContainer("principle", new PropertyModel<String>(this, "principle"), true));
        add(newValuationForm);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(getApplication().getJavaScriptLibrarySettings().getJQueryReference()));
        response.render(JavaScriptReferenceHeaderItem.forReference(NEW_VALUATION_JS));

        super.renderHead(response);
    }
}
