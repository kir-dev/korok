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

package hu.sch.web.kp.svie;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.wicket.components.customlinks.GroupLink;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.components.CheckBoxHolder;
import hu.sch.web.wicket.components.SvieDelegateNumberField;
import hu.sch.web.wicket.components.tables.PanelColumn;
import hu.sch.web.wicket.util.SortableGroupDataProvider;
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
public final class SvieGroupMgmt extends KorokPage {

    @EJB(name = "SvieManagerBean")
    private SvieManagerLocal svieManager;
    private static Logger log = Logger.getLogger(SvieUserMgmt.class);
    private List<Group> groups;
    private List<Group> filteredGroups;
    private String currentFilter;
    private SortableGroupDataProvider groupProvider;

    public SvieGroupMgmt() {
        createNavbarWithSupportId(34);
        if (!isCurrentUserSVIE()) {
            log.warn("Illetéktelen hozzáférési próbálkozás a SVIE beállításokhoz! Felhasználó: "
                    + getSession().getUserId());
            getSession().error("Nem rendelkezel a megfelelő jogosultságokkal!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Csoportok adminisztrálása");
        groups = userManager.getAllGroupsWithCount();
        filteredGroups = new ArrayList<Group>(groups);

        List<IColumn<Group>> columns = new ArrayList<IColumn<Group>>();
        columns.add(new PanelColumn<Group>("Név", "name") {

            @Override
            protected Panel getPanel(String componentId, Group g) {
                return new GroupLink(componentId, g);
            }
        });
        columns.add(new PanelColumn<Group>("Körvezető neve") {

            @Override
            protected Panel getPanel(String componentId, Group g) {
                // FIXME: nagyon gány, soronként 1 lekérdezés!!!
                User korvezeto = userManager.getGroupLeaderForGroup(g.getId());
                return new UserLink(componentId, korvezeto);
            }
        });
        columns.add(new PropertyColumn<Group>(new Model<String>("Elsődleges tagok száma"), "numberOfPrimaryMembers"));
        columns.add(new PanelColumn<Group>("SVIE tag?") {

            @Override
            protected Panel getPanel(String componentId, Group g) {
                return new CheckBoxHolder<Group>(componentId, g, "isSvie");
            }
        });
        columns.add(new PanelColumn<Group>("Küldöttek száma") {

            @Override
            protected Panel getPanel(String componentId, Group g) {
                return new SvieDelegateNumberField(componentId, g);
            }
        });

        Form form = new Form("svieForm") {

            @Override
            protected void onSubmit() {
                svieManager.updateSvieGroupInfos(groups);
                getSession().info("A beállítások sikeresen mentésre kerültek");
                setResponsePage(SvieGroupMgmt.class);
            }
        };

        groupProvider = new SortableGroupDataProvider(filteredGroups);
        //azért van változóban, hogy később ha szeretnénk játszadozni a rowperpage-dzsel
        //egyszerűbb legyen.
        final AjaxFallbackDefaultDataTable table =
                new AjaxFallbackDefaultDataTable("table", columns, groupProvider, 100);
        table.setOutputMarkupId(true);

        DropDownChoice<String> filter =
                new DropDownChoice<String>("status",
                new PropertyModel<String>(this, "currentFilter"),
                Arrays.asList(new String[]{"svie tag", "nem svie tag"}));
        filter.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                filteredGroups.clear();
                if (currentFilter == null) {
                    filteredGroups.addAll(groups);
                } else {
                    Iterator<Group> it = groups.iterator();
                    while (it.hasNext()) {
                        Group temp = it.next();
                        if (currentFilter.equals("svie tag")) {
                            if (temp.getIsSvie()) {
                                filteredGroups.add(temp);
                            }
                        } else if (!temp.getIsSvie()) {
                            filteredGroups.add(temp);
                        }
                    }
                }

                groupProvider.setGroups(filteredGroups);
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
