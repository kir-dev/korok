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
package hu.sch.web.kp.group;

import hu.sch.domain.Group;
import hu.sch.web.kp.KorokPage;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Adam Lantos
 */
public class GroupHierarchy extends KorokPage {

    private final List<Group> roots = userManager.getGroupHierarchy();

    public GroupHierarchy() {
        setHeaderLabelText("Csoportok listája");

        add(new DefaultNestedTree<Group>("hierarchyTree", new TreeProvider()) {
            @Override
            protected Component newContentComponent(final String id, final IModel<Group> model) {
                return new Folder<Group>(id, this, model) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected MarkupContainer newLinkComponent(final String id, final IModel<Group> model) {
                        final Group group = model.getObject();
                        return new BookmarkablePageLink<ShowGroup>(id, ShowGroup.class,
                                new PageParameters().add("id", group.getId()));
                    }
                };
            }
        });
    }

    public class TreeProvider implements ITreeProvider<Group> {

        @Override
        public Iterator<? extends Group> getChildren(final Group t) {
            return t.getSubGroups().iterator();
        }

        @Override
        public Iterator<? extends Group> getRoots() {
            return roots.iterator();
        }

        @Override
        public boolean hasChildren(final Group t) {
            final List<Group> groups = t.getSubGroups();
            if (groups == null) {
                return false;
            } else {
                return !groups.isEmpty();
            }
        }

        @Override
        public IModel<Group> model(final Group t) {
            return new Model<Group>(t);
        }

        @Override
        public void detach() {
        }
    }
}
