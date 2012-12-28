package domosaics.ui.views.domaintreeview.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.manager.CSAInSubtreeManager;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.renderer.arrangement.ArrangementRenderer;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.renderer.nodes.DefaultNodeRenderer;
import domosaics.ui.views.treeview.renderer.nodes.NodeRenderer;




/**
 * Class DefaultDomainNodeRenderer draws tree node components if they are visible.
 * If a node is collapsed the collapsed triangle is drawn, else its label
 * and if an arrangement is associated with the node, the arrangement is also drawn.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDomainNodeRenderer extends DefaultNodeRenderer {
	 
	/** the arrangement renderer being used to draw the associated arrangement for a tree node */
	protected ArrangementRenderer daRenderer; 
	
	/** the view to be rendered */
	protected DomainTreeViewI view;
	
	
	/**
	 * Constructor for a new DefaultDomainNodeRenderer
	 * 
	 * @param view
	 * 		the view to be rendered
	 */
	public DefaultDomainNodeRenderer(DomainTreeViewI view) {
		this.view = view;
		daRenderer = new BackBoneArrangementRenderer();
	}
	
	/**
	 * Sets a new arrangement renderer
	 * 
	 * @param ar
	 * 		the new arrangement renderer
	 */
	public void setArrangementRenderer(ArrangementRenderer ar) {
		daRenderer = ar;
	}
	
	/**
	 * Returns the currently used renderer used to render 
	 * the associated arrangements
	 * 
	 * @return
	 * 		currently used arrangement renderer
	 */
	public ArrangementRenderer getArrangementRenderer() {
		return daRenderer;
	}
	
	/**
	 * @see NodeRenderer
	 */
	public void renderNode(NodeComponent nc, TreeViewI view, Graphics2D g2) {
		// render the node normally
		super.renderNode(nc, view, g2);
		
		// if the node is in CSA mode draw its collapsing triangle
		if (this.view.getCSAInSubtreeManager().isInCSAMode(nc) && nc.isVisible()) {
			drawCsaTriangle(nc, g2);
			return;
		}
		
		// Now if there is a DA assigned draw it
		if (nc.getNode().isLeaf() && nc.getNode().hasArrangement()) { 
			ArrangementComponent dac = this.view.getArrangementComponentManager().getComponent(nc.getNode().getArrangement()); 
			if (dac.isVisible()) 
				daRenderer.renderArrangement(dac, this.view, g2);
		}
	}
	
	/**
	 * Helper method to draw the csa cubtree triangle
	 * 
	 * @param nc
	 * 		the tree node collapsed in csa mode
	 * @param g2
	 * 		the actual graphics context
	 */
	protected void drawCsaTriangle(NodeComponent nc, Graphics2D g2) {
		CSAInSubtreeManager csaManager = this.view.getCSAInSubtreeManager();
//		g2.setColor(Color.red);
//		g2.draw(csaManager.getSubtreeBounds(nc));

		// to make the rendering a bit more unugly determine the top and bottom arrangement
		ArrangementComponent top = null;
		ArrangementComponent bottom = null;
		
		for (ArrangementComponent dac : csaManager.getArrangements(nc)) {
			if (top == null) {
				top = dac;
				bottom = dac;
			} else {	
				if (dac.getY() < top.getY()) 
					top = dac;
				if (dac.getY() > bottom.getY()) 
					bottom = dac;
			}
		}
		
		// coordinates for start points of the triangle
		Font font = view.getTreeFontManager().getFont(nc);
		int labelSize = (nc.getLabel() != null) ? SwingUtilities.computeStringWidth(view.getViewComponent().getGraphics().getFontMetrics(font), nc.getLabel()) : 0 ;
		
		
		int x = nc.getX()+nc.getDisplayedShape().getBounds().width;
		int y = nc.getY();

//		int y1 = nc.getY()-nc.getDisplayedShape().getBounds().height/2;
//		int y2 = nc.getY()+nc.getDisplayedShape().getBounds().height/2;
		
		// calculate endpoints for lines
		int destX1 = top.getX() -5;
		int destX2 = bottom.getX() -5;
		int destY1 = (nc.getY() < top.getY()) ? nc.getY() : top.getY();
		int destY2 = (nc.getY() > bottom.getY()) ? nc.getY() : bottom.getY();
		
		// now print a triangle connecting the arrangements
		Color oldColor = g2.getColor();
		g2.setColor(Color.DARK_GRAY);
//		g2.drawLine(x , y1, destX1, destY1);
//		g2.drawLine(x , y2, destX2, destY2);
		
		g2.drawLine(x+labelSize+5, y, destX1,  y);
		
		
		g2.drawLine(destX1, destY1, destX2, destY2);
		g2.setColor(oldColor);
		
	}

}
