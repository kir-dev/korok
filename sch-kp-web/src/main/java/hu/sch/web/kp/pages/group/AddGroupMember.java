package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.domain.MembershipType;
import hu.sch.domain.User;
import hu.sch.web.kp.pages.user.ShowUser;
import hu.sch.web.kp.templates.SecuredPageTemplate;
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
            Group group = userManager.findGroupById(groupid);
            if (group == null) {
                //TODO
                return;
            }
            if (isUserGroupLeader(group)) {
                User felhasznalo = userManager.findUserById(userid);
                if (felhasznalo == null) {
                    //TODO
                    return;
                }
                userManager.addUserToGroup(felhasznalo, group, new Date(), null);
                getSession().info("Sikeres csoportba felvétel");
                setResponsePage(ShowUser.class, new PageParameters("id=" +
                        userid.toString()));
            }
        } catch (Exception e) {
            log.warn("Exception in AddGroupMember", e);
        }
    }
}
