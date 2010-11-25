package angstd.ui.views.treeview.renderer.additional;

import java.awt.Color;
import java.awt.Graphics2D;

import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.view.renderer.Renderer;

/**
 * NodeSelectionRenderer renders the selection mark for nodes and can be
 * added additionally to a tree view.
 * The selected nodes are rendered using a filled circle.
 * 
 * @author Andreas Held
 *
 */
public class NodeSelectionRenderer implements Renderer {

	/** the tree view supporting the feature */
	protected TreeViewI view;
	
	
	/**
	 * Constructor for a new SelectionRectangleRenderer
	 * 
	 * @param view
	 * 		the tree view supporting the feature
	 */
	public NodeSelectionRenderer(TreeViewI view) {
		this.view = view;
	}
	
	/**
	 * @see Renderer
	 */
	public void render(Graphics2D g2) {
		Color oldColor = g2.getColor();
		
		for (NodeComponent node : view.getTreeSelectionManager().getSelection()) {
			g2.setColor(Color.red);
			g2.fill(node.getDisplayedShape());
			g2.setColor(Color.black);
			g2.draw(node.getDisplayedShape());
		}
		
		g2.setColor(oldColor);
	}
}
