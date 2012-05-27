/**
 * Copyright (c) 2008-2010, Peter Major
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the Peter Major nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *  * All advertising materials mentioning features or use of this software
 * must display the following acknowledgement:
 * This product includes software developed by the Kir-Dev Team, Hungary
 * and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Peter Major BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.web.kp.valuation.request.point;

import hu.sch.domain.*;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.SvieMembershipDetailsIcon;
import hu.sch.web.wicket.components.TinyMCEContainer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
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
                    setResponsePage(ValuationDetails.class, new PageParameters("id=" + v.getId()));
                } catch (AlreadyModifiedException ex) {
                    getSession().error("Valaki már módosított az értékelésen, így lehet, hogy a pontokon is!");
                    setResponsePage(ValuationDetails.class, new PageParameters("id=" + valuation.getId()));
                }
            }
        };
        pointRequestsForm.add(new KeepAliveBehavior());

        // Bevitelhez táblázat létrehozása
        ListView<PointRequest> listView = new ListView<PointRequest>("requestList", requestList) {

            // QPA group pontozásvalidátora
            final IValidator<Integer> QpaPontValidator = new RangeValidator<Integer>(0, 100);
            // A többi group pontozásvalidátora
            final IValidator<Integer> pontValidator = new RangeValidator<Integer>(0, 50);
            // QPA group ID-ja

            @Override
            protected void populateItem(ListItem<PointRequest> item) {
                PointRequest pontIgeny = item.getModelObject();
                item.setModel(new CompoundPropertyModel<PointRequest>(pontIgeny));
                final ValidationError validationError = new ValidationError();
                validationError.addMessageKey("err.MinimumPontHiba");

                item.add(new Label("user.name"));
                item.add(new Label("user.nickName"));

                Membership ms = userManager.getMembership(val.getGroupId(),
                        item.getModelObject().getUserId());
                item.add(new SvieMembershipDetailsIcon("user.svie", ms));

                TextField<Integer> pont = new TextField<Integer>("point");
                //csoportfüggő validátor hozzácsatolása
                if (val.getGroupId().longValue() == Group.SCH_QPA) {
                    pont.add(QpaPontValidator);
                } else {
                    pont.add(pontValidator);
                }

                //olyan validátor, ami akkor dob hibát ha 0 és 5 pont között adott meg
                pont.add(new IValidator<Integer>() {

                    @Override
                    public void validate(IValidatable<Integer> arg0) {
                        final Integer pont = arg0.getValue();
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

        final TinyMCEContainer tinyMce = new TinyMCEContainer("principle", new PropertyModel<String>(val, "principle"), true);
        pointRequestsForm.add(tinyMce);

        add(pointRequestsForm);
    }

    private List<PointRequest> prepareRequests(Valuation ert) {
        List<User> members =
                userManager.getCsoporttagokWithoutOregtagok(ert.getGroupId());
        List<PointRequest> pointRequests =
                valuationManager.findPontIgenyekForErtekeles(ert.getId());

        if (pointRequests.size() != members.size()) {
            Set<Long> alreadyAdded =
                    new HashSet<Long>(pointRequests.size());

            for (PointRequest p : pointRequests) {
                alreadyAdded.add(p.getUserId());
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
