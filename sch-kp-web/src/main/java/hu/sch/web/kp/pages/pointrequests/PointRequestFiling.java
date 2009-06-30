/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.pointrequests;

import hu.sch.domain.Valuation;
import hu.sch.domain.User;
import hu.sch.domain.PointRequest;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 *
 * @author hege
 */
public class PointRequestFiling extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    final List<PointRequest> requestList;

    public PointRequestFiling(final Valuation val) {
        setHeaderLabelText("Pontigénylés leadása");
        //TODO jogosultság?!
        //szerintem nem kell ide, mivel nem könyvjelzőzhető az oldal
        requestList = prepareRequests(val);
        initComponents(val);
    }

    public PointRequestFiling(Valuation val, List<PointRequest> pointList) {
        requestList = pointList;
        initComponents(val);
    }

    public void initComponents(final Valuation ert) {
        final Long valuationId = ert.getId();
        setDefaultModel(new CompoundPropertyModel(ert));
        add(new Label("group.name"));
        add(new Label("semester"));
        add(new FeedbackPanel("pagemessages"));

        // Űrlap létrehozása
        Form pointRequestsForm = new Form("pointRequestsForm") {

            @Override
            protected void onSubmit() {
                // pontok tárolása
                valuationManager.pontIgenyekLeadasa(valuationId, requestList);
                getSession().info(getLocalizer().getString("info.PontIgenylesMentve", this));
                setResponsePage(Valuations.class);
                return;
            }
        };

        // Bevitelhez táblázat létrehozása
        ListView listView = new ListView("requestList", requestList) {

            // QPA group pontozásvalidátora
            final IValidator QpaPontValidator = new RangeValidator(0, 100);
            // A többi group pontozásvalidátora
            final IValidator pontValidator = new RangeValidator(0, 50);
            // QPA group ID-ja
            private final long SCH_QPA_ID = 27L;

            @Override
            protected void populateItem(ListItem item) {
                PointRequest pontIgeny = (PointRequest) item.getModelObject();
                item.setModel(new CompoundPropertyModel(pontIgeny));
                final ValidationError validationError = new ValidationError();
                validationError.addMessageKey("err.MinimumPontHiba");

                item.add(new Label("user.name"));
                item.add(new Label("user.nickName"));
                TextField pont = new TextField("point");
                //csoportfüggő validátor hozzácsatolása
                if (ert.getGroup().getId().equals(SCH_QPA_ID)) {
                    pont.add(QpaPontValidator);
                } else {
                    pont.add(pontValidator);
                }

                //olyan validátor, ami akkor dob hibát ha 0 és 5 pont között adott meg
                pont.add(new IValidator() {

                    public void validate(IValidatable arg0) {
                        final Integer pont = (Integer) arg0.getValue();
                        if (0 < pont && pont < 5) {
                            arg0.error(validationError);
                        }
                    }
                });
                item.add(pont);
            }
        };
        listView.setReuseItems(true);
        pointRequestsForm.add(listView);
        add(pointRequestsForm);
    }

    private List<PointRequest> prepareRequests(Valuation ert) {
        List<User> members =
                userManager.getCsoporttagokWithoutOregtagok(ert.getGroup().getId());
        List<PointRequest> pointRequests =
                valuationManager.findPontIgenyekForErtekeles(ert.getId());

        if (pointRequests.size() != members.size()) {
            Set<Long> alreadyAdded =
                    new HashSet<Long>(members.size());

            for (PointRequest p : pointRequests) {
                alreadyAdded.add(p.getUser().getId());
            }

            for (User f : members) {
                if (!alreadyAdded.contains(f.getId())) {
                    pointRequests.add(new PointRequest(f, 0));
                }
            }
        }

        return pointRequests;
    }
}
