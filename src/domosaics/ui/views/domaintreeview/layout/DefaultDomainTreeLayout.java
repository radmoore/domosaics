package domosaics.ui.views.domaintreeview.layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import domosaics.model.tree.TreeNodeI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.layout.DomainLayout;
import domosaics.ui.views.treeview.components.NodeComponent;




/**
 * DefaultDomainTreeLayout is the default layout to layout arrangements
 * in a domain tree view.
 * The arrangements are layouted corresponding to their associated
 * node components and the node component bounds are recalculated.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDomainTreeLayout extends AbstractDomainTreeLayout {
	
	/**
	 * @see DomainLayout
	 */
	public void layoutArrangements(int x, int y, int width, int height) {
		domlayout.getDomainParams().init(width, height);
		
		Iterator<NodeComponent> iter = view.getTreeComponentManager().getComponentsIterator();
		while (iter.hasNext()) {
			NodeComponent nc = iter.next();

			if(nc.isVisible() && nc.getNode().hasArrangement()) {  
				ArrangementComponent dac = view.getArrangementComponentManager().getComponent(nc.getNode().getArrangement()); 
	
				int startX = nc.getX();
				if (nc.getNode().isLeaf())
					startX += param.maxLabelWidth + DISTANCE_BETWEEN_TREE_AND_DATASET;
				
				domlayout.layoutArrangement(dac, startX, nc.getY(), width, height);
				
				// add arrangement bounds to the node bounds
				nc.setBounds( nc.getX(), 
							  nc.getY(), 
						      nc.getWidth() + dac.getWidth(),
						      nc.getHeight());
				
			}
		}
		calcNewSubtreeBounds(view.getTreeComponentManager().getComponent((TreeNodeI)domTree.getRoot()));
	}

	/**
	 * Helper method to recalculate the subtree bounds for nodes 
	 * which have an arrangement assigned to them.
	 * 
	 * @param node
	 * 		the node component which subtree bounds are going to be recomputed
	 */
	protected void calcNewSubtreeBounds(NodeComponent node) {	
		node.getSubtreeBounds().setFrame(node.getBounds());
		
		if(!(node.getNode().isLeaf())) { // || node.isCollapsed())){
			// iterate over all children and start a recursion, so that children are positioned first
			for (NodeComponent c : node.children()) {	
				calcNewSubtreeBounds(c);	
				node.getSubtreeBounds().add(c.getSubtreeBounds());
			}
		}
		
		node.setSubtreeShape(new Rectangle2D.Double(
				node.getSubtreeBounds().x,
				node.getSubtreeBounds().y, 
				node.getSubtreeBounds().width, 
				node.getSubtreeBounds().height));
	}

}

