package hu.sch.web.kp.valuation.request.point;

import hu.sch.domain.*;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.kp.valuation.request.Requests;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.SvieMembershipDetailsIcon;
import hu.sch.web.wicket.components.TinyMCEContainer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 *
 * @author hege
 * @author messo
 */
public class PointRequestEditor extends Panel {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public PointRequestEditor(String id, final Valuation val) {
        super(id);

        final List<PointRequest> requestList = prepareRequests(val);
        final Long valuationId = val.getId();

        // Űrlap létrehozása
        Form<Valuation> pointRequestsForm = new Form<Valuation>("pointRequestsForm", new Model<Valuation>(val)) {
            @Override
            protected void onSubmit() {
                final Valuation valuation = getModelObject();
                try {
                    Valuation v = valuationManager.updateValuation(valuation);
                    // pontok tárolása
                    v = valuationManager.updatePointRequests(v, requestList);
                    getSession().info(getLocalizer().getString("info.PontIgenylesMentve", this));
                    setResponsePage(ValuationDetails.class, new PageParameters().add("id", v.getId()));
                } catch (AlreadyModifiedException ex) {
                    getSession().error("Valaki már módosított az értékelésen, így lehet, hogy a pontokon is!");
                    setResponsePage(ValuationDetails.class, new PageParameters().add("id", valuation.getId()));
                }
            }
        };
        pointRequestsForm.add(new KeepAliveBehavior());

        // Bevitelhez táblázat létrehozása
        ListView<PointRequest> listView = new ListView<PointRequest>("requestList", requestList) {

            @Override
            protected void populateItem(ListItem<PointRequest> item) {
                PointRequest pontIgeny = item.getModelObject();
                item.setModel(new CompoundPropertyModel<PointRequest>(pontIgeny));

                item.add(new Label("user.name"));
                item.add(new Label("user.nickName"));

                Membership ms = userManager.getMembership(val.getGroupId(),
                        item.getModelObject().getUserId());
                item.add(new SvieMembershipDetailsIcon("user.svie", ms));

                TextField<Integer> pont = new TextField<Integer>("point");
                //csoportfüggő validátor hozzácsatolása
                if (val.getGroupId().longValue() == Group.SCH_QPA) {
                    pont.add(RangeValidator.range(5, 100));
                } else {
                    pont.add(RangeValidator.range(5, 50));
                }

                item.add(pont);
            }
        };
        listView.setReuseItems(true);
        pointRequestsForm.add(listView);

        final TinyMCEContainer tinyMce = new TinyMCEContainer("principle", new PropertyModel<String>(val, "principle"), true);
        pointRequestsForm.add(tinyMce);

        add(pointRequestsForm);
    }

    private List<PointRequest> prepareRequests(final Valuation ert) {
        final List<User> members =
                userManager.getCsoporttagokWithoutOregtagok(ert.getGroupId());
        final List<PointRequest> pointRequests =
                valuationManager.findPontIgenyekForErtekeles(ert.getId());

        if (pointRequests.isEmpty()) {
            for (User member : members) {
                pointRequests.add(new PointRequest(member, 0));
            }
        } else {
            //in case of exitsing request, we need merge if group members are changed
            Requests.cleanOldBoysFromRequests(pointRequests, members);
            addMissingRequests(pointRequests, members);
            //requests.add(new PointRequest(member, 0));
        }
        return pointRequests;
    }

    /**
     * Add missing pointrequest to new active members. (In case of members
     * changed between pointrequests)
     *
     * @param requests
     * @param actualMembers
     */
    public void addMissingRequests(final List<PointRequest> requests, final List<User> actualMembers) {


        final Set<User> usersHasRequest = new HashSet<User>(requests.size());
        for (PointRequest request : requests) {
            usersHasRequest.add(request.getUser());
        }

        boolean needReorder = false;
        for (User member : actualMembers) {
            if (!usersHasRequest.contains(member)) {
                requests.add(new PointRequest(member, 0));
                needReorder = true;
            }
        }

        if (needReorder) {
            Collections.sort(requests);
        }
    }
}
