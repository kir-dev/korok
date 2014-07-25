package hu.sch.web.kp.group.admin;

import hu.sch.domain.Membership;
import hu.sch.services.MembershipManagerLocal;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.kp.group.ShowGroup;
import hu.sch.web.session.VirSession;
import hu.sch.web.wicket.components.SelectableMembership;
import hu.sch.web.wicket.components.SvieMembershipDetailsIcon;
import hu.sch.web.wicket.components.customlinks.ChangePostLink;
import hu.sch.web.wicket.components.tables.MembershipTable;
import hu.sch.web.wicket.components.tables.PanelColumn;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ezt a panelt látja a user akkor, ha jogosult arra, hogy valakit öregtaggá
 * avasson, vagy töröljön valakit a listáról.
 *
 * @author aldaris
 * @author messo
 * @see MembershipTable
 */
public final class AdminMembershipsPanel extends Panel {

    private static Logger log = LoggerFactory.getLogger(AdminMembershipsPanel.class);

    @Inject
    private MembershipManagerLocal membershipManager;

    public AdminMembershipsPanel(String id, final List<Membership> activeMembers) {
        super(id);

        final List<SelectableMembership> lines
                = new ArrayList<SelectableMembership>(activeMembers.size());
        for (Membership ms : activeMembers) {
            lines.add(new SelectableMembership(ms));
        }

        Form entForm = new Form("form");

        add(entForm);

        entForm.add(new MembershipTable<SelectableMembership>("table", lines, SelectableMembership.class) {

            @Override
            public void onPopulateColumns(List<IColumn<SelectableMembership, String>> columns) {
                columns.add(new PanelColumn<SelectableMembership>("SVIE",
                        MembershipTable.SORT_BY_SVIE) {

                            @Override
                            protected Panel getPanel(String componentId, SelectableMembership obj) {
                                return new SvieMembershipDetailsIcon(componentId, obj.getMembership());
                            }
                        });

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
                    for (SelectableMembership extendedGroup : lines) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            if (!ms.getUser().getId().equals(getCurrentUserId())) {
                                membershipManager.inactivateMembership(ms);
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
                    for (SelectableMembership extendedGroup : lines) {
                        Membership ms = extendedGroup.getMembership();
                        if (extendedGroup.getSelected()) {
                            if (!ms.getUser().getId().equals(getCurrentUserId())) {
                                membershipManager.deleteMembership(ms);
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

    private Long getCurrentUserId() {
        return ((PhoenixApplication) getApplication()).getAuthorizationComponent().getUserid(getRequest());
    }
}
