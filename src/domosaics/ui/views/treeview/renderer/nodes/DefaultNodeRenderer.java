package domosaics.ui.views.treeview.renderer.nodes;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

import domosaics.ui.util.StringUIUtils;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;


/**
 * Class DefaultNodeRenderer draws tree node components if they are visible.
 * If a node is collapsed the collapsed triangle is drawn, else its label.
 * <p>
 * If the label exceeds the height of the actual set font for the node, its 
 * not rendered at all. If the width exceeds the space, DoMosaics tries to
 * clip it using "...". If this clipping results in "..." for the label 
 * its also not drawn.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class DefaultNodeRenderer implements NodeRenderer{
	 
	/**
	 * Renders the node. If a node is collapsed the collapsed 
	 * triangle is drawn, else its label. If inner nodes should be
	 * rendered, a circle is rendered.
	 */
	public void renderNode(NodeComponent node, TreeViewI view, Graphics2D g2) {
		if (!node.isVisible())
			return;
			
		Color oldColor = g2.getColor();
		
		// get needed properties from the node component
		int x = node.getX();
		int y = node.getY();
		int height = node.getHeight();
		
		// set the color for this node
		g2.setColor(view.getTreeColorManager().getNodeColor(node));
	
		int cr = 0;	// used height for collapsed nodes
		
		// if node is collapsed draw the collapsed symbol
		if(node.isCollapsed()){
			// if the height exceeds 12 use the maximal height of 12 else the real height
			cr = height > 12 ? 12 : height;
			
			// fill the collapsed symbol
			g2.setColor(Color.red);
			g2.fillPolygon(new int[]{x,x+cr,x+cr,x}, new int[]{y,y-(cr/2), y+(cr/2),y}, 4);

			// redraw the edge of the collapsed symbol
			g2.setColor(Color.black);
			g2.drawPolygon(new int[]{x,x+cr,x+cr,x}, new int[]{y,y-(cr/2), y+(cr/2),y}, 4);
		}

		// draw label
		String label = node.getLabel(); 
		
		// but only if there is one
		if(label == null || label.length() == 0) {
			g2.setColor(oldColor);
			return;
		}
		
		// and only if there is enough space to draw the labels with the given font
		g2.setFont(view.getTreeFontManager().getFont(node));
		if(height < g2.getFont().getSize2D()) {
			g2.setColor(oldColor);
			return;
		}
		
		// clip label if necessary
		int maxLen = (int) node.getBounds().getWidth()-4;
		int actLen = SwingUtilities.computeStringWidth(g2.getFontMetrics(), label);
		if (actLen > maxLen)
			label = StringUIUtils.clipStringIfNecessary(view.getViewComponent(), g2.getFontMetrics(), label, maxLen);
		
		if(label.equals("...")) {
			g2.setColor(oldColor);
			return;
		}
		
		// draw the label
		g2.setColor(view.getTreeColorManager().getNodeColor(node));
		int y2 = (int) (y + (g2.getFont().getSize2D() / 2.0));
		g2.drawString(label, x+2+cr, y2-1);
		
		g2.setColor(oldColor);
	}

}
