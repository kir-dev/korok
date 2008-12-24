/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.templates;

import hu.sch.domain.Csoport;
import hu.sch.domain.ErtekelesIdoszak;
import hu.sch.domain.Felhasznalo;
import hu.sch.domain.Szemeszter;
import hu.sch.kp.services.SystemManagerLocal;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.services.exceptions.NoSuchAttributeException;
import hu.sch.kp.web.pages.admin.EditSettings;
import hu.sch.kp.web.pages.elbiralas.OsszesErtekeles;
import hu.sch.kp.web.pages.ertekeles.Ertekelesek2;
import hu.sch.kp.web.pages.group.GroupHierarchy;
import hu.sch.kp.web.pages.group.SelectGroup;
import hu.sch.kp.web.pages.index.SelectUser;
import hu.sch.kp.web.pages.user.ShowUser;
import hu.sch.kp.web.session.VirSession;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebRequest;

/**
 *
 * @author hege
 */
public class SecuredPageTemplate extends WebPage {

    @EJB(name = "SystemManagerBean")
    protected SystemManagerLocal systemManager;
    @EJB(name = "UserManagerBean")
    protected UserManagerLocal userManager;

    public SecuredPageTemplate() {
        if (getSession().getUser() == null) {
            if (loadFelhasznalo() == null) {
                throw new RestartResponseAtInterceptPageException(SelectUser.class);
            }
        }

        //add(new Label("actualuser", new PropertyModel(getSession().getUser(), "nev")));

        IModel agmodel = null;
        if (getSession().getCsoport() != null) {
            agmodel = new PropertyModel(getSession().getCsoport(), "nev");
        } else {
            agmodel = new StringResourceModel("msg.NoGroupSelected", this, null);
        }
        //add(new Label("actualgroup", agmodel));

        IModel szmodel = null;
        Szemeszter szemeszter = getSzemeszter();
        if (szemeszter != null) {
            szmodel = new Model(szemeszter);
        } else {
            szmodel = new StringResourceModel("msg.NoSemester", this, null);
        }
        //add(new Label("actualsemester", szmodel));

        //add(new Label("actualidoszak", new StringResourceModel("ertekelesidoszak." + getIdoszak().toString(), this, null)));


        //((VirSession)getSession()).getUser().
        //TODO: Korvezetonek jelenjen csak meg a link
        //add(new BookmarkablePageLink("ertekelesek", Ertekelesek.class).setAutoEnable(true));
        //TODO: csak JETInek jelenjen meg a link
        add(new BookmarkablePageLink("elbiralas", OsszesErtekeles.class).setVisible(true));
        //TODO: csak JETInek jelenjen meg
        add(new BookmarkablePageLink("editsettings", EditSettings.class).setAutoEnable(true));
        //add(new BookmarkablePageLink("setsemester", EditSemesterPage.class).setAutoEnable(true));
        //add(new BookmarkablePageLink("selectgroup", SelectGroup.class).setAutoEnable(true));
        //add(new BookmarkablePageLink("setidoszak", EditErtekelesIdoszakPage.class).setAutoEnable(true));
        WebMarkupContainer headerLabelContainer = new WebMarkupContainer("headerLabelContainer");
        add(headerLabelContainer);
        headerLabelContainer.add(new Label("headerLabel", new Model()));
        headerLabelContainer.setVisible(false);

        //add(new FeedbackPanel("pagemessages"));
        add(new BookmarkablePageLink("grouphierarchylink", GroupHierarchy.class));
        add(new BookmarkablePageLink("showuserlink", ShowUser.class));
        
        add(new BookmarkablePageLink("ertekeleseklink", Ertekelesek2.class));
    }

    protected Felhasznalo loadFelhasznalo() {
        HttpServletRequest req =
                ((WebRequest) getRequest()).getHttpServletRequest();
        Set viridSet = (Set) req.getAttribute("virid");
        if (viridSet != null) {
            String virid = viridSet.iterator().next().toString();
            Matcher m = Pattern.compile("^.*:([0-9]+)$").matcher(virid);
            if (m.matches()) {
                Long virID = Long.parseLong(m.group(1));
                Felhasznalo user = userManager.findUserById(virID);
                getSession().setUser(user);

                return user;
            }
        }

        return null;
    }

    @Override
    public VirSession getSession() {
        return (VirSession) super.getSession();
    }

    public Szemeszter getSzemeszter() {
        Szemeszter sz = null;
        try {
            sz = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException ex) {
        }
        return sz;
    }

    public Csoport getCsoport() {
        Csoport cs = getSession().getCsoport();
        if (cs == null) {
            throw new RestartResponseAtInterceptPageException(SelectGroup.class);
        }

        return cs;
    }

    public ErtekelesIdoszak getIdoszak() {
        return systemManager.getErtekelesIdoszak();
    }

    public Felhasznalo getFelhasznalo() {
        return getSession().getUser();
    }

    public void setHeaderLabelText(String text) {
        get("headerLabelContainer").setVisible(true);
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setModel(new Model(text));
    }

    public void setHeaderLabelModel(IModel model) {
        get("headerLabelContainer").setVisible(true);
        ((WebMarkupContainer) get("headerLabelContainer")).get("headerLabel").setModel(model);
    }
}
