/**
 * Copyright (c) 2008-2010, Peter Major All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the Peter Major nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission. * All advertising
 * materials mentioning features or use of this software must display the
 * following acknowledgement: This product includes software developed by the
 * Kir-Dev Team, Hungary and its contributors.
 *
 * THIS SOFTWARE IS PROVIDED BY Peter Major ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL Peter Major BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package hu.sch.web.kp.valuation.request.entrant;

import hu.sch.domain.*;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.services.exceptions.valuation.AlreadyModifiedException;
import hu.sch.services.exceptions.valuation.NoExplanationException;
import hu.sch.web.kp.valuation.ValuationDetails;
import hu.sch.web.wicket.behaviors.KeepAliveBehavior;
import hu.sch.web.wicket.components.SvieMembershipDetailsIcon;
import hu.sch.web.wicket.components.choosers.EntrantTypeChooser;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author hege
 * @author messo
 */
public class EntrantRequestEditor extends Panel {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal valuationManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

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
                    setResponsePage(ValuationDetails.class, new PageParameters("id=" + v.getId()));
                } catch (AlreadyModifiedException ex) {
                    getSession().error("Valaki már módosított az értékelésen, így lehet, hogy a belépőkön is!");
                    setResponsePage(ValuationDetails.class, new PageParameters("id=" + ert.getId()));
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
                item.add(new Label("user.name"));
                item.add(new Label("user.nickName"));

                Membership ms = userManager.getMembership(ert.getGroupId(),
                        item.getModelObject().getUserId());
                item.add(new SvieMembershipDetailsIcon("user.svie", ms));

                EntrantTypeChooser bt = new EntrantTypeChooser("entrantType");
                bt.setRequired(true);
                item.add(bt);
            }
        });
    }

    private List<EntrantRequest> igenyeketElokeszit(Valuation ert) {
        List<User> csoporttagok = userManager.getCsoporttagokWithoutOregtagok(ert.getGroupId());
        List<EntrantRequest> igenyek = valuationManager.findBelepoIgenyekForErtekeles(ert.getId());

        //tagok és igények összefésülése
        if (igenyek.isEmpty()) {
            for (User f : csoporttagok) {
                igenyek.add(new EntrantRequest(f, EntrantType.KDO));
            }
        } else {

            //tényleges összefésülés
            boolean szerepel = false;
            if (igenyek.size() != csoporttagok.size()) {
                for (User csoporttag : csoporttagok) {
                    szerepel = false;
                    for (EntrantRequest igeny : igenyek) {
                        if (igeny.getUserId().equals(csoporttag.getId())) {
                            szerepel = true;
                            break;
                        }
                    }
                    if (!szerepel) {
                        igenyek.add(new EntrantRequest(csoporttag, EntrantType.KDO));
                    }
                }
            }
        }

        return igenyek;
    }
}
