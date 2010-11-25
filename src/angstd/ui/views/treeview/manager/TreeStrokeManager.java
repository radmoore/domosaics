package angstd.ui.views.treeview.manager;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import angstd.model.tree.TreeEdgeI;
import angstd.ui.views.view.manager.DefaultViewManager;

/**
 * TreeStrokeManager defines the mapping between tree edges and their 
 * strokes. This class extends DefaultViewManager to ensure the
 * communication with the managed view.
 * 
 * @author Andreas Held
 *
 */
public class TreeStrokeManager extends DefaultViewManager {

	/** Mapping between tree edges and their associated stroke */
	protected Map<TreeEdgeI, Stroke> edges2strokes;
	
	/** the default edge stroke */
	protected Stroke defaultEdgeStroke =  new BasicStroke(1f);
	
	/**
	 * Constructor for a new TreeStrokeManager
	 */
	public TreeStrokeManager() {
		edges2strokes = new HashMap<TreeEdgeI, Stroke>();
	}
	
	/**
	 * Returns the default edge stroke
	 * 
	 * @return
	 * 		default edge stroke
	 */
	public Stroke getDefaultEdgeStroke(){
		return defaultEdgeStroke;
	}
	
	/**
	 * Returns the stroke for the specified edge.
	 * 
	 * @param edge 
	 * 		the edge which stroke is requested
	 * @return 
	 * 		the stroke for the specified edge
	 */
	public Stroke getEdgeStroke(TreeEdgeI edge){
		Stroke stroke = edges2strokes.get(edge); 
		if (stroke == null) 
			return getDefaultEdgeStroke();
		
		return stroke;
	}
  
	/* ******************************************************************* *
	 *   							SET methods							   *
	 * ******************************************************************* */
	
	/**
	 * Sets the default edge stroke. Null is permitted as 
	 * default edge stroke
	 * 
	 * @param stroke 
	 * 		the new default edge stroke
	 */
	public void setDefaultEdgeStroke(Stroke stroke){
		if (stroke == null)
			return;
		this.defaultEdgeStroke = stroke;
		visualChange();
	}
	
	/**
	 * Set the edge stroke for a specified tree edge
	 * A null stroke resets the component value to default
	 * 
	 * @param stroke
	 * 		the stroke to be set for the specified edge
	 * @param edge
	 * 		the edge which stroke is being set
	 */
	public void setEdgeStroke(Stroke stroke, TreeEdgeI edge){
		if (stroke == null || stroke.equals(defaultEdgeStroke)) {
			edges2strokes.remove(edge);
			return;
		} 	
		
		edges2strokes.put(edge, stroke);
		visualChange();
	}
  
	/**
	 * Update the stroke for a set of edges
	 * 
	 * @param stroke
	 * 		the stroke to be set for the specified edges
	 * @param edges
	 * 		the edges which stroke are going to be set
	 */
	public void setEdgeStroke(BasicStroke stroke, Collection<TreeEdgeI> edges){
		for (TreeEdgeI edge : edges) 
			setEdgeStroke(stroke, edge);
		visualChange();
	}
      
}
