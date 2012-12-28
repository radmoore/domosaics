package domosaics.ui.tools.domaingraph.components;

import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;


/** 
 *  Class MyColorActions defines the colorization for the nodes 
 *  within the prefuse graph.
 *  
 * @author Andreas Held
 *
 */
public class ColorActions {
		
	/** the text color for node labels */
	protected ColorAction nodeTextColor;
	
	/** the stroke color for nodes */
	protected ColorAction nodeStrokeColor;
	
	/** the fill color for nodes */
	protected ColorAction nodeFillColor;
	
	/** the stroke color for edges */
	protected ColorAction edgeLineColor;
	
	
	/**
	 * Constructor to define and initialize the ColorActions used by the
	 * prefuse graph.
	 * 
	 * @param manager
	 * 		the layout manager
	 */
	public ColorActions (final GraphLayoutManager manager) { 
		nodeTextColor = new ColorAction("graph.nodes",	VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0));

        nodeStrokeColor = new ColorAction("graph.nodes", VisualItem.STROKECOLOR) {
        	public int getColor(VisualItem item) {
            	if (item.isHover())
            		return ColorLib.rgb(255, 0, 0);
            	else if (item.isInGroup(Visualization.SELECTED_ITEMS))
            		return ColorLib.rgb(0, 0, 255);
            	else {
            		if (manager.isDrawDomainShapes())
            			return ColorLib.rgb(255, 255, 255);
            		else
            			return ColorLib.rgb(0, 0, 0);
            	}
         }};

        
         nodeFillColor = new ColorAction("graph.nodes", VisualItem.FILLCOLOR) {
         	public int getColor(VisualItem item) {
         		// colorMode 0 and 1
             	if (item.getInt("status") == 0) // gray
             		return ColorLib.rgb(234,234,234);
             	else if (item.getInt("status") == 1) // green
             		return ColorLib.rgb(60, 255, 60);//200,255,200);
              	else
             		return ColorLib.rgb(234,234,234);
      
         	}};
      
  
       edgeLineColor = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
	}
	
	/**
	 * Returns the node text color
	 * 
	 * @return
	 * 		node text color	
	 */
	public ColorAction getNodeTextColor () {
		return nodeTextColor;
	}
	
	/**
	 * Returns the node stroke color
	 * 
	 * @return
	 * 		node stroke color	
	 */
	public ColorAction getNodeStrokeColor () {
		return nodeStrokeColor;
	}
	
	/**
	 * Returns the node fill color
	 * 
	 * @return
	 * 		node fill color	
	 */
	public ColorAction getNodeFillColor () {
		return nodeFillColor;
	}
	
	/**
	 * Returns the edge stroke color
	 * 
	 * @return
	 * 		edge stroke color	
	 */
	public ColorAction getEdgeLineColor () {
		return edgeLineColor;
	}

}

