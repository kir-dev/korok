package hu.sch.web.kp.valuation.message;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.ValuationMessage;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.kp.KorokPage;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author hege
 * @author messo
 */
class NewMessage extends KorokPage {

    @Inject
    ValuationManagerLocal valuationManager;
    private String message = "";

    public NewMessage(final Group group, final Semester semester) {
        setHeaderLabelText("Új üzenet küldése");
        add(new Label("groupName", group.getName()));

        ValuationMessage vm = new ValuationMessage();
        vm.setGroup(group);
        vm.setSender(getUser());
        vm.setSemester(semester);
        setDefaultModel(new CompoundPropertyModel<ValuationMessage>(vm));

        Form<ValuationMessage> form = new Form<ValuationMessage>("newMessageForm",
                new Model<ValuationMessage>(vm)) {

            @Override
            protected void onSubmit() {
                valuationManager.addNewMessage(getModelObject());
                getSession().info(getLocalizer().getString("info.UzenetMentve", this));
                setResponsePage(ValuationMessages.class, new PageParameters().add("gid", group.getId().toString()).
                        add("sid", semester.getId()));
            }
        };
        add(form);

        form.add(new TextArea("message").setRequired(true));
    }
}
