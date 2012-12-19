package domosaics.ui.views.treeview.renderer;

import domosaics.ui.views.treeview.renderer.edges.EdgeRenderer;
import domosaics.ui.views.treeview.renderer.nodes.NodeRenderer;
import domosaics.ui.views.view.renderer.Renderer;

/**
 * The TreeViewRenderer interface defines the methods a tree view render 
 * has to implement. For further details look into {@link DefaultTreeViewRenderer}.
 * 
 * @author Andreas Held
 *
 */
public interface TreeViewRenderer extends Renderer{

	/**
	 * Sets a new NodeRenderer
	 * 
	 * @param nodeRenderer 
	 * 		the new node renderer to set
	 */
	public void setNodeRenderer(NodeRenderer nodeRenderer);

	/**
	 * Returns the NodeRenderer
	 * 
	 * @return 
	 * 		the actually used node render
	 */
	public NodeRenderer getNodeRenderer();

	/**
	 * Sets a new EdgeRenderer
	 * 
	 * @param edgeRenderer 
	 * 		the new edge renderer to set
	 */
	public void setEdgeRenderer(EdgeRenderer edgeRenderer);

	/**
	 * Returns the EdgeRenderer
	 * 
	 * @return 
	 * 		the actually used edge render
	 */
	public EdgeRenderer getEdgeRenderer();

}
