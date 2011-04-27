package angstd.ui.workspace;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import angstd.model.configuration.Configuration;
import angstd.model.workspace.Workspace;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.workspace.components.WorkspaceChangeEvent;
import angstd.ui.workspace.components.WorkspaceChangeListener;

/**
 * TreeModel for the workspace tree. Represents the hierarchical structure of 
 * the given workspace.
 * <p> 
 * The given workspace is used as root and
 * the {@link WorkspaceElement} structure is resolved by the model.  
 * <p> 
 * Whenever changes occur within the workspace this TreeModel is notfied.
 * To ensure this its added as listener to the {@link Workspace}.
 * 
 * @author Andreas Held
 *
 */
public class WorkspaceTreeModel implements TreeModel, WorkspaceChangeListener {
	private static final long serialVersionUID = 1L;
	
	/** the workspace model to control */
	protected Workspace workspace;
	
	/** listener which are interested in model changes */
	protected EventListenerList listenerList = new EventListenerList();
	
	/** the displaying JTree used e.g. for expanding all nodes */
	protected WorkspaceView view;
	
	/**
	 * Constructor for the WorkspaceTreeModel.
	 * 
	 * @param workspace
	 * 		the workspace managed by this model
	 * @param tree
	 * 		the displaying JTree based on this model
	 */
	public WorkspaceTreeModel(Workspace workspace, WorkspaceView tree) { //, WorkspaceView view){
		super();
    	this.workspace = workspace;
    	this.view = tree;
    	
    	// add listener to the workspace to ensure reaction on model changes
    	workspace.addWorkspaceChangeListener(this);
    }
	
    /* ******************************************************************** *
     *               		WorkspaceChangeListener methods					*
     * ******************************************************************** */
    
	/**
	 * Triggered when a node was added. Fires the expensive 
	 * TreeStructureChangedEvent to all listeners and expands the tree.
	 */
	public void nodeAdded(WorkspaceChangeEvent o) {
		if(o.getNode().getParent() != null)
			fireTreeStructureChanged(o.getSource(), getPathTo(o.getNode().getParent()));
		refresh();
	}

	/**
	 * Triggered when a node was changed. Fires the expensive 
	 * TreeStructureChangedEvent to all listeners and expands the tree.
	 */
	public void nodeChanged(WorkspaceChangeEvent o) {
		fireTreeStructureChanged(o.getSource(), getPathTo(o.getNode()));
		refresh();
	}

	/**
	 * Triggered when a node was removed. Fires the expensive 
	 * TreeStructureChangedEvent to all listeners and expands the tree.
	 */
	public void nodeRemoved(WorkspaceChangeEvent o) {
		if(o.getNode().getParent() != null)
			fireTreeStructureChanged(o.getSource(), getPathTo(o.getNode().getParent()));
		refresh();
	}
	
	/**
	 * Triggers a refresh of the model which uncollapses all nodes within 
	 * the tree.
	 * FIXME (see expandAll() in workspaceView)
	 * there is a seemingly random runtime error here upon startup/project import
	 * which may be due to changes in other nodes of the tree triggering
	 * this method _before_ all children have been added to the tree
	 * (that is, refresh triggered while tree is still in buildup).
	 * As this has no real effects, for now the exception is caught 
	 * but not dealt with
	 */
	public void refresh(){	
		fireTreeStructureChanged(this, new TreePath(new Object[]{workspace}));
		try {
			view.expandAll();
		}
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
	}
	
	/* ***************************************************************** *
	 * 					Implementing the TreeModel interface			 *
	 * ***************************************************************** */
	/**
	 * Implementation of the TreeModel interface method to add listeners
	 */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * Implementation of the TreeModel interface method to remove listeners
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }
  
    /**
     * Implementation of the TreeModel interface method to get a child
     * at the specified index based on its parent. 
     * 
     * @param parent
     * 		the parent object of which the child is requested
     * @param index
     * 		the index of the child
     * @return 
     * 		the child of the specified parent at the specifiedindex
     */
	public Object getChild(Object parent, int index) {
		return ((WorkspaceElement)parent).getChildAt(index);		
	}

	/**
	 * Implementation of the TreeModel interface method to get the
	 * number of children for a specified element.
	 */
	public int getChildCount(Object parent) {
		return ((WorkspaceElement)parent).getChildCount();	
	}

	/**
	 * Implementation of the TreeModel interface method to get the
	 * index of a specified child from a specified parent.
	 */
	public int getIndexOfChild(Object parent, Object child) {
		return ((WorkspaceElement)parent).getIndex((WorkspaceElement) child);		
	}

	/**
	 * Implementation of the TreeModel interface method to get the
	 * root of the workspace tree
	 */
	public Object getRoot() {
		return workspace;
	}

	/**
	 *  Implementation of the TreeModel interface method to check 
	 *  whether or not the element is a possible leaf node.
	 *  In this case only ViewElements are possible leaf nodes.
	 */
	public boolean isLeaf(Object node) {
		return ((WorkspaceElement)node).isView();
	}		

	/**
	 * This method should hopefully never be called. 
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println("valueForPathChanged in TreeModel not supported");
	}
    
	/**
	 * Computes the path from the workspace root node to the given node.
	 * Single element paths pointing only to the workspace are created if 
	 * the given node is null. 
	 * 
	 * @param node 
	 * 		the node
	 * @return 
	 * 		path from the workspace to the node
	 */
	protected TreePath getPathTo(WorkspaceElement node){
		// create single path from workspace to this node
		if(node == null || node.getParent() == null){
			return new TreePath(new Object[]{workspace});
		}
		
		List<WorkspaceElement> nodes = new ArrayList<WorkspaceElement>();
		nodes.add(node);
		WorkspaceElement p = (WorkspaceElement) node.getParent();
		while(p.getParent() != null){
			nodes.add(p);
			p = (WorkspaceElement) p.getParent();
		}
		
		Object[] path = new Object[nodes.size() + 1];
		path[0] = workspace;
		for (int i = 1; i < path.length; i++) {
			path[i] = nodes.get(nodes.size()-i);
		}
		return new TreePath(path);		
	}
    
    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param source 
     * 		the node where the tree model has changed
     * @param path 
     * 		the path to the root node
     */
    private void fireTreeStructureChanged(Object source, TreePath path) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = new TreeModelEvent(source, path);
		
		// Process the listeners last to first, notifying those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) 
			if (listeners[i]==TreeModelListener.class) {
				TreeModelListener listener = (TreeModelListener)listeners[i+1];
				listener.treeStructureChanged(e);
			}
	}
    
}
