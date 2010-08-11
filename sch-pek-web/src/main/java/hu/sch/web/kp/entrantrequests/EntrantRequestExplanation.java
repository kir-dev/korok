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

package hu.sch.web.kp.entrantrequests;

import hu.sch.domain.EntrantRequest;
import hu.sch.domain.EntrantType;
import hu.sch.domain.Valuation;
import hu.sch.web.kp.valuation.Valuations;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.util.ListDataProviderCompoundPropertyModelImpl;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

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
        Form indoklasform = new Form("indoklasform") {

            @Override
            protected void onSubmit() {

                if (valuationManager.belepoIgenyekLeadasa(ert.getId(), igenyek)) {
                    getSession().info(getLocalizer().getString("info.BelepoIgenylesMentve", this));
                    //getSession().info("Belépőigények elmentve");
                    setResponsePage(Valuations.class);
                    return;
                } else {
                    getSession().error(getLocalizer().getString("info.BelepoIgenylesNincsIndoklas", this));
                    setResponsePage(new EntrantRequestExplanation(ert, igenyek));
                    return;
                }


            }
        };
        indoklasform.add(new KeepAliveBehavior());

        DataView<EntrantRequest> dview = new DataView<EntrantRequest>("indoklas", new ListDataProviderCompoundPropertyModelImpl<EntrantRequest>(indoklando)) {

            @Override
            protected void populateItem(Item<EntrantRequest> item) {
                item.add(new Label("user.name"));
                item.add(new Label("user.nickName"));
                item.add(new Label("entrantType"));
                TextArea<String> textArea = new TextArea<String>("valuationText");

                item.add(textArea);
            }
        };

        indoklasform.add(dview);
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
