/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.domain.Csoporttagsag;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.web.components.EditEntitlementsForm;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.PageParameters;

/**
 *
 * @author aldaris
 */
public final class EditEntitlements extends SecuredPageTemplate {

    public EditEntitlements() {
        super();
    }

    public EditEntitlements(PageParameters params) {
        Object p = params.get("id");
        Long id = null;

        try {
            id = Long.parseLong(p.toString());
        } catch (NumberFormatException e) {
            setResponsePage(GroupHierarchy.class);
        }
        setHeaderLabelText("Posztok beállítása");

        Csoport group = userManager.findGroupWithCsoporttagsagokById(id);
        if (!hasUserRoleInGroup(group, TagsagTipus.KORVEZETO)) {
            setResponsePage(ShowGroup.class, new PageParameters("id=" + group.getId().toString()));
            return;
        }
        group.sortCsoporttagsagok();
        List<Csoporttagsag> memberships = group.getCsoporttagsagok();
        List<Csoporttagsag> activeMemberships = new ArrayList<Csoporttagsag>();
        Iterator<Csoporttagsag> iterator = memberships.iterator();
        while (iterator.hasNext()) {
            Csoporttagsag temp = iterator.next();
            if (temp.getVeg() == null) {
                activeMemberships.add(temp);
            }
        }

        add(new EditEntitlementsForm("form", activeMemberships));
    }
}

