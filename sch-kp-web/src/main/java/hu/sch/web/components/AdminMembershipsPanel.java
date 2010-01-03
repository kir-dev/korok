/**
 * Copyright (c) 2009, Peter Major
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
package hu.sch.web.components;

import hu.sch.domain.Membership;
import hu.sch.web.components.EditEntitlementsForm.ExtendedGroup;
import hu.sch.web.components.customlinks.ChangePostLink;
import hu.sch.web.kp.pages.group.ShowGroup;
import hu.sch.web.session.VirSession;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author aldaris
 */
public final class AdminMembershipsPanel extends Panel {

    private static Logger log = Logger.getLogger(AdminMembershipsPanel.class);

    public AdminMembershipsPanel(String id, final List<Membership> activeMembers) {
        super(id);
        //TODO: szebbé tenni
        final EditEntitlementsForm entForm = new EditEntitlementsForm("form", activeMembers) {

            @Override
            public void onPopulateItem(ListItem<ExtendedGroup> item, Membership ms) {
                item.add(new Label("rights",
                        getConverter(List.class).convertToString(ms.getPosts(), getLocale())));
                item.add(new ChangePostLink("postLink", item.getModelObject().getMembership()));
            }

            @Override
            public void onSubmit() {
            }
        };
        entForm.add(new Button("oldBoyButton") {

            @Override
            public void onSubmit() {
                try {
                    long myId = ((VirSession) getSession()).getUserId();
                    for (ExtendedGroup extendedGroup : entForm.getLines()) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            if (!ms.getUser().getId().equals(myId)) {
                                entForm.userManager.setMemberToOldBoy(ms);
                            }
                        }
                    }
                    getSession().info("A változások sikeresen mentésre kerültek");
                } catch (Exception ex) {
                    getSession().error("Hiba történt a feldolgozás közben");
                    log.warn("Hiba történt az öregtaggá avatás közben", ex);
                }
                setResponsePage(ShowGroup.class, new PageParameters("id=" + activeMembers.get(0).getGroup().getId()));
            }
        });

        entForm.add(new Button("eraseButton") {

            @Override
            protected String getOnClickScript() {
                return "return confirm('Ezzel a művelettel végérvényesen eltűnnek az emberek a körből.\\nBiztosan szeretnéd törölni ezeket a tagokat?')";
            }

            @Override
            public void onSubmit() {
                try {
                    long myId = ((VirSession) getSession()).getUserId();
                    for (ExtendedGroup extendedGroup : entForm.getLines()) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            if (!ms.getUser().getId().equals(myId)) {
                                entForm.userManager.deleteMembership(ms);
                            }
                        }
                    }
                    getSession().info("A változások sikeresen mentésre kerültek");
                } catch (Exception ex) {
                    getSession().error("Hiba történt a tag törlése közben");
                    log.warn("Hiba történt a tag törlése közben", ex);
                }
                setResponsePage(ShowGroup.class, new PageParameters("id=" + activeMembers.get(0).getGroup().getId()));
            }
        });
        add(entForm);
    }
}
