/**
 * Copyright (c) 2008-2010, Peter Major
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

package hu.sch.web.kp;

import hu.sch.domain.Group;
import hu.sch.domain.Semester;
import hu.sch.domain.User;
import hu.sch.domain.ValuationPeriod;
import hu.sch.services.PostManagerLocal;
import hu.sch.services.exceptions.NoSuchAttributeException;
import hu.sch.services.SystemManagerLocal;
import hu.sch.web.common.PekPage;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hege
 */
public abstract class KorokPage extends PekPage {

    @EJB(name = "SystemManagerBean")
    protected SystemManagerLocal systemManager;
    @EJB(name = "PostManagerBean")
    protected PostManagerLocal postManager;
    private static final Logger log = LoggerFactory.getLogger(KorokPage.class);

    @Override
    protected String getTitle() {
        return "VIR Körök";
    }

    @Override
    protected String getCss() {
        return "korok-style.css";
    }

    @Override
    protected String getFavicon() {
        return "favicon-korok.ico";
    }

    @Override
    protected Panel getHeaderPanel(String id) {
        return new KorokHeaderPanel(id, isUserGroupLeaderInSomeGroup(),
                isCurrentUserJETI() && systemManager.getErtekelesIdoszak() == ValuationPeriod.ERTEKELESELBIRALAS,
                isCurrentUserJETI() || isCurrentUserSVIE() || isCurrentUserAdmin());
    }

    protected final Semester getSemester() {
        Semester sz = null;
        try {
            sz = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException ex) {
            log.warn("Attribute for semester isn't set in the database.", ex);
        }
        return sz;
    }

    protected final boolean isUserGroupLeader(Group group) {
        return getAuthorizationComponent().isGroupLeaderInGroup(getRequest(), group);
    }

    protected final boolean isUserGroupLeaderInSomeGroup() {
        return getAuthorizationComponent().isGroupLeaderInSomeGroup(getRequest());
    }

    /**
     * A beloginolt felhasználót ellenőrizzük, hogy van-e delegált posztja az adott csoportban
     *
     * @param group     melyik csoportban vizsgálódunk
     * @return          Van-e delegált posztja a csoportban?
     */
    protected final boolean hasUserDelegatedPostInGroup(Group group) {
        User user = getUser();
        if (user == null) {
            return false;
        }
        return postManager.hasUserDelegatedPostInGroup(group, user);
    }

    /**
     * Az adott felhasználót ellenőrizzük, hogy van-e delegált posztja az adott csoportban
     *
     * @param user      kérdéses felhasználó
     * @param group     melyik csoportban vizsgálódunk
     * @return          Van-e delegált posztja a csoportban?
     */
    protected final boolean hasUserDelegatedPostInGroup(User user, Group group) {
        if (user == null) {
            return false;
        }
        return postManager.hasUserDelegatedPostInGroup(group, user);
    }
}
