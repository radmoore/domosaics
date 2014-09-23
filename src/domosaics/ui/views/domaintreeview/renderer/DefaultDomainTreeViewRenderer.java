package domosaics.ui.views.domaintreeview.renderer;

import java.awt.Graphics2D;
import java.util.List;

import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.renderer.DomainViewRenderer;
import domosaics.ui.views.domainview.renderer.arrangement.ArrangementRenderer;
import domosaics.ui.views.treeview.renderer.DefaultTreeViewRenderer;
import domosaics.ui.views.treeview.renderer.edges.EdgeRenderer;
import domosaics.ui.views.treeview.renderer.edges.RectangleEdgeRenderer;
import domosaics.ui.views.treeview.renderer.nodes.NodeRenderer;




/**
 * The DefaultDomainTreeViewRenderer provides all methods necessary to
 * render a DomainTreeView. The {@link DefaultTreeViewRenderer} is extended
 * to by the functionality to draw the associated arrangements of a tree node.
 * <p>
 * The rendering of arrangements is delegated to an {@link ArrangementRenderer}.
 * The arrangement renderer can be changed during the applications
 * work flow. For instance if the rendering mode switches to MSA view
 * this renderer has to be changed.
 * <p>
 * In this case the renderer used to render the arrangements is a 
 * TreeNodeRenderer which renders the arrangements at the corresponding tree nodes.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDomainTreeViewRenderer extends DefaultTreeViewRenderer implements DomainViewRenderer {
	
	/** the view to be rendered */
	protected DomainTreeViewI view;
	
	/**
	 * Basic constructor initializing the view renderer.
	 * 
	 * @param view
	 * 		the view to render
	 */
	public DefaultDomainTreeViewRenderer(DomainTreeViewI view) {
		this (view, new DefaultDomainNodeRenderer(view), new RectangleEdgeRenderer());
	}

	/**
	 * Constructor for a new DefaultDomainTreeViewRenderer with 
	 * a node and edge renderer to be specified
	 * 
	 * @param view
	 * 		the view to be rendered
	 * @param nr
	 * 		the node renderer to be specified
	 * @param er
	 * 		the edge renderer to be specified
	 */
	public DefaultDomainTreeViewRenderer (DomainTreeViewI view, NodeRenderer nr, EdgeRenderer er) {
		super(view, nr, er);
		this.view = view;
	}
	
	/**
	 * Renders the tree normally using a DomainNodeRenderer to render 
	 * the arrangements at the corresponding nodes.
	 * 
	 * @see DefaultTreeViewRenderer
	 */
	@Override
	public void render(Graphics2D g) {
		super.render(g);

		if (view.getCSAInSubtreeManager().isInCSAMode(view.getNodesComponent(view.getTree().getRoot()))) {
			List<ArrangementComponent> daList = view.getCSAInSubtreeManager().getArrangements(view.getNodesComponent(view.getTree().getRoot()));
			for (int i = 0; i < daList.size(); i++)
				getArrangementRenderer().renderArrangement(daList.get(i), view, g);
		}
	}

	/**
	 * @see DomainViewRenderer
	 */
	@Override
	public ArrangementRenderer getArrangementRenderer() {
		return ((DefaultDomainNodeRenderer) getNodeRenderer()).getArrangementRenderer();
	}

	/**
	 * @see DomainViewRenderer
	 */
	@Override
	public void setArrangementRenderer(ArrangementRenderer daRenderer) {
		((DefaultDomainNodeRenderer) getNodeRenderer()).setArrangementRenderer(daRenderer);
	}

}
