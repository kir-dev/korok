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
package hu.sch.web.kp.group.admin;

import hu.sch.domain.Membership;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.kp.group.ShowGroup;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.components.SelectableMembership;
import hu.sch.web.wicket.components.customlinks.ChangePostLink;
import hu.sch.web.wicket.components.tables.MembershipTable;
import hu.sch.web.wicket.components.tables.PanelColumn;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Ezt a panelt látja a user akkor, ha jogosult arra, hogy valakit öregtaggá
 * avasson, vagy töröljön valakit a listáról.
 *
 * @author aldaris
 * @author messo
 * @see MembershipTable
 */
public final class AdminMembershipsPanel extends Panel {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;
    private static Logger log = Logger.getLogger(AdminMembershipsPanel.class);

    public AdminMembershipsPanel(String id, final List<Membership> activeMembers) {
        super(id);

        final List<SelectableMembership> lines =
                new ArrayList<SelectableMembership>(activeMembers.size());
        for (Membership ms : activeMembers) {
            lines.add(new SelectableMembership(ms));
        }

        Form entForm = new Form("form");

        add(entForm);

        entForm.add(new MembershipTable<SelectableMembership>("table", lines, SelectableMembership.class) {

            @Override
            public void onPopulateColumns(List<IColumn<SelectableMembership>> columns) {
                columns.add(new PropertyColumn<SelectableMembership>(new Model<String>("SVIE tag?"),
                        MembershipTable.SORT_BY_SVIE, "membership.user.svieMemberText"));

                columns.add(new PanelColumn<SelectableMembership>("Jogok") {

                    @Override
                    protected Panel getPanel(String id, SelectableMembership obj) {
                        return new ChangePostLink(id, obj.getMembership());
                    }
                });
            }
        }.getDataTable());

        entForm.add(new Button("oldBoyButton") {

            @Override
            public void onSubmit() {
                try {
                    long myId = ((VirSession) getSession()).getUserId();
                    for (SelectableMembership extendedGroup : lines) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            if (!ms.getUser().getId().equals(myId)) {
                                userManager.setMemberToOldBoy(ms);
                            }
                        }
                    }
                    getSession().info("A változások sikeresen mentésre kerültek");
                } catch (Exception ex) {
                    getSession().error("Hiba történt a feldolgozás közben");
                    log.warn("Hiba történt az öregtaggá avatás közben", ex);
                }
                setResponsePage(ShowGroup.class, new PageParameters().add("id", activeMembers.get(0).getGroup().getId()));
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
                    for (SelectableMembership extendedGroup : lines) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            if (!ms.getUser().getId().equals(myId)) {
                                userManager.deleteMembership(ms);
                            }
                        }
                    }
                    getSession().info("A változások sikeresen mentésre kerültek");
                } catch (Exception ex) {
                    getSession().error("Hiba történt a tag törlése közben");
                    log.warn("Hiba történt a tag törlése közben", ex);
                }
                setResponsePage(ShowGroup.class, new PageParameters().add("id", activeMembers.get(0).getGroup().getId()));
            }
        });
        if (activeMembers.isEmpty()) {
            setVisible(false);
        }
    }
}
