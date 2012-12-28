package domosaics.ui.views.treeview.components;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domosaics.ui.views.treeview.TreeViewI;




/**
 * NodeComponentDetector is a helper class to detect node components within
 * a tree view. FOr instance if the user clicks, this class can be used
 * if a node component is at the actual cursor position.
 * Therefore this class is used together with mouse controllers.
 * 
 * @author Andreas Held
 *
 */
public class NodeComponentDetector  {

	/** the tree view being controlled */
	protected TreeViewI view;

	/**
	 * Constructor for a new NodeComponentDetector
	 * 
	 * @param view
	 * 		the tree view being controlled 
	 */
	public NodeComponentDetector(TreeViewI view){
		this.view = view; 
	}
	
	/**
	 * Returns a list of all node components within an recngular shaped
	 * selection area.
	 * 
	 * @param r
	 * 		The area in which node components should be detected
	 * @return
	 * 		all node components being contained by the specified rectangle
	 */
	public List<NodeComponent> searchNodeComponents(Rectangle r) {
		List<NodeComponent> res = new ArrayList<NodeComponent>();
		
		Iterator<NodeComponent> iter = view.getTreeComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			NodeComponent nc = iter.next();
			
			if (!nc.isVisible())
				continue;

			// check if cursor is within circular node shape
			if (r.contains(nc.getDisplayedShape().getBounds())) {
				res.add(nc);
			}
		}
		return res;
	}
		
	/**
	 * Returns whether or not a node component is at the specified position
	 * 
	 * @param p
	 * 		the position to check for a node component
	 * @return
	 * 		whether or not a node component is at the specified position
	 */
	public NodeComponent searchNodeComponent(Point p) {
		Iterator<NodeComponent> iter = view.getTreeComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			NodeComponent nc = iter.next();
				
			if (!nc.isVisible())
				continue;
				
			// check if cursor is within circular node shape
			if (nc.getDisplayedShape().contains(p)) {
				return nc;
			}
		}	
		return null;
	}
		
}
