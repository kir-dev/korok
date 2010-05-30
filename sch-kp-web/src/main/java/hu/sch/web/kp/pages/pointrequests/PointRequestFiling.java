/**
 * Copyright (c) 2009-2010, Peter Major
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
package hu.sch.web.kp.pages.pointrequests;

import hu.sch.domain.Valuation;
import hu.sch.domain.User;
import hu.sch.domain.PointRequest;
import hu.sch.web.kp.pages.valuation.Valuations;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
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

        pointRequestsForm.add(new KeepAliveBehavior());

        // Bevitelhez táblázat létrehozása
        ListView<PointRequest> listView = new ListView<PointRequest>("requestList", requestList) {

            // QPA group pontozásvalidátora
            final IValidator<Integer> QpaPontValidator = new RangeValidator<Integer>(0, 100);
            // A többi group pontozásvalidátora
            final IValidator<Integer> pontValidator = new RangeValidator<Integer>(0, 50);
            // QPA group ID-ja
            private final long SCH_QPA_ID = 27L;

            @Override
            protected void populateItem(ListItem<PointRequest> item) {
                PointRequest pontIgeny = item.getModelObject();
                item.setModel(new CompoundPropertyModel<PointRequest>(pontIgeny));
                final ValidationError validationError = new ValidationError();
                validationError.addMessageKey("err.MinimumPontHiba");

                item.add(new Label("user.name"));
                item.add(new Label("user.nickName"));
                TextField<Integer> pont = new TextField<Integer>("point");
                //csoportfüggő validátor hozzácsatolása
                if (ert.getGroup().getId().equals(SCH_QPA_ID)) {
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
        add(pointRequestsForm);
    }

    private List<PointRequest> prepareRequests(Valuation ert) {
        List<User> members =
                userManager.getCsoporttagokWithoutOregtagok(ert.getGroup().getId());
        List<PointRequest> pointRequests =
                valuationManager.findPontIgenyekForErtekeles(ert.getId());

        if (pointRequests.size() != members.size()) {
            Set<Long> alreadyAdded =
                    new HashSet<Long>(pointRequests.size());

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
