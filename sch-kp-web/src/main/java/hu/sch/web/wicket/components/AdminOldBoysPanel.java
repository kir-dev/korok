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
package hu.sch.web.wicket.components;

import hu.sch.web.wicket.components.tables.MembershipTable;
import hu.sch.domain.Membership;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.kp.group.ShowGroup;
import hu.sch.web.wicket.components.tables.DateIntervalPropertyColumn;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * Ezt a panelt látja a user akkor, ha jogosult arra, hogy aktiválhassa az öregtagokat.
 * A körítésen (lásd markup) kívül lehet rendezgetni a táblázatot.
 *
 * @author aldaris
 * @author messo
 * @see MembershipTable
 */
public final class AdminOldBoysPanel extends Panel {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    private static Logger log = Logger.getLogger(AdminOldBoysPanel.class);
    List<SelectableMembership> lines;

    public AdminOldBoysPanel(String id, final List<Membership> inactiveMembers) {
        super(id);

        Form form;

        lines = new ArrayList<SelectableMembership>(inactiveMembers.size());
        for (Membership ms : inactiveMembers) {
            lines.add(new SelectableMembership(ms));
        }

        add(form = new Form("oldForm") {

            @Override
            public void onSubmit() {
                try {
                    for (SelectableMembership extendedGroup : lines) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            userManager.setOldBoyToActive(ms);
                        }
                    }
                    getSession().info("A változások sikeresen mentésre kerültek");
                } catch (Exception ex) {
                    getSession().error("Hiba történt a feldolgozás közben");
                    log.warn("Hiba történt az öregtag visszaállításakor", ex);
                }
                setResponsePage(ShowGroup.class, new PageParameters("id=" + inactiveMembers.get(0).getGroup().getId()));
            }
            
        });

        form.add(new MembershipTable<SelectableMembership>("table", lines, SelectableMembership.class) {

            @Override
            public void onPopulateColumns(List<IColumn<SelectableMembership>> columns) {
                columns.add(new DateIntervalPropertyColumn<SelectableMembership>(
                        new Model<String>("Tagság ideje"), "membershipStartEnd", "membership.start", "membership.end"));
            }
        }.getDataTable());
    }
}
