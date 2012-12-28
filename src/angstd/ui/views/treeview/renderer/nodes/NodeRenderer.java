package angstd.ui.views.treeview.renderer.nodes;

import java.awt.Graphics2D;

import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;



/**
 * Interface NodeRenderer which renders a NodeComponent within a tree.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public interface NodeRenderer {

	/**
	 * This should do the node rendering.
	 * 
	 * @param node 
	 * 		the node to render
	 * @param view
	 * 		 view on which is rendered
	 * @param g  
	 * 		graphics context used for rendering
	 */
	public void renderNode(NodeComponent node, TreeViewI view, Graphics2D g);
		
}
