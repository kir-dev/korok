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
import hu.sch.kp.services.exceptions.NoSuchAttributeException;
import hu.sch.kp.web.pages.admin.EditErtekelesIdoszakPage;
import hu.sch.kp.web.pages.admin.EditSemesterPage;
import hu.sch.kp.web.pages.elbiralas.OsszesErtekeles;
import hu.sch.kp.web.pages.ertekeles.Ertekelesek;
import hu.sch.kp.web.pages.group.SelectGroup;
import hu.sch.kp.web.pages.index.SelectUser;
import hu.sch.kp.web.session.VirSession;
import javax.ejb.EJB;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

/**
 *
 * @author hege
 */
public class SecuredPageTemplate extends WebPage {
    
    @EJB(name="SystemManagerBean")
    protected SystemManagerLocal systemManager;

    
    public SecuredPageTemplate() {
        if (getSession().getUser() == null) {
            throw new RestartResponseAtInterceptPageException(SelectUser.class);
        }
        
        add(new Label("actualuser",new PropertyModel(getSession().getUser(), "nev")));

        IModel agmodel = null;
        if (getSession().getCsoport() != null) {
            agmodel = new PropertyModel(getSession().getCsoport(), "nev");
        } else {
            agmodel = new StringResourceModel("msg.NoGroupSelected",this, null);
        }
        add(new Label("actualgroup",agmodel));
        
        IModel szmodel = null;
        Szemeszter szemeszter = getSzemeszter();
        if (szemeszter != null) {
            szmodel = new Model(szemeszter);
        } else {
            szmodel = new StringResourceModel("msg.NoSemester", this, null);
        }
        add(new Label("actualsemester",szmodel));
        
        add(new Label("actualidoszak",new StringResourceModel("ertekelesidoszak."+getIdoszak().toString(), this, null)));
        
        
        add(new FeedbackPanel("pagemessages"));
        add(new BookmarkablePageLink("ertekelesek", Ertekelesek.class).setAutoEnable(true));
        add(new BookmarkablePageLink("elbiralas", OsszesErtekeles.class).setAutoEnable(true));
        add(new BookmarkablePageLink("setsemester", EditSemesterPage.class).setAutoEnable(true));
        add(new BookmarkablePageLink("selectgroup", SelectGroup.class).setAutoEnable(true));
        add(new BookmarkablePageLink("setidoszak", EditErtekelesIdoszakPage.class).setAutoEnable(true));
    }
    
    @Override
    public VirSession getSession() {
        return (VirSession)super.getSession();
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
}
