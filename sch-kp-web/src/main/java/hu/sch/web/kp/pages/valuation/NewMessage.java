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

import hu.sch.domain.Valuation;
import hu.sch.web.kp.templates.KorokPageTemplate;
import hu.sch.services.ValuationManagerLocal;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
class NewMessage extends KorokPageTemplate {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal ertekelesManager;
    String message = "";

    public NewMessage(final Long valuationId) {
        setHeaderLabelText("Új üzenet küldése");
        Valuation val = ertekelesManager.findErtekelesById(valuationId);
        Form form = new Form("newMessageForm") {

            @Override
            protected void onSubmit() {
                ertekelesManager.ujErtekelesUzenet(valuationId, getUser(), getMessage());
                getSession().info(getLocalizer().getString("info.UzenetMentve", this));
                setResponsePage(new ValuationMessages(valuationId));
            }
        };

        TextArea msgField = new TextArea("message", new PropertyModel(this, "message"));
        msgField.setRequired(true);

        form.add(msgField);
        add(form);
        add(new Label("groupName", val.getGroup().getName()));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
