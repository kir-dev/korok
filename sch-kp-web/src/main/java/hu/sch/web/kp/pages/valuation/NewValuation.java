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
package hu.sch.web.kp.pages.valuation;

import hu.sch.domain.Group;
import hu.sch.web.kp.templates.KorokPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.TinyMCEContainer;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.StringValueConversionException;

/**
 *
 * @author hege
 */
public class NewValuation extends KorokPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    private final Group group;
    private String valuationText;
    private String principle;

    public NewValuation(PageParameters params) {
        Long groupId;
        try {
            groupId = params.getLong("id");
        } catch (StringValueConversionException svce) {
            getSession().error("Hibás paraméter!");
            throw new RestartResponseException(getApplication().getHomePage());
        }
        if (groupId != null) {
            group = userManager.findGroupById(groupId);
        } else {
            group = null;
        }
        if (group == null) {
            getSession().error("Nincs csoport kiválasztva");
            throw new RestartResponseException(Valuations.class);
        }
        setHeaderLabelText(group.getName());
        FeedbackPanel feedbackPanel = new FeedbackPanel("pagemessages");
        add(feedbackPanel);
        if (!valuationManager.isErtekelesLeadhato(group)) {
            getSession().info(getLocalizer().getString("err.UjErtekelesNemAdhatoLe", this));
            setResponsePage(Valuations.class);
            return;
        }
        Form<Void> newValuationForm = new Form<Void>("newValuationForm") {

            @Override
            protected void onSubmit() {
                valuationManager.addNewValuation(group, getUser(), valuationText, principle);
                getSession().info(getLocalizer().getString("info.ErtekelesMentve", this));
                setResponsePage(Valuations.class);
            }
        };
        newValuationForm.add(new KeepAliveBehavior());

        TinyMCEContainer tinyMce = new TinyMCEContainer("valuationText", new PropertyModel<String>(this, "valuationText"), true);
        newValuationForm.add(tinyMce);
        TinyMCEContainer principleMce = new TinyMCEContainer("principle", new PropertyModel<String>(this, "principle"), false);
        newValuationForm.add(principleMce);
        add(newValuationForm);
    }
}
