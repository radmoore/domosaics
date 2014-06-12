package domosaics.ui.views.treeview.actions;

import java.awt.event.ActionEvent;

import domosaics.model.tree.TreeNodeI;
import domosaics.ui.ViewHandler;
import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.manager.TreeSelectionManager.TreeSelectionType;




/**
 * Action to select all nodes
 * 
 * @author Andreas Held
 *
 */
public class SelectAllAction  extends AbstractMenuAction{
	private static final long serialVersionUID = 1L;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the root node component and use selection manager to select the subtree
		TreeNodeI root = view.getTree().getRoot();
		NodeComponent nc = view.getNodesComponent(root);
		view.getTreeSelectionManager().addToSelection(nc, TreeSelectionType.SUBTREE);		
	}
}
