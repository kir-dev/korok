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
package hu.sch.web.kp.svie;

import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.wicket.components.choosers.MembershipTypeChooser;
import hu.sch.web.wicket.components.choosers.SvieStatusChooser;
import hu.sch.web.wicket.components.customlinks.LinkPanel;
import hu.sch.web.wicket.components.customlinks.SvieRegPdfLink;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.kp.KorokPageTemplate;
import hu.sch.web.wicket.components.tables.LinkColumn;
import hu.sch.web.wicket.components.tables.PanelColumn;
import hu.sch.web.wicket.util.SortableUserDataProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public final class SvieUserMgmt extends KorokPageTemplate {

    @EJB(name = "SvieManagerBean")
    SvieManagerLocal svieManager;
    private static Logger log = Logger.getLogger(SvieUserMgmt.class);
    private List<User> users;
    private List<User> filteredUsers;
    private SortableUserDataProvider userProvider;
    private SvieStatus currentFilter;

    public SvieUserMgmt() {
        createNavbarWithSupportId(34);
        if (!isCurrentUserSVIE()) {
            log.warn("Illetéktelen hozzáférési próbálkozás a SVIE beállításokhoz! Felhasználó: "
                    + getSession().getUserId());
            getSession().error("Nem rendelkezel a megfelelő jogosultságokkal!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Felhasználók adminisztrálása");
        users = svieManager.getSvieMembers();
        filteredUsers = new ArrayList<User>(users);

        List<IColumn<User>> columns = new ArrayList<IColumn<User>>();
        columns.add(new PanelColumn<User>("Név", "name") {

            @Override
            protected Panel getPanel(String componentId, User u) {
                return new UserLink(componentId, u);
            }
        });
        columns.add(new PanelColumn<User>("Tagság típusa", "svieMembershipType") {

            @Override
            protected Panel getPanel(String componentId, User u) {
                return new MembershipTypeChooser(componentId, u);
            }
        });
        columns.add(new PanelColumn<User>("Tagság állapota") {

            @Override
            protected Panel getPanel(String componentId, User u) {
                return new SvieStatusChooser(componentId, u);
            }
        });
        columns.add(new PropertyColumn<User>(new Model<String>("Elsődleges kör"), "sviePrimaryMembershipText"));
        columns.add(new LinkColumn<User>("Felvételi kérvény") {

            @Override
            protected boolean isVisible(User user) {
                return !user.getSvieStatus().equals(SvieStatus.ELFOGADVA);
            }

            @Override
            protected LinkPanel getLinkPanel(String componentId, User user) {
                return new SvieRegPdfLink(componentId, user);
            }
        });

        Form form = new Form("svieForm") {

            @Override
            protected void onSubmit() {
                svieManager.updateSvieInfos(filteredUsers);
                getSession().info("A beállítások sikeresen mentésre kerültek");
                setResponsePage(SvieUserMgmt.class);
            }
        };
        userProvider = new SortableUserDataProvider(filteredUsers);
        //azért van változóban, hogy később ha szeretnénk játszadozni a rowperpage-dzsel
        //egyszerűbb legyen.
        final AjaxFallbackDefaultDataTable table =
                new AjaxFallbackDefaultDataTable("table", columns, userProvider, 100);
        table.setOutputMarkupId(true);
        DropDownChoice<SvieStatus> filter =
                new DropDownChoice<SvieStatus>("status",
                new PropertyModel<SvieStatus>(this, "currentFilter"),
                Arrays.asList(SvieStatus.values()));
        filter.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                filteredUsers.clear();
                if (currentFilter == null) {
                    filteredUsers.addAll(users);
                } else {
                    Iterator<User> it = users.iterator();
                    while (it.hasNext()) {
                        User temp = it.next();
                        if (temp.getSvieStatus().equals(currentFilter)) {
                            filteredUsers.add(temp);
                        }
                    }
                }
                userProvider.setUsers(filteredUsers);
                if (target != null) {
                    target.addComponent(table);
                }
            }
        });
        form.add(filter);


        form.add(table);
        add(form);
    }
}
