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
package hu.sch.web.kp.pages.consider;

import hu.sch.domain.ConsideredValuation;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 */
public class ConsiderExplainPage extends SecuredPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;

    public ConsiderExplainPage(final List<ConsideredValuation> underConsider) {
        add(new FeedbackPanel("pagemessages"));
        setHeaderLabelText("Elbírálás indoklása");

        Form considerForm = new Form("considerExplainForm") {

            @Override
            protected void onSubmit() {
                if (valuationManager.ErtekeleseketElbiral(underConsider, getUser())) {
                    getSession().info("Az elbírálás sikeres volt.");
                    setResponsePage(ConsiderPage.class);
                } else {
                    getSession().error("Minden elutasított értékeléshez kell indoklást mellékelni!");
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
