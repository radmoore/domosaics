package domosaics.ui.views.treeview.renderer.additional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import domosaics.model.tree.TreeI;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * TreeLinealRenderer renders a legend for a tree view showing the 
 * summed up distances (edge weights). This renderer can be added  
 * additionally to a tree view.
 * 
 * @author Andreas Held (loosely based on the EPOS code by Thasso Griebel - thasso@minet.uni-jena.de)
 *
 */
public class TreeLinealRenderer implements Renderer {
	
	/** the tree view supporting the feature */
	protected TreeViewI view;
	
	/** the position of the leaf farest away from the root */
	protected Point leafPosition;
	
	/** the root position  */
	protected Point rootPosition;
	
	/** the root component (more precisely the node most left) */
	protected NodeComponent start;
	
	/** the node component most to the right */
	protected NodeComponent end;
	
	/** color used to draw the lines */
	protected Color lineColor = Color.black;
	
	/** color used to draw the ticks */
	protected Color tickColor = Color.black;	
	
	/** stroke for the lines */
	protected float lineStroke = 1f;
	
	/** number of ticks */
	protected int ticks = 4;
	
	/** the length of ticks (vertical) */
	protected int tickLength = 10;
	
	/** the font used to draw the tick labels */
	protected Font tickFont = new Font("Arial", 0, 12);
	
	
	/**
	 * Constructor for a new TreeLinealRenderer
	 * 
	 * @param view
	 * 		the tree view supporting the feature
	 */
	public TreeLinealRenderer(TreeViewI view) {
		this.view = view;
	}

	/** @see Renderer */
	public void render(Graphics2D g2) {
		if (!view.getTreeLayoutManager().isShowLegend())
			return;
		
		Stroke oldStroke = g2.getStroke();
		Color oldColor = g2.getColor();
		Font oldFont = g2.getFont();
		
		initDataFromTree();

		double dx = 0, dy = 0;

		rootPosition.y = view.getViewComponent().getSize().height -15; 

			dx = Math.abs(rootPosition.x - leafPosition.x);
			dy = 0;			
		
		g2.setStroke(new BasicStroke(lineStroke));
		g2.setColor(lineColor);
		g2.drawLine(rootPosition.x, rootPosition.y, (int) (rootPosition.x + dx), (int)(rootPosition.y + dy));
		g2.setColor(tickColor);
		g2.setFont(tickFont);
		
		double length = getPathLength();
		NumberFormat format = DecimalFormat.getInstance();
		format.setMaximumFractionDigits(2);
		for(double i=0; i<= ticks; i++){
			double x = (rootPosition.x + (i*(dx/ticks))); 
			double y = (rootPosition.y + (i*(dy/ticks)));
			double x2 = x;
			double y2 = y;

			x2 = x;
			y2 = y + tickLength;			

			g2.drawLine((int)x, (int)y, (int)x2,(int)y2);
			
			
			// draw the label
			String l = format.format(i*(length/ticks));
			g2.drawString(l,(int) (x2+2), (int)(y2+3));
		}
		
		g2.setStroke(oldStroke);
		g2.setColor(oldColor);
		g2.setFont(oldFont);
	}
	
	/**
	 * Helper method to initialize needed parameters 
	 */
	private void initDataFromTree() {
		TreeI t= view.getTree();
		
		if(t == null) 
			return;
		
		start = view.getNodesComponent(t.getRoot());
		Point rootPosition = view.getNodesComponent(t.getRoot()).getLocation();
		
		Point leafPosition = rootPosition;
		NodeComponent lComp = view.getNodesComponent(t.getRoot());
		double dist = leafPosition.distance(rootPosition);
		for (NodeComponent c : lComp.depthFirstIterator()) {
			double d = 0;
				d =Math.abs(rootPosition.x - c.getLocation().x);

			if(d > dist){
				dist = d;
				leafPosition = c.getLocation();
				lComp = c;
			}
		}
		
		this.end = lComp;
		this.rootPosition = rootPosition;
		this.leafPosition = leafPosition;
				
	}
	
	/**
	 * Helper method to get the longest path within the tree
	 * 
	 * @return
	 * 		longest path within the tree
	 */
	private double getPathLength(){
		double p = 0;
		NodeComponent c = end;
		while(c.getParent() != null){
			p += c.getNode().getDistanceToParent();
			c = c.getParent();
			if(c == start){
				break;
			}
		}
		return p;
	}

}
