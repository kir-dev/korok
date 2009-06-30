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
package hu.sch.web.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;

/**
 *
 * @author aldaris
 */
public class ConfirmationBoxRenderer extends AbstractBehavior {

    private String message;

    /**
     * Constructor.
     * @param message Message to be shown in the confirm box.
     */
    public ConfirmationBoxRenderer(final String message) {
        super();
        this.message = message;
    }

    /**
     * @param component Component to attach.
     * @param tag Tag to modify.
     * @see org.apache.wicket.behavior.AbstractBehavior#onComponentTag(org.apache.wicket.Component, org.apache.wicket.markup.ComponentTag)
     */
    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        if (component instanceof Button || component instanceof Link) {
            tag.getAttributes().remove("onclick");
            tag.getAttributes().put("onclick", "return confirm('" + message + "')");
        }
    }
}
