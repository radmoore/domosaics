package angstd.ui.views.treeview.actions.context;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.ui.ViewHandler;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;

/**
 * Action used for collapsing nodes and expand collapsed nodes. 
 * 
 * @author Andreas Held
 *
 */
public class CollapseTreeAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public CollapseTreeAction () {
		super();
		putValue(Action.NAME, "Collapse / Expand Node");
		putValue(Action.SHORT_DESCRIPTION, "Collapses respectively expands the subtree of this node");
	}
	
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();
		
		// get the selected node from the SelectionManager
		NodeComponent selectedNode =  view.getTreeSelectionManager().getClickedComp();
		if (selectedNode == null) 
			return;
		
		// make collapsed node status (normal collapsing) and csa manager synchron
		if (view instanceof DomainTreeViewI) {
			Iterator<NodeComponent> subtreeIter = selectedNode.depthFirstIterator().iterator();
			while(subtreeIter.hasNext()) {
				NodeComponent nc = subtreeIter.next();
				if (((DomainTreeViewI) view).getCSAInSubtreeManager().isInCSAMode(nc))
					((DomainTreeViewI) view).getCSAInSubtreeManager().removeNode(nc);
			}
		}

		// make the treeViewManager trigger the collapsing
		view.getTreeComponentManager().setNodeCollapsed(selectedNode, !selectedNode.isCollapsed(), null);
			
		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null); 
	}

}
