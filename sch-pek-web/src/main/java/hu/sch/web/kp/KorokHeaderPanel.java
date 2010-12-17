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
