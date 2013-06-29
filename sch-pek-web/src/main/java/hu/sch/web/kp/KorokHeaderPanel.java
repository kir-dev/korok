package hu.sch.web.kp;

import hu.sch.web.common.HeaderLink;
import hu.sch.web.common.HeaderPanel;
import hu.sch.web.kp.admin.EditSettings;
import hu.sch.web.kp.consider.ConsiderPage;
import hu.sch.web.kp.group.GroupHierarchy;
import hu.sch.web.kp.svie.SvieAccount;
import hu.sch.web.kp.user.ShowUser;
import hu.sch.web.kp.valuation.Valuations;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author messo
 */
class KorokHeaderPanel extends HeaderPanel {

    private boolean showValuationsLink;
    private boolean showConsiderPageLink;
    private boolean showEditSettingsLink;

    public KorokHeaderPanel(String id, boolean showValuationsLink,
            boolean showConsiderPageLink, boolean showEditSettingsLink) {
        super(id);

        this.showValuationsLink = showValuationsLink;
        this.showConsiderPageLink = showConsiderPageLink;
        this.showEditSettingsLink = showEditSettingsLink;

        createLinks();
    }

    @Override
    protected List<HeaderLink> getHeaderLinks() {
        List<HeaderLink> links = new ArrayList<HeaderLink>(6);
        links.add(new HeaderLink(ShowUser.class, "Profilom"));
        links.add(new HeaderLink(GroupHierarchy.class, "Egységek"));
        links.add(new HeaderLink(SvieAccount.class, "SVIE tagság"));
        if (showValuationsLink) {
            links.add(new HeaderLink(Valuations.class, "Értékelések"));
        }
        if (showConsiderPageLink) {
            links.add(new HeaderLink(ConsiderPage.class, "Elbírálás"));
        }
        if (showEditSettingsLink) {
            links.add(new HeaderLink(EditSettings.class, "Adminisztráció"));
        }
        return links;
    }
}
