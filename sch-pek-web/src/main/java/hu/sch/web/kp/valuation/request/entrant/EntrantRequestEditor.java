package hu.sch.web.kp.valuation.request.entrant;

import hu.sch.domain.enums.EntrantType;
import hu.sch.domain.user.User;
import hu.sch.domain.*;
import hu.sch.services.GroupManagerLocal;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.services.exceptions.valuation.NoExplanationException;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.kp.valuation.request.Requests;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.SvieMembershipDetailsIcon;
import hu.sch.web.wicket.components.choosers.EntrantTypeChooser;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author hege
 * @author messo
 */
public class EntrantRequestEditor extends Panel {

    @Inject
    ValuationManagerLocal valuationManager;
    @Inject
    UserManagerLocal userManager;
    @Inject
    private MembershipManagerLocal membershipManager;
    @Inject
    private GroupManagerLocal groupManager;

    public EntrantRequestEditor(String id, final Valuation ert) {
        super(id);

        final List<EntrantRequest> igenylista = igenyeketElokeszit(ert);

        Form<Valuation> igform = new Form<Valuation>("igenyekform", new Model<Valuation>(ert)) {
            @Override
            protected void onSubmit() {
                // Van-e olyan, amit indokolni kell
                final Valuation ert = getModelObject();
                for (EntrantRequest belepoIgeny : igenylista) {
                    if (belepoIgeny.getEntrantType() == EntrantType.AB || belepoIgeny.getEntrantType() == EntrantType.KB) {
                        setResponsePage(new EntrantRequestExplanation(ert, igenylista));
                        return;
                    }
                }
                try {
                    Valuation v = valuationManager.updateEntrantRequests(ert, igenylista);
                    getSession().info(getLocalizer().getString("info.BelepoIgenylesMentve", getParent()));
                    setResponsePage(ValuationDetails.class, new PageParameters().add("id", v.getId()));
                } catch (AlreadyModifiedException ex) {
                    getSession().error("Valaki már módosított az értékelésen, így lehet, hogy a belépőkön is!");
                    setResponsePage(ValuationDetails.class, new PageParameters().add("id", ert.getId()));
                } catch (NoExplanationException ex) {
                    // ilyen elvileg itt nem köverkezhet be
                }
            }
        };
        igform.add(new KeepAliveBehavior());
        add(igform);

        igform.add(new ListView<EntrantRequest>("igenyek", igenylista) {
            @Override
            protected void populateItem(ListItem<EntrantRequest> item) {
                item.setDefaultModel(new CompoundPropertyModel<EntrantRequest>(item.getModelObject()));
                item.add(new Label("user.fullName"));
                item.add(new Label("user.nickName"));

                Membership ms = membershipManager.findMembership(ert.getGroupId(),
                        item.getModelObject().getUserId());

                item.add(new SvieMembershipDetailsIcon("user.svie", ms));

                EntrantTypeChooser bt = new EntrantTypeChooser("entrantType");
                bt.setRequired(true);
                item.add(bt);
            }
        });
    }

    private List<EntrantRequest> igenyeketElokeszit(final Valuation ert) {
        final List<User> csoporttagok =
                groupManager.findActiveMembers(ert.getGroupId());
        final List<EntrantRequest> igenyek =
                valuationManager.findBelepoIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.isEmpty()) {
            for (User f : csoporttagok) {
                igenyek.add(new EntrantRequest(f, EntrantType.KDO));
            }
        } else {
            //in case of exitsing request, we need merge if group members are changed
            Requests.cleanOldBoysFromRequests(igenyek, csoporttagok);
            addMissingRequests(igenyek, csoporttagok);
        }

        return igenyek;
    }

    /**
     * Add missing entrantrequest to new active members. (In case of members
     * changed between entrantrequests)
     *
     * @param requests
     * @param actualMembers
     */
    public void addMissingRequests(final List<EntrantRequest> requests, final List<User> actualMembers) {


        final Set<User> usersHasRequest = new HashSet<User>(requests.size());
        for (EntrantRequest request : requests) {
            usersHasRequest.add(request.getUser());
        }

        boolean needReorder = false;
        for (User member : actualMembers) {
            if (!usersHasRequest.contains(member)) {
                requests.add(new EntrantRequest(member, EntrantType.KDO));
                needReorder = true;
            }
        }

        if (needReorder) {
            Collections.sort(requests);
        }
    }
}
