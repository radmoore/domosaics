package angstd.ui.views.treeview.manager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import angstd.model.tree.TreeEdgeI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.view.manager.DefaultViewManager;

/**
 * TreeColorManager defines the color mapping between node components and
 * tree edges to colors. This class extends DefaultViewManager to ensure 
 * the communication with the managed view.
 * 
 * @author Andreas Held
 *
 */
public class TreeColorManager extends DefaultViewManager {

	/** maps a node to a color, e.g. used for node labels*/
	protected Map<NodeComponent, Color> nodes2colors;
	
	/** maps Tree edges to colors, no graphical component exist for edges so the backend data structure is taken */
	protected Map<TreeEdgeI, Color> edges2colors;
	
	/** actual selection color (e.g. to preview a selected color */
	protected Color selectionColor = Color.red;

	/** default node color */
	protected Color nodeDefault = Color.black;
	
	/** default edge color */
	protected Color edgeDefault = Color.black;
	
	/** default edge label color */
	protected Color edgeLabelColor = Color.black;
	
	/** the default selection color */
	protected Color defaultSelectionColor = Color.red;
	

	/**
	 * Constructor for a new TreeColorManager
	 */
	public TreeColorManager() {
		nodes2colors = new HashMap<NodeComponent, Color>();
		edges2colors = new HashMap<TreeEdgeI, Color>();
	}
	
	/**
	 * Sets the color for a tree edge
	 * 
	 * @param edge
	 * 		the tree edge which color is going to be changed
	 * @param color
	 * 		the new color for the tree edge
	 */
	public void setEdgeColor(TreeEdgeI edge, Color color) {
		edges2colors.put(edge, color);
		visualChange();
	}
	
	/**
	 *  Sets the color for a tree edge (e.g. for labels)
	 *  
	 * @param nc
	 * 		the tree node which color is going to be changed
	 * @param color
	 * 		the new color for the tree node
	 */
	public void setNodeColor(NodeComponent nc, Color color) {
		nodes2colors.put(nc, color);
		visualChange();
	}	
	
	/**
	 * Sets the color for selected items, this color is also used
	 * to preview a selected color
	 * 
	 * @param color
	 * 		the color used to color the actual selected items
	 */
	public void setSelectionColor(Color color) {
		selectionColor = color;
		visualChange();
	}
	
	/**
	 * Returns the actually used selection color
	 * 
	 * @return
	 * 		actually used selection color
	 */
	public Color getSelectionColor() {
		return selectionColor;
	}
  
	/**
	 * Returns the default selection color
	 * 
	 * @return
	 * 		default selection color
	 */
	public Color getDefaultSelectionColor() {
		return defaultSelectionColor;
	}

	/**
	 * Returns the color for a node component.
	 * 
	 * @param nc
	 * 		the node component which color is requested
	 * @return
	 * 		the color of the specified node component
	 */
  	public Color getNodeColor(NodeComponent nc) {
   		if (nodes2colors.get(nc) == null)
   			return getDefaultNodeColor();
   		else
   			return nodes2colors.get(nc);
   	}
	
  	/**
  	 * Returns the default node color
  	 * 
  	 * @return
  	 * 		default node color
  	 */
	public Color getDefaultNodeColor() {
		return nodeDefault;
	}
  	
	/**
	 * Returns the color for a tree edge.
	 * 
	 * @param edge
	 * 		the tree edge which color is requested
	 * @return
	 * 		the color of the specified tree edge
	 */
  	public Color getEdgeColor(TreeEdgeI edge) {
   		if (edges2colors.get(edge) == null)
   			return getDefaultEdgeColor();
   		else
   			return edges2colors.get(edge);
   	}
	
  	/**
  	 * Returns the default edge color
  	 * 
  	 * @return
  	 * 		default edge color
  	 */
	public Color getDefaultEdgeColor() {
		return edgeDefault;
	}
	
	/**
	 * Returns the default color used to colorize edge labels
	 * 
	 * @return
	 * 		default color used to colorize edge labels
	 */
	public Color getEdgeLabelColor() {
		return edgeLabelColor;
	}
	


}
