/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.admin;

import hu.sch.domain.Szemeszter;
import hu.sch.kp.services.exceptions.NoSuchAttributeException;
import hu.sch.kp.services.SystemManagerLocal;
import hu.sch.kp.web.pages.index.Index;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.NumberValidator;

/**
 *
 * @author hege
 */
public class EditSemesterPage extends SecuredPageTemplate {
    
    @EJB(name = "SystemManagerBean")
    SystemManagerLocal systemManager;
    
    private Szemeszter szemeszter;

    public EditSemesterPage() {
        super();

        try {
            szemeszter = systemManager.getSzemeszter();
        } catch (NoSuchAttributeException e) {
            szemeszter = new Szemeszter();
        }
        
        Form editSemesterForm = new Form("editSemesterForm") {
            @Override
            public void onSubmit() {
                systemManager.setSzemeszter(getSzemeszter());
                setResponsePage(Index.class);
            }
        };
        editSemesterForm.setModel(new CompoundPropertyModel(szemeszter));
        final TextField elsoEv = new TextField("elsoEv");
        editSemesterForm.add(elsoEv.add(NumberValidator.range(2000, 2030)));
        final TextField masodikEv = new TextField("masodikEv");
        editSemesterForm.add(masodikEv.add(NumberValidator.range(2000, 2030)));
        editSemesterForm.add(new CheckBox("osziFelev"));
        editSemesterForm.add(new AbstractFormValidator() {

            public FormComponent[] getDependentFormComponents() {
                return new FormComponent[]{elsoEv,masodikEv};
            }

            public void validate(Form form) {
                if (Integer.parseInt(elsoEv.getValue()) + 1 != 
                        Integer.parseInt(masodikEv.getValue())) {
                    error(elsoEv,"err.SzemeszterEvKulonbseg");
                }
            }
        });
        
        add(editSemesterForm);
    }
    
    public Szemeszter getSzemeszter() {
        return szemeszter;
    }

}
