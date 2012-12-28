package domosaics.ui.views.treeview.renderer.additional;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.renderer.Renderer;


/**
 * InnerNodeRenderer renders inner nodes as circles so they can be
 * identified more easily by the user. This renderer can be
 * added additionally to a tree view.
 * The selected nodes are rendered using circles.
 * 
 * @author Andreas Held
 *
 */
public class InnerNodeRenderer implements Renderer {

	/** the tree view supporting the feature */
	protected TreeViewI view;
	
	/**
	 * Constructor for a new InnerNodeRenderer
	 * 
	 * @param view
	 * 		the tree view supporting the feature
	 */
	public InnerNodeRenderer(TreeViewI view) {
		this.view = view;
	}
	
	/**
	 * @see Renderer
	 */
	public void render(Graphics2D g2) {
		if (!view.getTreeLayoutManager().isShowInnerNodes())
			return;
		
		Color oldColor = g2.getColor();
		
		Iterator<NodeComponent> iter = view.getTreeComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			NodeComponent node = iter.next();
			if(node.getLabel() == null || node.getLabel().length() == 0) {
				g2.setColor(Color.white);
				g2.fill(node.getDisplayedShape());
				g2.setColor(Color.black);
				g2.draw(node.getDisplayedShape());
			}
		}
		
		g2.setColor(oldColor);
		
	}
}
