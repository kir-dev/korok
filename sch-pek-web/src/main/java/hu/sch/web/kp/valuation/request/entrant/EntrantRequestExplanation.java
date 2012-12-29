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
package hu.sch.web.kp.valuation.request.entrant;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.EntrantType;
import hu.sch.domain.Valuation;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.services.exceptions.valuation.NoExplanationException;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author hege
 */
public class EntrantRequestExplanation extends KorokPage {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;

    public EntrantRequestExplanation(final Valuation ert, final List<EntrantRequest> igenyek) {
        List<EntrantRequest> indoklando = kellIndoklas(igenyek);
        setHeaderLabelText("Színes belépők indoklása");
        Form<Valuation> indoklasform = new Form<Valuation>("indoklasform", new Model<Valuation>(ert)) {

            @Override
            protected void onSubmit() {
                final Valuation ert = getModelObject();
                try {
                    Valuation v = valuationManager.updateEntrantRequests(ert, igenyek);
                    getSession().info(getLocalizer().getString("info.BelepoIgenylesMentve", this));
                    setResponsePage(ValuationDetails.class, new PageParameters().add("id", v.getId()));
                } catch (NoExplanationException ex) {
                    getSession().error(getLocalizer().getString("info.BelepoIgenylesNincsIndoklas", this));
                    setResponsePage(new EntrantRequestExplanation(ert, igenyek));
                } catch (AlreadyModifiedException ex) {
                    getSession().error("Valaki már módosított az értékelésen, így lehet, hogy a belépőkön is!");
                    setResponsePage(ValuationDetails.class, new PageParameters().add("id", ert.getId()));
                }
            }
        };
        indoklasform.add(new KeepAliveBehavior());

        indoklasform.add(new ListView<EntrantRequest>("indoklas", indoklando) {

            @Override
            protected void populateItem(ListItem<EntrantRequest> item) {
                item.setModel(new CompoundPropertyModel<EntrantRequest>(item.getModelObject()));
                item.add(new Label("user.name"));
                item.add(new Label("user.nickName"));
                item.add(new Label("entrantType"));
                item.add(new TextArea<String>("valuationText"));
            }
        });

        add(indoklasform);
    }

    private List<EntrantRequest> kellIndoklas(List<EntrantRequest> igenyek) {
        List<EntrantRequest> indoklando = new ArrayList<EntrantRequest>();
        for (EntrantRequest i : igenyek) {
            if (!i.getEntrantType().equals(EntrantType.KDO)) {
                indoklando.add(i);
            }
        }
        return indoklando;
    }
}
