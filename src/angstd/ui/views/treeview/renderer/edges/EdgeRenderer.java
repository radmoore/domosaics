package angstd.ui.views.treeview.renderer.edges;

import java.awt.Color;
import java.awt.Graphics2D;

import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;

/**
 * Interface EdgeRenderer is used to render edges 
 * connecting a specified parent node with its child.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public interface EdgeRenderer {

	/**
	 * This method gathers the needed edge properties and then delegates to 
	 * drawEdge() to do the actual rendering
	 * 
	 * @param parent 
	 * 		parent node component of the edge
	 * @param child 
	 * 		child node component of the edge
	 * @param view 
	 * 		view on which is rendered
	 * @param g 
	 * 		graphics context used for rendering
	 * @param c 
	 * 		edge color
	 */
	public void renderEdge(NodeComponent parent, NodeComponent child, TreeViewI view, Graphics2D g, Color c);
	
	/**
	 * this should do the actual edge drawing
	 * 
	 * @param x1 	
	 * 		x coordinate of the parent
	 * @param y1 
	 * 		y coordinate of the parent
	 * @param x2 
	 * 		x coordinate of the child
	 * @param y2 
	 * 		y coordinate of the child
	 * @param view 
	 * 		the view on which is rendered
	 * @param g 
	 * 		graphics context used for rendering
	 */
	public void drawEdge(int x1, int y1, int x2, int y2, TreeViewI view, Graphics2D g);

	/**
	 * this should draw the edge label
	 * 
	 * @param x1 
	 * 		x coordinate of the parent
	 * @param y1 
	 * 		y coordinate of the parent
	 * @param x2 
	 * 		x coordinate of the child
	 * @param y2 
	 * 		x coordinate of the child
	 * @param view 
	 * 		the view on which is rendered
	 * @param g 
	 * 		graphics context used for rendering
	 */
	public void drawLabel(int x1, int y1, int x2, int y2, TreeViewI view, Graphics2D g);
}
