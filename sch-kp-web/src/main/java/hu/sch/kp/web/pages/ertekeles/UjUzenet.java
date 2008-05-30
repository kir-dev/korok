/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sch.kp.web.pages.ertekeles;

import hu.sch.domain.Ertekeles;
import hu.sch.domain.ErtekelesUzenet;
import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
class UjUzenet extends SecuredPageTemplate {
    @EJB(name="ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;

    String uzenet = "";
    
    public UjUzenet(final Long ertekelesId) {
        Form form = new Form("ujuzenetform") {

            @Override
            protected void onSubmit() {
                ertekelesManager.ujErtekelesUzenet(ertekelesId, getFelhasznalo(), getUzenet());
                getSession().info(getLocalizer().getString("info.UzenetMentve", this));
                setResponsePage(new ErtekelesUzenetek(ertekelesId));
            }
        };

        TextArea uzfield = new TextArea("uzenet",new PropertyModel(this, "uzenet"));
        uzfield.setRequired(true);
        
        form.add(uzfield);
        add(form);
    }

    public String getUzenet() {
        return uzenet;
    }

    public void setUzenet(String uzenet) {
        this.uzenet = uzenet;
    }

    
}
