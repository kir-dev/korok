package hu.sch.web.kp.pages.group;

import hu.sch.domain.Group;
import hu.sch.web.components.SearchAutoCompleteTextField;
import hu.sch.web.kp.templates.SecuredPageTemplate;
import java.io.Serializable;
import java.text.Collator;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author Adam Lantos
 */
public class GroupHierarchy extends SecuredPageTemplate {

    private String[] sort(String[] items) {
        Collator huCollator = Collator.getInstance(new Locale("hu"));
        Arrays.sort(items, huCollator);
        return items;
    }

    public GroupHierarchy() {
        setHeaderLabelText("Csoportok listája");
        add(new FeedbackPanel("pagemessages"));
        final String[] csoportok = sort(userManager.getEveryGroupName().toArray(new String[0]));
        final SearchAutoCompleteTextField field =
                new SearchAutoCompleteTextField("ac", new Model(""), csoportok);
        final Label label = new Label("selectedValue", field.getModel());
        Form form = new Form("form") {

            @Override
            protected void onSubmit() {
                super.onSubmit();
                try {
                    Long id = userManager.getGroupByName(field.getDefaultModelObjectAsString()).getId();
                    setResponsePage(ShowGroup.class, new PageParameters("id=" + id.toString()));
                } catch (Exception ex) {
                    error("A megadott keresési feltételeknek egyetlen kör sem felelt meg.");
                }
                return;
            }
        };

        add(form);
        form.add(field);

        label.setOutputMarkupId(true);
        label.setVisible(false);
        form.add(label);

        field.add(new AjaxFormSubmitBehavior(form, "onchange") {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                try {
                    target.addComponent(label);
                    Long id = userManager.getGroupByName(label.getDefaultModelObjectAsString()).getId();
                    setResponsePage(ShowGroup.class, new PageParameters("id=" + id.toString()));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
            }
        });

        TreeModel model = new DefaultTreeModel(
                new CsoportTreeNode(userManager.getGroupHierarchy()));
        LinkTree tree = new LinkTree("hierarchyTree", model) {

            @Override
            protected IModel getNodeTextModel(IModel nodeModel) {
                return new PropertyModel(nodeModel, "group.name");
            }

            protected void onNodeLinkClicked(
                    TreeNode node, BaseTree baseTree, AjaxRequestTarget target) {
                Long csoportId = ((CsoportTreeNode) node).getGroup().getId();
                setResponsePage(ShowGroup.class,
                        new PageParameters("id=" + csoportId.toString()));
            }
        };
        tree.setRootLess(true);
        add(tree);
    }

    class CsoportTreeNode implements TreeNode, Serializable {

        private Group group;
        private Vector<TreeNode> children;
        private TreeNode parent;

        public CsoportTreeNode(Group csoport) {
            this(csoport, null);
        }

        private CsoportTreeNode(Group group, TreeNode parent) {
            this.group = group;
            this.parent = parent;

            children = new Vector<TreeNode>();
            if (group.getSubGroups() != null) {
                for (Group g : group.getSubGroups()) {
                    children.add(new CsoportTreeNode(g, this));
                }
            }
        }

        public Group getGroup() {
            return group;
        }

        public TreeNode getChildAt(int childIndex) {
            return children.elementAt(childIndex);
        }

        public int getChildCount() {
            return children.size();
        }

        public TreeNode getParent() {
            return parent;
        }

        public int getIndex(TreeNode node) {
            return children.indexOf(node);
        }

        public boolean getAllowsChildren() {
            return true;
        }

        public boolean isLeaf() {
            return children.isEmpty();
        }

        public Enumeration<TreeNode> children() {
            return children.elements();
        }
    }
}
