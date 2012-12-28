package angstd.ui.views.treeview.actions.context;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import angstd.model.tree.TreeEdgeI;
import angstd.ui.ViewHandler;
import angstd.ui.util.AngstdColorPicker;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;



/**
 * Action which colorizes elements of the tree. The elements being 
 * colorized can be defined by setting the type flag when using the 
 * constructor.
 * 
 * @author Andreas Held
 *
 */
public class ColorizeAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	/** colorizes the nodes, e.g. labels */
	public static final int NODE = AngstdColorPicker.NODE;
	
	/** colorizes the subtree of a node */
	public static final int SUBTREE = AngstdColorPicker.SUBTREE;
	
	/** colorizes the path to the root */
	public static final int PATH_TO_ROOT = AngstdColorPicker.PATH_TO_ROOT;
	
	/** colorizes the path to the parent */
	public static final int PATH_TO_PARENT = AngstdColorPicker.PATH_TO_PARENT;
	
	/** colorizes the path to the children */
	public static final int PATH_TO_CHILDREN = AngstdColorPicker.PATH_TO_CHILDREN;
	
	/** the currently chosen type for colorization */
	protected int type;
	
	/** the color to start with */
	protected Color startColor;
	
	
	/**
	 * Constructor for a new ColorizeAction.
	 * 
	 * @param type
	 * 		type flag defining what tree elements are going to be colorized
	 */
	public ColorizeAction (int type) {
		super();
		this.type = type;
		switch (type) {
			case NODE: 
				putValue(Action.NAME, "Change Font Color"); 
				putValue(Action.SHORT_DESCRIPTION, "Changes the Font");
				break;
			case SUBTREE: 
				putValue(Action.NAME, "Colorize Subtree"); 
				putValue(Action.SHORT_DESCRIPTION, "Colorize the subtree branches");
				break;
			case PATH_TO_ROOT: 
				putValue(Action.NAME, "Colorize Path to Root"); 
				putValue(Action.SHORT_DESCRIPTION, "Colorize the path to the root");
				break;
			case PATH_TO_PARENT: 
				putValue(Action.NAME, "Colorize Path to Parent"); 
				putValue(Action.SHORT_DESCRIPTION, "Colorize the path to the parent");
				break;
			case PATH_TO_CHILDREN: 
				putValue(Action.NAME, "Colorize Path to Children"); 
				putValue(Action.SHORT_DESCRIPTION, "Colorize the path to the children");
				break;
			default: 
				putValue(Action.NAME, "Change Color");
				putValue(Action.SHORT_DESCRIPTION, "Changes the Color");
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		TreeViewI view = (TreeViewI) ViewHandler.getInstance().getActiveView();
		
		NodeComponent selectedNode = view.getTreeSelectionManager().getClickedComp();
		
		// if the user cancels the selection we want to be able to restore the old colors, so first we backup them
		startColor = Color.black;    // this may change during the backup of edge colors
		Map<TreeEdgeI, Color> backup = backupOldColors(view, selectedNode);
		Color backupNodeColor = null;
		Color newColor = null;
		
		switch (type) {
			case NODE: 
				backupNodeColor = view.getTreeColorManager().getNodeColor(selectedNode);
				startColor = backupNodeColor;
				newColor = new AngstdColorPicker(AngstdColorPicker.NODE, view, startColor).show(); 
				break;
			case SUBTREE: 
				newColor = new AngstdColorPicker(AngstdColorPicker.SUBTREE, view, startColor).show(); 
				break;
			case PATH_TO_ROOT: 
				newColor = new AngstdColorPicker(AngstdColorPicker.PATH_TO_ROOT, view, startColor).show(); 
				break;
			case PATH_TO_PARENT: 
				newColor = new AngstdColorPicker(AngstdColorPicker.PATH_TO_PARENT, view, startColor).show(); 
				break;
			case PATH_TO_CHILDREN: 
				newColor = new AngstdColorPicker(AngstdColorPicker.PATH_TO_CHILDREN, view, startColor).show(); 
				break;
		}
		
		// we have to backup the colors now
		if(newColor == null) 
			restoreOldColors(view, backup, backupNodeColor, selectedNode);
		
		// clear selection and reset the selection color (which might be changed because of previews) be altered
		view.getTreeColorManager().setSelectionColor(view.getTreeColorManager().getDefaultSelectionColor());
		view.getTreeSelectionManager().clearSelection();
	}
	
	/**
	 * Helper method to backup the colors for the selected items.
	 * It would have been more easy to just backup all edges but
	 * performance performance. ;)
	 * If there are any bugs with this just backup everything and
	 * therefore make the code more easy.
	 * 
	 * @param view
	 * 		the backend tree view
	 * @param selectedNode
	 * 		the selected node
	 * @return
	 * 		the backuped colors for the selected items
	 */
	protected Map<TreeEdgeI, Color> backupOldColors(TreeViewI view, NodeComponent selectedNode) {
		Map<TreeEdgeI, Color> backup = new HashMap<TreeEdgeI, Color>();
			
		switch (type) {
			case SUBTREE: 
				for (NodeComponent nc : selectedNode.depthFirstIterator()) 
					if (!nc.equals(selectedNode)) {
						TreeEdgeI actEdge = nc.getNode().getEdgeToParent();
						backup.put(actEdge, view.getTreeColorManager().getEdgeColor(actEdge));
					}
				break;
			case PATH_TO_ROOT: 
				while(selectedNode != null && selectedNode.getNode().getEdgeToParent() != null){
					TreeEdgeI actEdge = selectedNode.getNode().getEdgeToParent();
					backup.put(actEdge, view.getTreeColorManager().getEdgeColor(actEdge));
					selectedNode = selectedNode.getParent();
				}	
				break;
			case PATH_TO_PARENT: {
				TreeEdgeI actEdge = selectedNode.getNode().getEdgeToParent();
				backup.put(actEdge, view.getTreeColorManager().getEdgeColor(actEdge));
				break;
			}
			case PATH_TO_CHILDREN: 
				for (NodeComponent nc : selectedNode.children()) {
					TreeEdgeI actEdge = nc.getNode().getEdgeToParent();
					backup.put(actEdge, view.getTreeColorManager().getEdgeColor(actEdge));
				}
				break;
		}
		return backup;
	}
	
	/**
	 * Helper method to restore the old colors for the selected items
	 * based on the backup colors created in backupOldColors(). <br>
	 * If nodeColor is null, its the indicator that the node color was
	 * changed (and no edges).
	 * 
	 * @param view
	 * 		the backend tree view
	 * @param backup
	 * 		the backuped colors for the selected edge items or null
	 * @param nodeColor
	 * 		the backuped node color or null
	 * @param selectedNode
	 * 		the selected node
	 * 		
	 */
	protected void restoreOldColors(TreeViewI view, Map<TreeEdgeI, Color> backup, Color nodeColor, NodeComponent selectedNode) {
		// we were in NODE colorization mode
		if (nodeColor != null) { 
			view.getTreeColorManager().setNodeColor(selectedNode, nodeColor);
			return;
		}
		
		// else restore EDGES
		for (TreeEdgeI actEdge : backup.keySet()) 
			view.getTreeColorManager().setEdgeColor(actEdge, backup.get(actEdge));
	}

}
