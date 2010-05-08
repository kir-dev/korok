/**
 * Copyright (c) 2009-2010, Peter Major
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

package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.web.wicket.behaviors.FocusOnLoadBehavior;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.text.Collator;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import wickettree.ITreeProvider;
import wickettree.NestedTree;
import wickettree.content.Folder;
import wickettree.theme.WindowsTheme;

/**
 *
 * @author Adam Lantos
 */
public class GroupHierarchy extends SecuredPageTemplate {

    private static Logger log = Logger.getLogger(GroupHierarchy.class);
    private List<Group> roots = userManager.getGroupHierarchy();

    public GroupHierarchy() {
        setHeaderLabelText("Csoportok listája");
        add(new FeedbackPanel("pagemessages"));

        final NestedTree<Group> tree = new NestedTree<Group>("hierarchyTree", new TreeProvider()) {

            @Override
            protected Component newContentComponent(String string, IModel<Group> model) {
                return new Folder<Group>(string, this, model) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    protected MarkupContainer newLinkComponent(String id, IModel<Group> model) {
                        Group group = model.getObject();
                        return new BookmarkablePageLink<Void>(id, ShowGroup.class,
                                new PageParameters("id=" + group.getId()));
                    }
                };
            }
        };
        tree.add(new HeaderContributor(new IHeaderContributor() {

            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(IHeaderResponse response) {
                response.renderCSSReference(new CompressedResourceReference(WindowsTheme.class, "windows/theme.css"));
            }
        }));
        add(tree);
    }

    public class TreeProvider implements ITreeProvider<Group> {

        @Override
        public Iterator<? extends Group> getChildren(Group t) {
            return t.getSubGroups().iterator();
        }

        @Override
        public Iterator<? extends Group> getRoots() {
            return roots.iterator();
        }

        @Override
        public boolean hasChildren(Group t) {
            List<Group> groups = t.getSubGroups();
            if (groups == null) {
                return false;
            } else {
                return !groups.isEmpty();
            }
        }

        @Override
        public IModel<Group> model(Group t) {
            return new Model<Group>(t);
        }

        @Override
        public void detach() {
        }
    }
}
