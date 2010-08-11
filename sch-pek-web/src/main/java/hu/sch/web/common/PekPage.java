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

package hu.sch.web.common;

import hu.sch.services.LdapManagerLocal;
import hu.sch.web.PhoenixApplication;
import hu.sch.web.authz.UserAuthorization;
import hu.sch.web.session.VirSession;
import javax.ejb.EJB;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author  messo
 * @since   2.4
 */
public abstract class PekPage extends WebPage {

    private Label titleLabel;
    private Label navbarScript;
    private Label headerLabel;
    @EJB(name = "LdapManagerBean")
    protected LdapManagerLocal ldapManager;

    public PekPage() {
        init();
    }

    private void init() {
        add(titleLabel = new Label("title", getTitle()));
        add(navbarScript = new Label("navbarScript"));
        createNavbarWithSupportId(32);
        navbarScript.setEscapeModelStrings(false); // do not HTML escape JavaScript code

        add(new WebComponent("css").add(
                new AttributeModifier("href", new Model<String>("/css/" + getCss()))));
        add(new WebComponent("favicon").add(
                new AttributeModifier("href", new Model<String>("/images/" + getFavicon()))));

        add(getHeaderPanel("headerPanel"));
        add(headerLabel = new Label("headerLabel", new Model<String>("")));
        add(new FeedbackPanel("pagemessages").setEscapeModelStrings(false));
    }

    /**
     * Beállítjuk az adott lapon a &lt;title/&gt;-t, a "VIR Körök - " előtaggal
     *
     * @param title a cím, amit a "VIR Körök - " után szerepel
     * @since 2.4
     */
    protected void setTitleText(String title) {
        titleLabel.setDefaultModelObject(getTitle() + " - " + title);
    }

    protected abstract String getTitle();

    protected void setHeaderLabelText(String text) {
        headerLabel.setDefaultModelObject(text);
    }

    protected abstract String getCss();

    protected abstract String getFavicon();

    protected abstract Panel getHeaderPanel(String id);

    protected final void createNavbarWithSupportId(int supportId) {
        navbarScript.setDefaultModel(new Model<String>("var navbarConf = { "
                + "logoutLink: 'https://idp.sch.bme.hu/opensso/UI/Logout', "
                + "theme: 'blue', "
                + "width: 900, "
                + "support: " + supportId + ", "
                + "helpMenuItems: ["
                + "{"
                + "title: 'FAQ',"
                + "url: 'https://kir-dev.sch.bme.hu/kozossegi-pontozas/'"
                + "}"
                + "]"
                + "}; "
                + "printNavbar(navbarConf);"));
    }

    protected final boolean isCurrentUserAdmin() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "ADMIN");
    }

    protected final boolean isCurrentUserJETI() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "JETI");
    }

    protected final boolean isCurrentUserSVIE() {
        return getAuthorizationComponent().hasAbstractRole(getRequest(), "SVIE");
    }

    protected UserAuthorization getAuthorizationComponent() {
        return ((PhoenixApplication) getApplication()).getAuthorizationComponent();
    }

    @Override
    public VirSession getSession() {
        return (VirSession) super.getSession();
    }

    protected String getRemoteUser() {
        return getAuthorizationComponent().getRemoteUser(getRequest());
    }
}
