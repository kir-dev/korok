/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.kp.pages.svie;

import hu.sch.domain.Group;
import hu.sch.domain.User;
import hu.sch.services.PostManagerLocal;
import hu.sch.services.SvieManagerLocal;
import hu.sch.web.components.SvieDelegateNumberChooser;
import hu.sch.web.components.SvieGroupStatusChooser;
import hu.sch.web.components.customlinks.GroupLink;
import hu.sch.web.components.customlinks.UserLink;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import hu.sch.web.kp.util.SortableGroupDataProvider;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author aldaris
 */
public final class SvieGroupMgmt extends SecuredPageTemplate {

    @EJB(name = "SvieManagerBean")
    SvieManagerLocal svieManager;
    @EJB(name = "PostManagerBean")
    PostManagerLocal postManager;
    private static Logger log = Logger.getLogger(SvieUserMgmt.class);
    private List<Group> groups;
    private SortableGroupDataProvider groupProvider;

    public SvieGroupMgmt() {
        if (!isCurrentUserSVIE()) {
            log.warn("Illetéktelen hozzáférési próbálkozás a SVIE beállításokhoz! Felhasználó: " +
                    getSession().getUserId());
            getSession().error("Nem rendelkezel a megfelelő jogosultságokkal!");
            throw new RestartResponseException(getApplication().getHomePage());
        }

        setHeaderLabelText("Csoportok adminisztrálása");
        add(new FeedbackPanel("pagemessages"));
        groups = userManager.getAllGroups();

        List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
        columns.add(new AbstractColumn<Group>(new Model<String>("Név"), "name") {

            public void populateItem(Item<ICellPopulator<Group>> cellItem, String componentId, IModel<Group> rowModel) {
                cellItem.add(new GroupLink(componentId, rowModel.getObject()));
            }
        });
        columns.add(new AbstractColumn<Group>(new Model<String>("Körvezető neve")) {

            public void populateItem(Item<ICellPopulator<Group>> cellItem, String componentId, IModel<Group> rowModel) {
                User korvezeto = postManager.getGroupLeaderForGroup(rowModel.getObject().getId());
                cellItem.add(new UserLink(componentId, korvezeto));

            }
        });
        columns.add(new AbstractColumn<Group>(new Model<String>("Tagság állapota")) {

            public void populateItem(Item<ICellPopulator<Group>> cellItem, String componentId, IModel<Group> rowModel) {
                cellItem.add(new SvieGroupStatusChooser(componentId, rowModel.getObject()));
            }
        });

        columns.add(new AbstractColumn<Group>(new Model<String>("Küldöttek száma")) {

            public void populateItem(Item<ICellPopulator<Group>> cellItem, String componentId, IModel<Group> rowModel) {
                cellItem.add(new SvieDelegateNumberChooser(componentId, rowModel.getObject()));
            }
        });


        Form form = new Form("svieForm") {

            @Override
            protected void onSubmit() {
                svieManager.updateSvieGroupInfos(groups);
                groupProvider.updateIndexes();
            }
        };

        groupProvider = new SortableGroupDataProvider(groups);
        //azért van változóban, hogy később ha szeretnénk játszadozni a rowperpage-dzsel
        //egyszerűbb legyen.
        final AjaxFallbackDefaultDataTable table =
                new AjaxFallbackDefaultDataTable("table", columns, groupProvider, 40);

        form.add(table);
        add(form);
    }
}

