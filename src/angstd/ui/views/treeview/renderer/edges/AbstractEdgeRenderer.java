package angstd.ui.views.treeview.renderer.edges;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;



/**
 * Abstract class AbstractdgeRenderer initiates the rendering process for 
 * edges.
 * <p>
 * The graphic properties like line stroke and color are set before the 
 * rendering is delegated to implementing subclasses, like 
 * {@link RectangleEdgeRenderer}.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public abstract class AbstractEdgeRenderer implements EdgeRenderer {
	
	/**
	 * edge label to draw
	 */
	protected String label = "";
	
	/**
	 * formatter which formats the edge label in decimal font with three digits
	 */
	protected NumberFormat formatter = DecimalFormat.getNumberInstance(Locale.ENGLISH); 
	{		
		formatter.setMaximumFractionDigits(3);
	}
	
	/**
	 * Sets the graphic properties for the edge and delegates to drawEdge()
	 * for the actual drawing. <br>
	 * Then its checked if also the labels have to be drawn and if so the format 
	 * is set for the graphics context before again the actual drawing process
	 * is delegated to drawLabel.
	 */
	public void renderEdge(NodeComponent p, NodeComponent c, TreeViewI view, Graphics2D g, Color color) {	
		// save last color and stroke settings
		Color oldColor = g.getColor();         
		Stroke oldStroke = g.getStroke();
		
		// set new color / stroke
		g.setColor(view.getTreeColorManager().getEdgeColor(c.getNode().getEdgeToParent()));
		g.setStroke(view.getTreeStrokeManager().getEdgeStroke(c.getNode().getEdgeToParent()));
		
		// draw the edge
		drawEdge(p.getX(), p.getY(), c.getX(), c.getY(), view, g);
		
		// revert color for edge labels
		g.setColor(view.getTreeColorManager().getEdgeLabelColor());
		
		// draw the edges label if necessary
		if (view.getTreeLayoutManager().isShowBootstrap()) {
			if (c.getNode().getBootstrap() != -1)
				label = formatter.format(c.getNode().getBootstrap());
			else
				label = "";
			g.setFont(Font.decode("Arial-12"));
			drawLabel(p.getX(),p.getY(), c.getX(), c.getY(), view, g);
		}
		
		if(view.getTreeLayoutManager().isDrawEdgeWeights()){
			label = formatter.format(c.getNode().getDistanceToParent());
			g.setFont(Font.decode("Arial-12"));
			drawLabel(p.getX(),p.getY(), c.getX(), c.getY(), view, g);
		}

		// restore old color / stroke
		g.setColor(oldColor);
		g.setStroke(oldStroke);

	}
	
	/**
	 * @see EdgeRenderer
	 */
	public abstract void drawEdge(int x1, int y1, int x2, int y2, TreeViewI view, Graphics2D g);
	
	/**
	 * @see EdgeRenderer
	 */
	public abstract void drawLabel(int x1, int y1, int x2, int y2, TreeViewI view, Graphics2D g);

}
