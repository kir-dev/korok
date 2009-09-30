/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.svie;

import hu.sch.domain.SvieStatus;
import hu.sch.domain.User;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.components.MembershipTypeChooser;
import hu.sch.web.components.SvieStatusChooser;
import hu.sch.web.components.customlinks.SvieRegPdfLink;
import hu.sch.web.components.customlinks.UserLink;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.SortableUserDataProvider;
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
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author aldaris
 */
public final class SvieUserMgmt extends SecuredPageTemplate {

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
            log.warn("Illetéktelen hozzáférési próbálkozás a SVIE beállításokhoz! Felhasználó: " +
                    getSession().getUserId());
            getSession().error("Nem rendelkezel a megfelelő jogosultságokkal!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Felhasználók adminisztrálása");
        add(new FeedbackPanel("pagemessages"));
        users = svieManager.getSvieMembers();
        filteredUsers = new ArrayList<User>(users);

        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add(new AbstractColumn<User>(new Model<String>("Név"), "name") {

            public void populateItem(Item<ICellPopulator<User>> cellItem, String componentId, IModel<User> rowModel) {
                cellItem.add(new UserLink(componentId, rowModel.getObject()));
            }
        });
        columns.add(new AbstractColumn<User>(new Model<String>("Tagság típusa"), "svieMembershipType") {

            public void populateItem(Item<ICellPopulator<User>> cellItem, String componentId, IModel<User> rowModel) {
                cellItem.add(new MembershipTypeChooser(componentId, rowModel.getObject()));
            }
        });
        columns.add(new AbstractColumn<User>(new Model<String>("Tagság állapota")) {

            public void populateItem(Item<ICellPopulator<User>> cellItem, String componentId, IModel<User> rowModel) {
                cellItem.add(new SvieStatusChooser(componentId, rowModel.getObject()));
            }
        });
        columns.add(new AbstractColumn<User>(new Model<String>("Felvételi kérvény")) {

            public void populateItem(Item<ICellPopulator<User>> cellItem, String componentId,
                    IModel<User> model) {
                User user = model.getObject();
                SvieRegPdfLink svieRegLink = new SvieRegPdfLink(componentId, user);
                cellItem.add(svieRegLink);
                if (user.getSvieStatus().equals(SvieStatus.ELFOGADVA)) {
                    svieRegLink.setVisible(false);
                }
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
