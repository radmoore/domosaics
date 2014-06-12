package domosaics.ui.views.treeview.manager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

import domosaics.model.tree.TreeI;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.ViewHandler;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.treeview.actions.context.CollapseTreeAction;
import domosaics.ui.views.treeview.actions.context.RotateAction;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.manager.AbstractComponentManager;
import domosaics.ui.views.view.manager.ComponentManager;




/**
 * The TreeComponentManager maps tree NodeComponents to their backend data
 * TreeNodeI. <br>
 * Therefore it is possible to access all NodeComponents
 * provided by the TreeView.
 * <p>
 * The TreeComponentManager extends the 
 * {@link AbstractComponentManager} which handles the component mapping.
 * The getComponent() method is extended in this manager to manage the
 * initialization of new NodeComponents. 
 * <p>
 * Also this class provides methods to change the tree structure. For instance
 * shrink children, rotate nodes etc.
 * <p>
 * DoMosaics handles bootstrap values which can be coded in newick format 
 * as node labels. Therefore this class also provides a method to
 * switch between the display of bootstrap values and edge weights.
 * If the user wants to show the bootstrap values the useLabelAsBootstrap()
 * method has to be invoked which manages the rest, e.g. converting the 
 * numeric labels from 0 - 100 into bootstrap values and therefore
 * into edge labels.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class TreeComponentManager extends AbstractComponentManager<TreeNodeI, NodeComponent> {
	
	/**
	 * @see ComponentManager
	 */
	@Override
	public NodeComponent getComponent(TreeNodeI node){
		if (node == null) 
			return null;

		NodeComponent component = backend2components.get(node);
		if (component == null) {
			component = new NodeComponent(node, this);
			backend2components.put(node, component);
		}
		return component;
	}
	
	/**
	 * Sets the label for a tree node
	 * 
	 * @param nc
	 * 		the node component which label is going to be changed
	 * @param label
	 * 		the new label for the specified node component
	 */
   	public void setLabel(NodeComponent nc, String label) {
   		if (nc == null || label == null)
   			return;
   		nc.getNode().setLabel(label);
   		structuralChange();
   	}
   	
   	/**
	 * formatter which formats the edge label in decimal font with three digits
	 */
	protected NumberFormat formatter = DecimalFormat.getNumberInstance(Locale.ENGLISH); 
	{		
		formatter.setMaximumFractionDigits(3);
	}
   	
   	/**
   	 * Method which must be invoked when the user switches to the
   	 * visualization of bootstrap values and back. If no bootstrap
   	 * values are displayed the edge weights are rendered (default).
   	 * 
   	 * @param useLabelAsBootStrap
   	 * 		flag indicating whether or not bootstrap values shall be used
   	 */
	public void useLabelAsBootstrap(boolean useLabelAsBootStrap){
		Iterator<NodeComponent> iter = getComponentsIterator();
		
		// generate labels out of bootstrap vals
		if (useLabelAsBootStrap) {
			while(iter.hasNext()) {
				NodeComponent nc = iter.next();
				if (nc.getNode().getBootstrap() != -1)
					nc.getNode().setLabel(formatter.format(nc.getNode().getBootstrap()));
			}
		} else { // kill labels if bootstrap is present
			while(iter.hasNext()) {
				NodeComponent nc = iter.next();
				if (nc.getNode().getBootstrap() != -1 && nc.getNode().getLabel().equals(formatter.format(nc.getNode().getBootstrap())))
					nc.getNode().setLabel("");
			}
		}
		//structuralChange();	
	}
   	
    /**
     * Rotates the children of the given node clockwise. Triggered by {@link RotateAction}.
     * <p>
     * The direct change happens via the {@link TreeNodeI#rotateChildren(boolean)} method. 
     * 
     * @param node 
     * 		the node to rotate
     */
	public void rotateNode(NodeComponent node){
		TreeNodeI p = node.getNode();
		p.rotateChildren();
		structuralChange();	
	}
	
	/**
	 * Wrapper method around the reroot method within the tree class 
	 * which performs a reroot and therefore the choosing of a new
	 * node as outgroup.
	 * 
	 * @param tree
	 * 		the tree to be rerooted
	 * @param outgroup
	 * 		the new outgroup of the tree
	 */
	public void setNewOutgroup(TreeI tree, NodeComponent outgroup){
		TreeNodeI newRoot = outgroup.getNode();
		
		// the rootNode will be removed from the tree therefore its component has to be deleted
		removeComponent(tree.getRoot());

		tree.reRoot(newRoot);
		structuralChange();	
	}
	
	/**
	 * Collapse/expands a given node. Triggered by {@link CollapseTreeAction}.
	 * <p>
	 * Changes the nodes status by delegating to the nodes {@link NodeComponent#setCollapsed(boolean)}
	 * method. The Helper method {@link #shrinkChildren(boolean, NodeComponent) is
	 * triggered where new visibility status for the nodes within the subtree is set. 
	 * Afterwards a structural change is fired.
	 * 
	 * @param node 
	 * 		the node to collapse or expand
	 * @param collapsed 
	 * 		if true, the node gets collapsed, otherwise it gets expanded
	 * @param domView 
	 * 		FIXME the dependency shouldnt be necessary. Its the domain view to set the new visibility flag for DAs after collapsing.
	 */
	public void setNodeCollapsed(NodeComponent node, boolean collapsed, DomainTreeViewI domView){
		node.setCollapsed(collapsed);
		shrinkChildren(collapsed, node, domView);
		structuralChange();	
	}
	
	/**
	 * Recursive helper method to shrink all children of a collapsed node. Sets the
	 * new visibility status for the node components of the subtree depending
	 * on whether the node was collapsed or expanded.
	 * 
	 * @param collapse 
	 * 		the new collapsed status for the node
	 * @param collapsedNode 
	 * 		the node to be collapsed /expanded
	 * @param domView 
	 * 		FIXME the dependency shouldnt be necessary. Its the domain view to set the new visibility flag for DAs after collapsing.
	 */
	private void shrinkChildren(boolean collapse, NodeComponent collapsedNode, DomainTreeViewI domView) {
		
		if (domView == null && ViewHandler.getInstance().getActiveView() instanceof DomainTreeViewI) 
			domView = (DomainTreeViewI) ViewHandler.getInstance().getActiveView();
		
		
		for (NodeComponent nc : collapsedNode.children()) {
			if (collapse) {
				nc.setVisible(false);	
				
				if (domView != null && nc.getNode().hasArrangement())  // means domtree is loaded
					domView.getArrangementComponentManager().setVisible(domView.getArrangementComponentManager().getComponent(nc.getNode().getArrangement()), false);
				shrinkChildren(collapse, nc, domView);
			} else {
				nc.setVisible(true);
				
				if (domView != null && nc.getNode().hasArrangement())		
					domView.getArrangementComponentManager().setVisible(domView.getArrangementComponentManager().getComponent(nc.getNode().getArrangement()), true);
			
				if (!nc.isCollapsed()) 
					shrinkChildren(collapse, nc, domView);
			}
		}
	}
}

