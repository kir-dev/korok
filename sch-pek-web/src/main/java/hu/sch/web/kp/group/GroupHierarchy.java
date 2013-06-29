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
