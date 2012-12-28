package angstd.ui.views.treeview.actions.context;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;



/**
 * Action used to define a new outgroup for a tree
 * 
 * @author Andreas Held
 *
 */
public class TakeAsNewOutgroupAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public TakeAsNewOutgroupAction () {
		super();
		putValue(Action.NAME, "Take As New Outgroup");
		putValue(Action.SHORT_DESCRIPTION, "Takes the node as new outgroup");
	}
	
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = ViewHandler.getInstance().getActiveView();

		// get the selected node from the SelectionManager
		NodeComponent selectedNode =  view.getTreeSelectionManager().getClickedComp();
		if (selectedNode == null) 
			return;
		
		// make the treeViewManager trigger the rotate process
		view.getTreeComponentManager().setNewOutgroup(view.getTree(), selectedNode);
		
		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null); 
	}
}
