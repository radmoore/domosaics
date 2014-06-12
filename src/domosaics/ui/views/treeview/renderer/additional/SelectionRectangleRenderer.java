package domosaics.ui.views.treeview.renderer.additional;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * SelectionRectangleRenderer renders the selection rectangle which can be used
 * to select tree components. This renderer can be
 * added additionally to a tree view.
 * 
 * @author Andreas Held
 *
 */
public class SelectionRectangleRenderer implements Renderer {

	/** the tree view supporting the feature */
	protected TreeViewI view;
	
	
	/**
	 * Constructor for a new SelectionRectangleRenderer
	 * 
	 * @param view
	 * 		the tree view supporting the feature
	 */
	public SelectionRectangleRenderer(TreeViewI view) {
		this.view = view;
	}
	
	/**
	 * @see renderer
	 */
	@Override
	public void render(Graphics2D g) {
		if (view.getTreeMouseController().getSelectionRectangle() == null)
			return;
		Color oldColor = g.getColor();
		g.setColor(Color.blue);
		Rectangle r = view.getTreeMouseController().getSelectionRectangle();
		g.drawRect(r.x, r.y, r.width, r.height);  
		g.setColor(oldColor);
	}
	
	
}
