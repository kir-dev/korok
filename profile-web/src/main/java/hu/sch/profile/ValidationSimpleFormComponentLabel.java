/*
 *  Copyright 2008 konvergal.
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

package hu.sch.profile;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;

/**
 *
 * @author konvergal
 */
class ValidationSimpleFormComponentLabel extends SimpleFormComponentLabel {

    public ValidationSimpleFormComponentLabel(String id, LabeledWebMarkupContainer labelProvider) {
        super(id, labelProvider);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        FormComponent fc = (FormComponent) getFormComponent();
        if (!fc.isValid()) {
            tag.getAttributes().put("class", "labelError");
        }
    }
};
        