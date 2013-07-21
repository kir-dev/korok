package hu.sch.web.kp.group.admin;

import hu.sch.domain.Membership;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.services.UserManagerLocal;
import hu.sch.web.kp.group.ShowGroup;
import hu.sch.web.wicket.components.SelectableMembership;
import hu.sch.web.wicket.components.tables.DateIntervalPropertyColumn;
import hu.sch.web.wicket.components.tables.MembershipTable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ezt a panelt látja a user akkor, ha jogosult arra, hogy aktiválhassa az
 * öregtagokat. A körítésen (lásd markup) kívül lehet rendezgetni a táblázatot.
 *
 * @author aldaris
 * @author messo
 * @see MembershipTable
 */
public final class AdminOldBoysPanel extends Panel {

    private static Logger log = LoggerFactory.getLogger(AdminOldBoysPanel.class);

    @EJB(name = "MembershipManagerBean")
    private MembershipManagerLocal membershipManager;

    public AdminOldBoysPanel(String id, final List<Membership> inactiveMembers) {
        super(id);

        Form form;

        final List<SelectableMembership> lines = new ArrayList<SelectableMembership>(inactiveMembers.size());
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
                            membershipManager.activateMembership(ms);
                        }
                    }
                    getSession().info("A változások sikeresen mentésre kerültek");
                } catch (Exception ex) {
                    getSession().error("Hiba történt a feldolgozás közben");
                    log.warn("Hiba történt az öregtag visszaállításakor", ex);
                }
                setResponsePage(ShowGroup.class, new PageParameters().add("id", inactiveMembers.get(0).getGroup().getId()));
            }
        });

        form.add(new MembershipTable<SelectableMembership>("table", lines, SelectableMembership.class) {

            @Override
            public void onPopulateColumns(List<IColumn<SelectableMembership, String>> columns) {
                columns.add(new DateIntervalPropertyColumn<SelectableMembership>(
                        new Model<String>("Tagság ideje"), "membershipStartEnd", "membership.start", "membership.end"));
            }
        }.getDataTable());

        if (inactiveMembers.isEmpty()) {
            setVisible(false);
        }
    }
}
