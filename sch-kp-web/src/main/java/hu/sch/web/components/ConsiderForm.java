/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.web.components;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;

/**
 *
 * @author aldaris
 */
public abstract class ConsiderForm extends Form {

    public ConsiderForm(String name) {
        super(name);
        add(new Button("accept") {

            @Override
            public void onSubmit() {
                super.onSubmit();
                doSave();
            }
        });
        add(new Button("refuse") {

            @Override
            public void onSubmit() {
                super.onSubmit();
                doRefuse();
            }
        });
    }

    public void doSave() {
    }

    public void doRefuse() {
    }
}
