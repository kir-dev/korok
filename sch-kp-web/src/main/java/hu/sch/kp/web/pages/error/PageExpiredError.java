/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sch.kp.web.pages.error;

import hu.sch.kp.web.components.MetaHeaderContributor;
import hu.sch.kp.web.pages.group.GroupHierarchy;
import hu.sch.kp.web.templates.SecuredPageTemplate;

/**
 *
 * @author aldaris
 */
public final class PageExpiredError extends SecuredPageTemplate {

    public PageExpiredError() {
        super();
        setHeaderLabelText("Hiba!");
        add(MetaHeaderContributor.forMeta(GroupHierarchy.class));
    }
}

