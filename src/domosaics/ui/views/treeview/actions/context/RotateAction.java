package domosaics.ui.views.treeview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.manager.TreeComponentManager;




/**
 * Action used for rotating children of a node. 
 * Gets the actual selected node (on which the popup menu is shown) 
 * using the {@link TreeComponentManager} and triggers the {@link TreeViwManager} 
 * to rotate the children of the selected node.
 * <p>
 * After the rotating is done the selected node is deselected.
 * 
 * @author Andreas Held
 *
 */
public class RotateAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for the internal rotate children action for nodes.
	 */
	public RotateAction () {
		super();
		putValue(Action.NAME, "Rotate Children");
		putValue(Action.SHORT_DESCRIPTION, "Rotates the children of this node");
	}
	
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		NodeComponent selectedNode =  view.getTreeSelectionManager().getClickedComp();
		if (selectedNode == null) 
			return;
		
		// make the treeViewManager trigger the rotate process
		view.getTreeComponentManager().rotateNode(selectedNode);
		
		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null); 
	}
}
