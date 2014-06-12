package domosaics.ui.views.domaintreeview.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.ui.ViewHandler;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.layout.CSAModeDomainTreeLayout;
import domosaics.ui.views.domaintreeview.layout.DefaultDomainTreeLayout;
import domosaics.ui.views.treeview.components.NodeComponent;




/**
 * Collapses a subtree as well as redundant arrangements along this subtree.
 * Only non redundant arrangements remain visible, the corresponding 
 * tree nodes will be invisible.
 * 
 * @author Andreas Held
 *
 */
public class CollapseSameArrangementsAtNodeAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	public CollapseSameArrangementsAtNodeAction () {
		super();
		putValue(Action.NAME, "Collapse same arrangements");
		putValue(Action.SHORT_DESCRIPTION, "Collapses the subtree and only shows non redundant arrangements");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DomainTreeViewI view = ViewHandler.getInstance().getActiveView();
	
		// get the selected node from the SelectionManager
		NodeComponent selectedNode =  view.getTreeSelectionManager().getClickedComp();
		if (selectedNode == null) 
			return;
		
		collapse(view, selectedNode);
	}
	
	public static void collapse(DomainTreeViewI view, NodeComponent selectedNode) {
		// make collapsed node status (normal collapsing) and csa manager synchron
		if (selectedNode.isCollapsed() && !view.getCSAInSubtreeManager().isInCSAMode(selectedNode))
			view.getTreeComponentManager().setNodeCollapsed(selectedNode, false, view);
		
		Iterator<NodeComponent> subtreeIter = selectedNode.depthFirstIterator().iterator();
		while(subtreeIter.hasNext()) {
			NodeComponent nc = subtreeIter.next();
			if (nc.isCollapsed() && !view.getCSAInSubtreeManager().isInCSAMode(nc))
				view.getCSAInSubtreeManager().addNode(nc);
		}

		// do a normal collapse
		view.getTreeComponentManager().setNodeCollapsed(selectedNode, !selectedNode.isCollapsed(), view);
		
		// and feed the information into the manager in charge
		if (!view.getCSAInSubtreeManager().isInCSAMode(selectedNode))
			view.getCSAInSubtreeManager().addNode(selectedNode);
		else 
			view.getCSAInSubtreeManager().removeNode(selectedNode);
		
		
		// change the layout if nodes are collapsed in csa mode or not
		if (view.getCSAInSubtreeManager().isActive()) {
			if (!(view.getDomainTreeLayout() instanceof CSAModeDomainTreeLayout)) {
				DefaultDomainTreeLayout newLayout = new CSAModeDomainTreeLayout();
				newLayout.setDomainLayout(view.getDomainLayout());
				view.setViewLayout(newLayout);
				view.getDomainTreeLayoutManager().structuralChange();
			}
		} else {
			if (view.getDomainTreeLayout() instanceof CSAModeDomainTreeLayout) {
				DefaultDomainTreeLayout newLayout = new DefaultDomainTreeLayout();
				newLayout.setDomainLayout(view.getDomainLayout());
				view.setViewLayout(newLayout);
				view.getDomainTreeLayoutManager().structuralChange();
			}
		}
		
		// deselect the node within the Selection manager 
		view.getTreeSelectionManager().setMouseOverComp(null); 
		
		// finally disable shifting because the subtree marker is confused
		if (view.getCSAInSubtreeManager().isActive())
			if (view.getDomainShiftManager().isActive())
				view.getDomainShiftManager().reset();
		
		view.registerMouseListeners();
	}
	
}
