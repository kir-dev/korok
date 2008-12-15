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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 *
 * @author konvergal
 */
public final class TestPage extends ProfilePage {

    boolean state = true;

    public class AttributeAjaxFallbackLink extends AjaxFallbackLink {
        public AttributeAjaxFallbackLink(String id) {
            super(id);
        }
        
        public AttributeAjaxFallbackLink(String linkId, String imgId, final boolean privateAttr) {
            this(linkId);
            
            this.add(new Image(imgId).add(new AttributeModifier("src", true, new AbstractReadOnlyModel() {

                @Override
                public Object getObject() {
                    if (privateAttr) {
                        return "http://centaur.sch.bme.hu/~konvergal/no.png";
                    } else {
                        return "http://centaur.sch.bme.hu/~konvergal/ok.png";
                    }
                }
            })));
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            if (target != null) {
                if (state) {
                    state = false;
                } else {
                    state = true;
                }
            }
        }

        @Override
        protected IAjaxCallDecorator getAjaxCallDecorator() {
            return new AjaxCallDecorator() {

                @Override
                public CharSequence decorateScript(CharSequence script) {
                    return "alert('almafa'); " + script;
                }
            };
        }
    }

    
    
    public TestPage() {
        super();

        final Image dynamicImage = new Image("img");
        dynamicImage.add(new AttributeModifier("src", true, new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                if (state) {
                    return "http://wicket.apache.org/style/wicket.png";
                } else {
                    return "http://wicket.apache.org/style/apache.png";
                }
            }
        }));
        dynamicImage.setOutputMarkupId(true);

        add(new AjaxFallbackLink("link") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (target != null) {
                    if (state) {
                        state = false;
                    } else {
                        state = true;
                    }
                }
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return new AjaxCallDecorator() {

                    public CharSequence decorateScript(CharSequence script) {
                        return "alert('This is my javascript call'); " + script;
                    }
                };
            }
        }.add(dynamicImage));
    }

    public TestPage(PageParameters params) {
        //TODO:  process page parameters
    }
}

