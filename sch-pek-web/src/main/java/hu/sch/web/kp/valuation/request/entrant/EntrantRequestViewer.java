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
import hu.sch.domain.User;
import hu.sch.domain.Valuation;
import hu.sch.services.UserManagerLocal;
import hu.sch.services.ValuationManagerLocal;
import hu.sch.web.wicket.components.customlinks.UserLink;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author hege
 * @author messo
 */
public class EntrantRequestViewer extends Panel {

    @EJB(name = "ValuationManagerBean")
    ValuationManagerLocal ertekelesManager;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public EntrantRequestViewer(String id, final Valuation ert) {
        super(id);

        add(new ListView<EntrantRequest>("requests", igenyeketElokeszit(ert)) {

            @Override
            protected void populateItem(ListItem<EntrantRequest> item) {
                final EntrantRequest b = item.getModelObject();
                item.setModel(new CompoundPropertyModel<EntrantRequest>(b));
                
                item.add(new UserLink("userLink", b.getUser()));
                item.add(new Label("user.nickName"));
                item.add(new Label("entrantType"));
                item.add(new Label("valuationText"));
            }
        });
    }

    private List<EntrantRequest> igenyeketElokeszit(Valuation ert) {
        List<User> csoporttagok = userManager.getCsoporttagokWithoutOregtagok(ert.getGroup().getId());
        List<EntrantRequest> igenyek = ertekelesManager.findBelepoIgenyekForErtekeles(ert.getId());

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
                        if (igeny.getUser().getId().equals(csoporttag.getId())) {
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
