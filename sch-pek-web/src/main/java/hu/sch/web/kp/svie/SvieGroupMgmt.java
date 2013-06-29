package hu.sch.web.kp.svie;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.wicket.components.CheckBoxHolder;
import hu.sch.web.wicket.components.SvieDelegateNumberField;
import hu.sch.web.wicket.components.customlinks.GroupLink;
import hu.sch.web.wicket.components.customlinks.UserLink;
import hu.sch.web.wicket.components.tables.PanelColumn;
import hu.sch.web.wicket.util.SortableGroupDataProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aldaris
 */
public final class SvieGroupMgmt extends KorokPage {

    @EJB(name = "SvieManagerBean")
    private SvieManagerLocal svieManager;
    private static Logger log = LoggerFactory.getLogger(SvieUserMgmt.class);
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

        List<IColumn<Group, String>> columns = new ArrayList<IColumn<Group, String>>();
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
        columns.add(new PropertyColumn<Group, String>(new Model<String>("Elsődleges tagok száma"), "numberOfPrimaryMembers"));
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
                    target.add(table);
                }
            }
        });

        form.add(filter);

        form.add(table);
        add(form);
    }
}
