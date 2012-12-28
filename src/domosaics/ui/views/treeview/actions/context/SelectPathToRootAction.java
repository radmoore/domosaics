package domosaics.ui.views.treeview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.manager.TreeSelectionManager.TreeSelectionType;




/**
 * Select the path to the root node based on a selected node
 * 
 * @author Andreas Held
 *
 */
public class SelectPathToRootAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public SelectPathToRootAction () {
		super();
		putValue(Action.NAME, "Select Path To Root");
		putValue(Action.SHORT_DESCRIPTION, "Selects the path to the root node");
	}

	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		NodeComponent selectedNode =  view.getTreeSelectionManager().getClickedComp();
		if (selectedNode == null) 
			return;
		
		// use selection manager to select the path to the root
		view.getTreeSelectionManager().addToSelection(selectedNode, TreeSelectionType.PATH_TO_ROOT);
		
		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null); 
	}
}
