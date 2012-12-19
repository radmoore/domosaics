package domosaics.ui.views.treeview.renderer.additional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Collection;

import domosaics.model.tree.TreeEdgeI;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.renderer.Renderer;


/**
 * EdgeSelectionRenderer renders selected edges and can be added 
 * additionally to a tree view.
 * The selected edges are rendered using the selection color and 
 * a bigger stroke.
 * 
 * @author Andreas Held
 *
 */
public class EdgeSelectionRenderer implements Renderer {

	/** the tree view supporting the feature */
	protected TreeViewI view;
	
	
	/**
	 * Constructor for a new HighlightEdgeRenderer
	 * 
	 * @param view
	 * 		the tree view supporting the feature
	 */
	public EdgeSelectionRenderer(TreeViewI view) {
		this.view = view;
	}
	
	public void render(Graphics2D g2) {
		Collection<TreeEdgeI> edges = view.getTreeSelectionManager().getSelectedEdges();
		for (TreeEdgeI edge : edges) {
			NodeComponent parent = view.getTreeComponentManager().getComponent(edge.getSource());
			NodeComponent child = view.getTreeComponentManager().getComponent(edge.getTarget());
			hightlightEdge(parent, child, g2);
		}
	}
	
	public void hightlightEdge(NodeComponent parent, NodeComponent child, Graphics2D g) {
		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();
		
		// if edge is selected draw a bigger line
		g.setColor(view.getTreeColorManager().getSelectionColor());          
		BasicStroke st = (BasicStroke) view.getTreeStrokeManager().getEdgeStroke(child.getNode().getEdgeToParent());
		g.setStroke(new BasicStroke(st.getLineWidth()+2, st.getEndCap(), st.getLineJoin(), st.getMiterLimit(), st.getDashArray(), st.getDashPhase()));

		view.getTreeViewRenderer().getEdgeRenderer().drawEdge(parent.getX(), parent.getY(), child.getX(), child.getY(), view, g);
		
		g.setColor(oldColor);
		g.setStroke(oldStroke);
	}
}
