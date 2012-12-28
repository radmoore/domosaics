package angstd.ui.views.treeview.manager;

import java.util.ArrayList;
import java.util.Collection;

import angstd.model.tree.TreeEdgeI;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.view.manager.DefaultSelectionManager;



/**
 * TreeSelectionManager provides method to automatically select elements of the tree
 * such as a subtree of a node. Therefore the selection of tree nodes
 * are managed by this manager.
 * <p>
 * Because there doesn't exist any graphical components this class also 
 * provides a method to retrieve selected edges based on the current node 
 * selection.
 *  
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class TreeSelectionManager extends DefaultSelectionManager<NodeComponent>  {

	/**
	 * An enumeration of possible ways to select components within a tree view
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum TreeSelectionType {

		NODE, PATH_TO_ROOT, SUBTREE;
		
	}
	
	/** the view to be managed */
	protected TreeViewI view;
	
	
	/**
	 * Constructor for a new TreeSelectionManager
	 * 
	 * @param view
	 * 		the view to be managed
	 */
	public TreeSelectionManager(TreeViewI view) {
		this.view = view;
	}
	
	/**
	 * @see DefaultSelectionManager
	 */
	public void clearSelection() {
		super.clearSelection();
		view.getTreeColorManager().setSelectionColor(view.getTreeColorManager().getDefaultSelectionColor());
	}
	
	/**
	 * Adds a path between two nodes to the selection
	 * 
	 * @param ancestor
	 * 		the node being higher within the hierarchy
	 * @param child
	 * 		the node being lower within the hierarchy
	 */
	public void addPathToSelection(NodeComponent ancestor, NodeComponent child) {
		ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
		while(child != ancestor){
			newAdds.add(child);
			child = child.getParent();
		}		
		newAdds.add(ancestor);
		selection.addAll(newAdds);
	}

	/**
	 * Adds depending on the selection type nodes to the selection.
	 * 
	 * @param node
	 * 		the selected node 
	 * @param type
	 * 		the type of selection (e.g. subtree)
	 */
	public void addToSelection(NodeComponent node, TreeSelectionType type) {
		if(node == null)
			return;
		
		switch(type) {
			case NODE: selection.add(node); break;
			case PATH_TO_ROOT: selectPathToRoot(node);  break;
			case SUBTREE: selectSubtree(node);  break;
			default: selection.add(node);
		}
		visualChange();
	}
	
	/**
	 * Method to retrieve all selected edges
	 * 
	 * @return
	 * 		all selected edges
	 */
	public Collection<TreeEdgeI> getSelectedEdges() {
		Collection<TreeEdgeI> res = new ArrayList<TreeEdgeI>();
		
		for (NodeComponent nc : getSelection()) {
			TreeEdgeI actEdge = nc.getNode().getEdgeToParent();
			if (actEdge == null) // necessary because of the root node
				continue; 
			
			// edge is selected if both the node and its parent are selected
			if (!isCompSelected(view.getTreeComponentManager().getComponent(nc.getNode().getParent())))
				continue;
			
			res.add(actEdge);
		}
		
		return res;
	}
	
	/**
	 * Helper method to select the nodes on the path to the root 
	 * starting with the selected node
	 * 
	 * @param node
	 * 		the selected node
	 */
	private void selectPathToRoot(NodeComponent node) {
		ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
		while(node != null){
			newAdds.add(node);
			node = node.getParent();
		}					
		selection.addAll(newAdds);
	}
	
	/**
	 * Helper method to select the subtree of a selected node
	 * 
	 * @param node
	 * 		the selected node
	 */
	private void selectSubtree(NodeComponent node) {
		ArrayList<NodeComponent> newAdds = new ArrayList<NodeComponent>();
		for (NodeComponent c : node.depthFirstIterator()) 
			newAdds.add(c);
		selection.addAll(newAdds);
	}
}
