package angstd.ui.views.treeview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.treeview.manager.TreeSelectionManager.TreeSelectionType;

/**
 * Select the subtree of an selected node
 * 
 * @author Andreas Held
 *
 */
public class SelectSubtreeAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public SelectSubtreeAction () {
		super();
		putValue(Action.NAME, "Select Subtree");
		putValue(Action.SHORT_DESCRIPTION, "Selects the nodes of the subtree");
	}

	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		NodeComponent selectedNode =  view.getTreeSelectionManager().getClickedComp();
		if (selectedNode == null) 
			return;
		
		// use selection manager to select the subtree
		view.getTreeSelectionManager().addToSelection(selectedNode, TreeSelectionType.SUBTREE);
		
		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null); 
	}
}
