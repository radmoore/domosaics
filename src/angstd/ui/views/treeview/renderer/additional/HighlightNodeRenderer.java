package angstd.ui.views.treeview.renderer.additional;

import java.awt.Color;
import java.awt.Graphics2D;

import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.view.renderer.Renderer;

/**
 * HighlightNodeRenderer highlights a node component when the user
 * is mouse over on it. The renderer can be added additionally to a 
 * tree view.
 * The highlighting is done by rendering a blue circle.
 * 
 * @author Andreas Held
 *
 */
public class HighlightNodeRenderer implements Renderer {

	/** the tree view supporting the feature */
	protected TreeViewI view;
	
	
	/**
	 * Constructor for a new HighlightNodeRenderer
	 * 
	 * @param view
	 * 		the tree view supporting the feature
	 */
	public HighlightNodeRenderer(TreeViewI view) {
		this.view = view;
	}
	
	/**
	 * @see renderer
	 */
	public void render(Graphics2D g2) {
		if (view.getTreeSelectionManager().getMouseOverComp() != null)
			hightlightNode(view.getTreeSelectionManager().getMouseOverComp(), g2);
	}
	
	/**
	 * Helper method which actually highlights the node
	 * 
	 * @param nc
	 * 		the node component to highlight
	 * @param g
	 * 		the actual graphics context
	 */
	public void hightlightNode(NodeComponent nc, Graphics2D g) {
		Color oldColor = g.getColor();
		
		g.setColor(Color.BLUE);
		g.drawArc(nc.getX()-5, nc.getY()-5, 10, 10, 0, 360);
		
		g.setColor(oldColor);
	}
}
