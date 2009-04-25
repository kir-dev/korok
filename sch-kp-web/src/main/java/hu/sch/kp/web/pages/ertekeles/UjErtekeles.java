/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.ertekeles;

import hu.sch.kp.services.ErtekelesManagerLocal;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author hege
 */
public class UjErtekeles extends SecuredPageTemplate {

    @EJB(name = "ErtekelesManagerBean")
    ErtekelesManagerLocal ertekelesManager;
    String ertekeles = "";

    public UjErtekeles() {
        setHeaderLabelText("Új szöveges értékelés leadása");
        FeedbackPanel feedbackPanel = new FeedbackPanel("pagemessages");
        add(feedbackPanel);
        if (!ertekelesManager.isErtekelesLeadhato(getCsoport())) {
            getSession().info(getLocalizer().getString("err.UjErtekelesNemAdhatoLe", this));
            setResponsePage(Ertekelesek.class);
            return;
        }
        Form ertekelesform = new Form("ujertekelesform") {

            @Override
            protected void onSubmit() {
                if (getErtekeles().isEmpty()) {
                    getSession().error(getLocalizer().getString("err.NincsBeszamolo", this));
                    return;
                }
                ertekelesManager.ujErtekeles(getCsoport(), getFelhasznalo(), getErtekeles());
                getSession().info(getLocalizer().getString("info.ErtekelesMentve", this));
                setResponsePage(Ertekelesek.class);
                return;
            }
        };

        TextArea szovegesErt = new TextArea("szovegesErtekeles", new PropertyModel(this, "ertekeles"));
        szovegesErt.setRequired(true);

        ertekelesform.add(szovegesErt);
        add(ertekelesform);
    }

    public String getErtekeles() {
        return ertekeles;
    }

    public void setErtekeles(String ertekeles) {
        this.ertekeles = ertekeles;
    }
}
