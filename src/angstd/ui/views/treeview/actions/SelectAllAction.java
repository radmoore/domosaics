package angstd.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import angstd.model.tree.TreeNodeI;
import angstd.ui.ViewHandler;
import angstd.ui.io.menureader.AbstractMenuAction;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.treeview.manager.TreeSelectionManager.TreeSelectionType;



/**
 * Action to select all nodes
 * 
 * @author Andreas Held
 *
 */
public class SelectAllAction  extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the root node component and use selection manager to select the subtree
		TreeNodeI root = view.getTree().getRoot();
		NodeComponent nc = view.getNodesComponent(root);
		view.getTreeSelectionManager().addToSelection(nc, TreeSelectionType.SUBTREE);		
	}
}
