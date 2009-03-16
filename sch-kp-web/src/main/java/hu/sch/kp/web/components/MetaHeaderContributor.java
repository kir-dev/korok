/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.components;

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

    public static final MetaHeaderContributor forMeta(final Class scope) {

        return new MetaHeaderContributor(new IHeaderContributor() {

            private static final long serialVersionUID = 1L;

            public void renderHead(IHeaderResponse response) {
                response.renderString("<META http-equiv=\"refresh\" content=\"5;URL=" +
                        RequestCycle.get().urlFor(scope, null) + "\">");
            }
        });
    }
}
