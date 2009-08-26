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

import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 *
 * @author aldaris
 */
public class MetaHeaderContributor extends HeaderContributor {

    public MetaHeaderContributor(IHeaderContributor headerContributor) {
        super(headerContributor);
    }

    public static final MetaHeaderContributor forMeta(final Class<? extends Page> scope) {

        return new MetaHeaderContributor(new IHeaderContributor() {

            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(IHeaderResponse response) {
                response.renderString("<meta http-equiv=\"refresh\" content=\"5;URL=" +
                        RequestCycle.get().urlFor(scope, null) + "\">");
            }
        });
    }
}
