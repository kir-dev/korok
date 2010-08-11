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

package hu.sch.web.wicket.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.wizard.CancelButton;
import org.apache.wicket.extensions.wizard.FinishButton;
import org.apache.wicket.extensions.wizard.IDefaultButtonProvider;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.LastButton;
import org.apache.wicket.extensions.wizard.NextButton;
import org.apache.wicket.extensions.wizard.PreviousButton;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardButton;
import org.apache.wicket.extensions.wizard.WizardButtonBar;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * A bar of buttons for wizards utilizing {@link AjaxFormSubmitBehavior}.
 * Az osztály a WICKET-2542-es ticketből származik, viszont amíg ki nem lesz
 * javítva a hibás default gomb probléma, addig sajnos hanyagoljuk használatát :(
 * 
 * @see WizardButtonBar
 */
public class AjaxWizardButtonBar extends Panel implements IDefaultButtonProvider {

    private static final long serialVersionUID = 1L;
    private Wizard wizard;

    /**
     * Construct.
     *
     * @param id
     *            The component id
     * @param wizard
     *            The containing wizard
     */
    public AjaxWizardButtonBar(String id, Wizard wizard) {
        super(id);

        this.wizard = wizard;
        wizard.setOutputMarkupId(true);

        addAjax(new PreviousButton("previous", wizard));
        addAjax(new NextButton("next", wizard));
        addAjax(new LastButton("last", wizard));
        addAjax(new CancelButton("cancel", wizard));
        addAjax(new FinishButton("finish", wizard));
    }

    private void addAjax(final WizardButton button) {
        button.add(new AjaxFormSubmitBehavior("onclick") {

            private static final long serialVersionUID = 1L;

            @Override
            protected CharSequence getEventHandler() {
                AppendingStringBuffer handler = new AppendingStringBuffer();
                handler.append(super.getEventHandler());
                handler.append("; return false;");
                return handler;
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return AjaxWizardButtonBar.this.getAjaxCallDecorator();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.addComponent(wizard);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.addComponent(wizard);
            }
        });

        add(button);
    }

    /**
     * @see org.apache.wicket.extensions.wizard.IDefaultButtonProvider#getDefaultButton(org.apache.wicket.extensions.wizard.IWizardModel)
     */
    @Override
    public IFormSubmittingComponent getDefaultButton(IWizardModel model) {
        if (model.isNextAvailable()) {
            return (Button) get("next");
        } else if (model.isLastAvailable()) {
            return (Button) get("last");
        } else if (model.isLastStep(model.getActiveStep())) {
            return (Button) get("finish");
        }
        return null;
    }

    /**
     *
     * @return call decorator to use or null if none
     */
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        return null;
    }
}
