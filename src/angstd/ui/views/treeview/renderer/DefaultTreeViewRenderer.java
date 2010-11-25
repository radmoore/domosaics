package angstd.ui.views.treeview.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import angstd.model.tree.TreeNodeI;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.treeview.renderer.edges.EdgeRenderer;
import angstd.ui.views.treeview.renderer.edges.RectangleEdgeRenderer;
import angstd.ui.views.treeview.renderer.nodes.DefaultNodeRenderer;
import angstd.ui.views.treeview.renderer.nodes.NodeRenderer;

/**
 * Class DefaultTreeViewRenderer manages the rendering of a tree view.
 * Therefore it has a {@link NodeRenderer} and a {@link EdgeRenderer}
 * assigned to it, which draw the tree components. 
 * <p>
 * There various methods to change node and edge renderer as well as 
 * constructors to initialize the tree view renderer with the correct 
 * component renderer. 
 * <p>
 * The actual drawing is started using the method render().
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *  
 */
public class DefaultTreeViewRenderer implements TreeViewRenderer  {
	
	/**
	 * The view to render
	 */
	protected TreeViewI view;
	
	/**
	 * Renderer to draw edges
	 */
	protected EdgeRenderer edgeRenderer;
	
	/**
	 * Renderer to draw nodes
	 */
	protected NodeRenderer nodeRenderer;
	
	
	/**
	 * Constructs a new TreeViewRenderer using the Default NodeRenderer 
	 * and a RectangleEdgeRenderer for dendogram layouts.
	 * 
	 * @param view 
	 * 		the view which should be rendered
	 */
	public DefaultTreeViewRenderer (TreeViewI view) {
		this (view, new DefaultNodeRenderer(), new RectangleEdgeRenderer());
	}
	
	/**
	 * Constructs a new TreeViewRenderer using the specified NodeRenderer 
	 * and a RectangleEdgeRenderer for dendogram layouts.
	 * 
	 * @param view 
	 * 		the view which should be rendered
	 * @param nr 
	 * 		the specified node renderer
	 */
	public DefaultTreeViewRenderer (TreeViewI view, NodeRenderer nr) {
		this (view, nr, new RectangleEdgeRenderer());
	}
	
	/**
	 * Constructs a new TreeViewRenderer using the Default NodeRenderer and
	 * a specified EdgeRenderer.
	 * 
	 * @param view 
	 * 		the view which should be rendered
	 * @param er 
	 * 		the specified edge renderer
	 */
	public DefaultTreeViewRenderer (TreeViewI view, EdgeRenderer er) {
		this (view, new DefaultNodeRenderer(), er);
	}
	
	/**
	 * Constructs a new TreeViewRenderer using the specified NodeRenderer 
	 * and a specified EdgeRenderer.
	 * 
	 * @param view 
	 * 		the view to render
	 * @param nr 
	 * 		the specified node renderer
	 * @param er 
	 * 		the specified edge renderer
	 */
	public DefaultTreeViewRenderer (TreeViewI view, NodeRenderer nr, EdgeRenderer er) {
		this.view = view;
		this.edgeRenderer = er;
		this.nodeRenderer = nr;
	}
	
	/**
	 * Sets a new NodeRenderer
	 * 
	 * @param nodeRenderer 
	 * 		the new node renderer to set
	 */
	public void setNodeRenderer(NodeRenderer nodeRenderer) {
		this.nodeRenderer = nodeRenderer;
	}
	
	/**
	 * Returns the NodeRenderer
	 * 
	 * @return 
	 * 		the actually used node render
	 */
	public NodeRenderer getNodeRenderer() {
		return nodeRenderer;
	}
	
	/**
	 * Sets a new EdgeRenderer
	 * 
	 * @param edgeRenderer 
	 * 		the new edge renderer to set
	 */
	public void setEdgeRenderer(EdgeRenderer edgeRenderer) {
		this.edgeRenderer = edgeRenderer;
	}
	
	/**
	 * Returns the EdgeRenderer
	 * 
	 * @return 
	 * 		the actually used edge render
	 */
	public EdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}
	
	/* ******************************************************************* *
	 *   						 Renderering methods					   *
	 * ******************************************************************* */
	
	/**
	 * Gets in a first step the clipping area. Then renders the background
	 * of the view before rendering recursively the tree.
	 */
	public void render(Graphics2D g) {
		// get clip bounds or visible rectangle
		Rectangle r = g.getClipBounds();
		if (r == null) 
			r = view.getViewComponent().getVisibleRect();

		// render the background
		g.setColor(Color.white);
		g.fill(r);
		
		// render recursively the tree
		renderTree(g, r);
	}
	
	/**
	 * Starts a recursive rendering procedure using the renderingtraversal()
	 * method and starting with the trees root node.
	 * 
	 * @param g 
	 * 		graphics object.
	 * @param clip 
	 * 		clipping area in which the drawing is done
	 */
	private void renderTree(Graphics2D g, Rectangle clip) {
		// create a graphics 2D object and take the clipping area
		g.setClip(clip.x, clip.y, clip.width, clip.height);	
		
		// get the tree node as NodeComponent and start the recursive rendering process
		if (view.getTree() != null) {
			TreeNodeI root = (TreeNodeI) view.getTree().getRoot();
			NodeComponent rn = view.getTreeComponentManager().getComponent(root);
			renderingtraversal(rn, clip, g);
		}
	}
	
	/**
	 * Recursive rendering procedure for tree components. 
	 * Edges are rendered first and nodes last, so that nodes always 
	 * overlay edges. Subtrees are only traversed if they intersect 
	 * with the clipping area.
	 * 
	 * @param n 
	 * 		actual node to render
	 * @param clip 
	 * 		clipping area
	 * @param g2d 
	 * 		graphics object used for rendering
	 */
	private void renderingtraversal(NodeComponent n, Rectangle clip, Graphics2D g2d) {
	
		// go through the children of this node
		for (NodeComponent c : n.children()) {
			
			// render the edge form parent to actual child
			if (n.isVisible() && !n.isCollapsed())
				edgeRenderer.renderEdge(n, c, view, g2d, Color.black);

			// traverse subtree if it is within clipping area and needed
//TODO			if (c.getSubtreeShape().intersects(clip)) //|| c.getSubtreeShape().getBounds2D().getHeight() == 0.0) 
				renderingtraversal(c, clip, g2d);
		}
		
		// render node if it is within the clipping area last, so that it overlay edges 
//TODO		if (n.getBoundingShape().intersects(clip))
			nodeRenderer.renderNode(n, view, g2d);
	}

}
