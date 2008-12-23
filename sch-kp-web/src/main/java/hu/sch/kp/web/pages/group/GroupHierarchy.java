package hu.sch.kp.web.pages.group;

import hu.sch.domain.Csoport;
import hu.sch.kp.services.UserManagerLocal;
import hu.sch.kp.web.templates.SecuredPageTemplate;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import javax.ejb.EJB;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 *
 * @author Adam Lantos
 */
public class GroupHierarchy extends SecuredPageTemplate {

    @EJB(name = "UserManagerBean")
    UserManagerLocal userManager;

    public GroupHierarchy() {
        setHeaderLabelText("Csoportok listája");
        TreeModel model = new DefaultTreeModel(
                new CsoportTreeNode(userManager.getGroupHierarchy()));
        LinkTree tree = new LinkTree("hierarchyTree", model) {

            @Override
            protected IModel getNodeTextModel(IModel nodeModel) {
                return new PropertyModel(nodeModel, "csoport.nev");
            }

            @Override
            protected void onNodeLinkClicked(
                    TreeNode node, BaseTree tree, AjaxRequestTarget target) {
                Long csoportId = ((CsoportTreeNode) node).getCsoport().getId();
                setResponsePage(ShowGroup.class,
                        new PageParameters("id=" + csoportId.toString()));
            }
        };
        tree.setRootLess(true);
        add(tree);
    }

    class CsoportTreeNode implements TreeNode, Serializable {

        private Csoport csoport;
        private Vector<TreeNode> children;
        private TreeNode parent;

        public CsoportTreeNode(Csoport csoport) {
            this(csoport, null);
        }

        private CsoportTreeNode(Csoport csoport, TreeNode parent) {
            this.csoport = csoport;
            this.parent = parent;

            this.children = new Vector();
            if (csoport.getAlcsoportok() != null) {
                for (Csoport cs : csoport.getAlcsoportok()) {
                    children.add(new CsoportTreeNode(cs, this));
                }
            }
        }

        public Csoport getCsoport() {
            return csoport;
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

        public Enumeration children() {
            return children.elements();
        }
    }
}
