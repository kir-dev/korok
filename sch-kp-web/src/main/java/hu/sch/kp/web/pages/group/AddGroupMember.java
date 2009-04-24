package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.TagsagTipus;
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

/**
 *
 * @author hege
 */
public class AddGroupMember extends SecuredPageTemplate {

    private static final Logger log = Logger.getLogger(AddGroupMember.class);

    public AddGroupMember(PageParameters params) {
        Long groupid = new Long(params.getLong("groupid"));
        Long userid = new Long(params.getLong("userid"));
        try {
            Csoport csoport = userManager.findGroupById(groupid);
            if (csoport == null) {
                //TODO
                return;
            }
            if (hasUserRoleInGroup(csoport, TagsagTipus.KORVEZETO)) {
                Felhasznalo felhasznalo = userManager.findUserById(userid);
                if (felhasznalo == null) {
                    //TODO
                    return;
                }
                userManager.addUserToGroup(felhasznalo, csoport, new Date(), null);
                getSession().info("Sikeres csoportba felvétel");
                setResponsePage(ShowUser.class, new PageParameters("id=" +
                        userid.toString()));
            }
        } catch (Exception e) {
            log.warn("Exception in AddGroupMember", e);
        }
    }
}
