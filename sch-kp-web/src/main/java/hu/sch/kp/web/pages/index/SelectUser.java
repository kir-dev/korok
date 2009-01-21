/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.index;

import hu.sch.domain.Felhasznalo;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.session.VirSession;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

/**
 * @todo temp page
 * @author hege
 */
public class SelectUser extends WebPage {

    Long uid;
    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public SelectUser() {
        add(new FeedbackPanel("feedback"));
        Form selectUserForm = new Form("SelectUserForm") {

            @Override
            protected void onSubmit() {
                VirSession sess = (VirSession) getSession();
                //Felhasznalo user = userManager.findUserWithCsoporttagsagokById(getUid());
//                if (user == null) {
                    Felhasznalo user = userManager.findUserById(getUid());
//                }
                if (user != null) {
                    debug("Found user: " + user);
                    sess.setUser(user);
                    if (!continueToOriginalDestination()) {
                        setResponsePage(Index.class);
                        return;
                    } else {
                        return;
                    }
                } else {
                    //TODO: LDAP-ban levo virid-val nem rendelkezo kolleganak generalni
                    //kell id-t, es az LDAP-ba is be kell irni
                    error(getLocalizer().getString("err.NoDBUser", this));
                }
            }
        };
        add(selectUserForm);
        selectUserForm.add(new TextField("uid", new PropertyModel(this, "uid")));
    }
}
