/*
 *  Copyright 2009 aldaris.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package hu.sch.web.error;

import hu.sch.web.profile.pages.template.ProfilePage;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;

/**
 *
 * @author aldaris
 */
public final class InternalServerError extends ProfilePage {

    public InternalServerError() {
        super();
        setHeaderLabelText("Hiba!");
        SmartLinkLabel mailtoLink = new SmartLinkLabel("mail", "kir-dev@sch.bme.hu");
        add(mailtoLink);
    }
}
