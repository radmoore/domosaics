package domosaics.ui.views.treeview.layout;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import domosaics.model.tree.TreeI;
import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.treeview.components.NodeComponent;


/**
 * Class DendogramLayout manages to layout a {@link TreeI} within a given area
 * using a dendogram as layout.
 * The class extends the abstract class TreeLayout where the space of
 * the layout area is calculated. 
 * <p>
 * To layout a component Java uses the bounds of the components 
 * for positioning. So the aim of the layout process is to calculate the 
 * bounds of all node components within the tree, which is
 * done in layoutTree. 
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class DendogramLayout extends AbstractTreeLayout {				
	
	/**
	 * flag if a relayout of the relative positions is necessary
	 */
	protected boolean relayout;		
	
	/**
	 * internal collection of parameters used to compute the layout
	 */
	protected DendoParameter params; 
	
	
	/**
	 * Constructor for new DendogramLayouts. 
	 */
	public DendogramLayout() {
		params = new DendoParameter();	
	}
	
	/**
	 * Is triggered when the tree structure changed and therefore a 
	 * recalculation of the node positions is necessary
	 */
	public void treeStructureChanged() {
		super.treeStructureChanged();
		if(params != null) 
			params.max_width_leave = null; 	
		relayout = true;
	}
	
	/* ******************************************************************* *
	 *   						Methods for layouting				   	   *
	 * ******************************************************************* */
	
	/**
	 * Calculates in a first step the relative positions for the node components
	 * by using a recursive bottom-up approach, see {@link #oneShotlayout()}.
	 * Afterwards the real coordinates are computed.
	 */
	public void layoutTree(int x, int y, int width, int height, int leaveLabaleSpace) {
		if (width < 0 || height < 0)
			return;
		
		// init parameters (see inner class DendoParameter) for layout calculation
		if (params.max_width_leave == null)
			params.init();
		
		// calculate the relative positions of the node components if necessary
		if(relayout){
			oneShotlayout();
			relayout = false;
		}
		
		// iterate over all node components and compute the real coordinates
		Iterator<NodeComponent> iter = treeView.getTreeComponentManager().getComponentsIterator();
		while (iter.hasNext()) {
			NodeComponent nc = iter.next();
			
			if(nc.getNode().isLeaf()) { // || nc.isCollapsed()) {
				nc.setBounds((int) (x + Math.round((nc.getRelativeBounds().x * width))),
						 	 (int) (y + Math.round((nc.getRelativeBounds().y * height))),					 						 
						 	 (int) Math.round(leaveLabaleSpace),
						 	 (int) Math.round(nc.getRelativeBounds().height * height));			
			}else {
				if (nc.isCollapsed()) 
					nc.setBounds(
						(int) (x + Math.round((nc.getRelativeBounds().x * width))),
						(int) (y + Math.round((nc.getRelativeBounds().y * height))) ,					 						 
						(int) Math.round(leaveLabaleSpace + nc.getRelativeBounds().width * width),
						(int) Math.round(nc.getRelativeBounds().height * height)
					);			
				else
					nc.setBounds(
						(int) (x + Math.round((nc.getRelativeBounds().x * width))),
						(int) (y + Math.round((nc.getRelativeBounds().y * height))) ,					 						 
						(int) Math.round(nc.getRelativeBounds().width * width),
						(int) Math.round(nc.getRelativeBounds().height * height)
					);			
			
					
			}
			nc.setSubtreeShape(new Rectangle2D.Double(
					x + (nc.getSubtreeBounds().x * width),
					y + (nc.getSubtreeBounds().y * height), 
					nc.getSubtreeBounds().width * width, 
					nc.getSubtreeBounds().height * height));		
		}
	}

	/**
	 * Starts the recursive bottom-up approach to calculate the relative 
	 * positions for the node components using the recursive method 
	 * {@link #layoutStep(NodeComponent, StepParameter, double)}.
	 */
	public void oneShotlayout(){
		StepParameter p = new StepParameter();
		p.offsetX = 1.0 / params.maxDepth;
		p.offsetY = 1.0 / params.numberOfLeaves;
		p.max_dist = 1.0 / params.max_distance;
		
		layoutStep(treeView.getTreeComponentManager().getComponent(treeView.getTree().getRoot()), p, 0);
	}
	
	/**
	 * Recursive method to calculate the tree layout starting with the leaves.
	 * 
	 * @param node actual node to be layouted
	 * @param p the parameter to calculate the relative node positions
	 * @param depth actual depth within the tree
	 */
	protected void layoutStep(NodeComponent node, StepParameter p, double depth) {
		// compute X coordinate of the node components anchor point
		if(!treeView.getTreeLayoutManager().isUseDistances())
			node.getRelativeBounds().x = depth * p.offsetX;
		else if(node.getParent() != null)
			node.getRelativeBounds().x = node.getParent().getRelativeBounds().x + (p.max_dist * node.getNode().getDistanceToParent());

		// set HEIGHT of this node	
		node.getRelativeBounds().height = p.offsetY;
		
		// compute Y, WIDTH and the SUBTREE BOUNDS for leaves
		if(node.getNode().isLeaf()) {// || node.isCollapsed()){
			if(treeView.getTreeLayoutManager().isExpandLeaves() && node.getNode().isLeaf())
				node.getRelativeBounds().x = 1.0;
			node.getRelativeBounds().y = p.offsetY * p.leaves_done;
			node.getRelativeBounds().width = relativeleaveLabaleSpace;

			// in case of leaves, subtree bounds are equal to the node bounds
			node.getSubtreeBounds().setFrame(node.getRelativeBounds());
			p.leaves_done++;
		}else{ 
			// compute Y, WIDTH and the SUBTREE BOUNDS for inner nodes
			// find minY, maxY and minX from all children to position a inner node central
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			double minX = Double.MAX_VALUE;
						
			/* while layouting the children, accumulate the subtree bounds of this node.
			 * to do so, initialy set the subtreebounds to the upmost y position and size of 
			 * the relative bounds.  
			 */
			node.getSubtreeBounds().setFrame(node.getRelativeBounds());
			node.getSubtreeBounds().y = p.offsetY * p.leaves_done;
			
			// iterate over all children and start a recursion, so that children are positioned first
			for (NodeComponent c : node.children()) {	
				layoutStep(c, p, depth+1);	
				
				// get the parameters needed to position the node
				minY = Math.min(minY, c.getRelativeBounds().y);
				maxY = Math.max(maxY, c.getRelativeBounds().y);
				minX = Math.min(minX, c.getRelativeBounds().x);
				node.getSubtreeBounds().add(c.getSubtreeBounds());
			}

			// center this node between minY and maxY
			node.getRelativeBounds().y = minY + ((maxY - minY)/2.0);
			
			// set  nodes width 
			node.getRelativeBounds().width = minX - node.getRelativeBounds().x;
		}	
	}


	
	/* ******************************************************************* *
	 *   							Helper Methods		 			  	   *
	 * ******************************************************************* */
	
	/**
	 * Helper method collection information about the tree Finds all leaves in
	 * the tree, the maximum depth and the maximum distance from the root to a
	 * leave.
	 */
	class DendoParameter {
		double max_distance = Double.MIN_VALUE;
		int maxDepth = 0;
		int numberOfLeaves = 0;
		NodeComponent max_width_leave = null;
		double max_width = 0;

		public void init() {
			max_distance = Double.MIN_VALUE;
			maxDepth = treeView.getTree().getMaxDepth();
			numberOfLeaves = 0;
			max_width_leave = null;		
			
			traverse(treeView.getNodesComponent(treeView.getTree().getRoot()), 0);
		}

		private void traverse(NodeComponent nc, int actDepth) {
			if (maxDepth < actDepth)
				maxDepth = actDepth;
			
			if (nc.getNode().isLeaf()) {
				numberOfLeaves++;	
				Dimension dim = getPreferredSize(nc);
				if (max_width_leave == null || max_width < dim.width) {
					max_width_leave = nc;
					max_width = dim.getWidth();
				}
				double dist = 0;
				for (TreeNodeI node = nc.getNode(); node.getParent() != null; node = (TreeNodeI) node.getParent()) {
					if (node.getDistanceToParent() != -1)
						dist += node.getDistanceToParent();
				}
				if (max_distance < dist)
					max_distance = dist;
			} else {
				for (NodeComponent c : nc.children()) 
					traverse(c, actDepth+1);
			}
		}
	}
	
	/**
	 * Helper class containing the necessary parameters to calculate
	 * the relative positions for the node components.
	 */
	protected class StepParameter{
		double leaves_done = 0;		
		double offsetX = 0;
		double offsetY = 0;
		double max_dist = 0;
	}
}
