package domosaics.ui.tools.domaingraph.components;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * DomainGraphControl controls mouse events such as itemEntered
 * on prefuse graph objects. For instance nodes get selected when the 
 * user clicks on them.
 * 
 * @author Andreas Held
 *
 */
public class DomainGraphControl extends ControlAdapter {
    
	/** the prefuse graph being controlled */
	protected PrefuseGraph graph;
	
	
	/**
	 * Constructor for a new DomainGraphControl
	 * 
	 * @param graph
	 * 		the prefuse graph being controlled 
	 */
	public DomainGraphControl (PrefuseGraph graph) {
		this.graph = graph;
	}
	
	/**
	 * Method triggered when the cursor enters a graph node
	 */
	public void itemEntered(VisualItem item, MouseEvent e) {
		if (item.isInGroup("graph.nodes")) 
			((Display)e.getSource()).setToolTipText(item.getString("name"));
    }
   
	/**
	 * Method triggered when the cursor exits a graph node
	 */
	public void itemExited(VisualItem item, MouseEvent e) {
		Display d = (Display)e.getSource();
		d.setToolTipText(null);
	}
	
	/**
	 * Method triggered when the cursor clicks on a graph node
	 */
	public void itemClicked(VisualItem item, MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (item.isInGroup("graph.nodes")) {
			graph.clearSelectedNodes();
           	graph.addToSelectedItems((NodeItem) item);
		}
	}
}
