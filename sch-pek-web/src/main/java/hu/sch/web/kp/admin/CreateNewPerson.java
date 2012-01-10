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

package hu.sch.web.kp.admin;

import hu.sch.domain.profile.Person;
import hu.sch.services.exceptions.PersonNotFoundException;
import hu.sch.web.error.NotFound;
import hu.sch.web.kp.KorokPage;
import hu.sch.web.profile.admin.AdminPage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 * @author aldaris
 */
public class CreateNewPerson extends KorokPage {

    private Person person = new Person();

    public CreateNewPerson() {
        if (!isCurrentUserAdmin()) {
            throw new RestartResponseException(NotFound.class);
        }

        Form<Person> form = new Form<Person>("form", new CompoundPropertyModel<Person>(person)) {

            @Override
            protected void onSubmit() {
                person.setStudentUserStatus("urn:mace:terena.org:schac:status:sch.hu:student_status:other");
                person.setStatus("Active");
                ldapManager.registerPerson(person, null);
                setResponsePage(AdminPage.class, new PageParameters("uid=" + person.getUid()));
            }
        };
        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        RequiredTextField<String> uidTF = new RequiredTextField<String>("uid");
        final Label notifier = new Label("notifier", "");
        AjaxFormComponentUpdatingBehavior afcup = new AjaxFormComponentUpdatingBehavior("onblur") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (person.getUid() != null) {
                    try {
                        ldapManager.getPersonByUid(person.getUid());
                        notifier.setDefaultModelObject("Foglalt uid!");
                    } catch (PersonNotFoundException pnfe) {
                        notifier.setDefaultModelObject("Szabad uid");
                    }
                }
                if (target != null) {
                    target.addComponent(wmc);
                }
            }
        };
        uidTF.add(afcup);
        wmc.add(uidTF);
        wmc.add(notifier);
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        form.add(new RequiredTextField<String>("lastName"));
        form.add(new RequiredTextField<String>("firstName"));
        form.add(new RequiredTextField<String>("mail"));

        add(form);
    }
}
