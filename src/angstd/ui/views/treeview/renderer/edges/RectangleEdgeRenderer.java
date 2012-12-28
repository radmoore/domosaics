package angstd.ui.views.treeview.renderer.edges;

import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

import angstd.ui.util.StringUIUtils;
import angstd.ui.views.treeview.TreeViewI;



/**
 * The RectangleEdgeRenderer extends AbstractEdgeRenderer to draw rectangeled 
 * edges used for a dendogram layout.
 * <p>
 * This class implements only the actual drawing operation.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class RectangleEdgeRenderer extends AbstractEdgeRenderer {

	/**
	 * Draws the edge between two nodes by drawing two lines.
	 */
	@Override
	public void drawEdge(int x1, int y1, int x2, int y2, TreeViewI view, Graphics2D g) {
		g.drawLine(x1,y1,x1,y2);
		g.drawLine(x1,y2,x2,y2);
	}

	/**
	 * Draws the label of a edge if there is enough room for it
	 */
	@Override
	public void drawLabel(int x1, int y1, int x2, int y2, TreeViewI view, Graphics2D g) {
		// calculate label height
		int height = Math.abs(y1-y2)-1;
		if( height< g.getFont().getSize()){
			if(height > 5){
				g.setFont(g.getFont().deriveFont((float)height));
			}else{
				return;
			}
		}
		
		// check if there is enough space to draw the label
		int maxLen = Math.abs(x1-x2)-4;
		int actLen = SwingUtilities.computeStringWidth(g.getFontMetrics(), label);
		if (actLen > maxLen)
			label = StringUIUtils.clipStringIfNecessary(view.getViewComponent(), g.getFontMetrics(), label, maxLen);
		
		if(label.equals("...")) 
			return;
	
		g.drawString(label, x1 + 3, (int) (y1 < y2 ? y2 -1 : y2-1));
	}

}
