/**
 * Copyright (c) 2009-2010, Peter Major
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

package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
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
                setResponsePage(ShowUser.class, new PageParameters("id="
                        + userid.toString()));
            }
        } catch (Exception e) {
            log.warn("Exception in AddGroupMember", e);
        }
    }
}
