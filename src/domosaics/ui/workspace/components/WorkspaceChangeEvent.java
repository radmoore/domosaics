package domosaics.ui.workspace.components;

import java.util.EventObject;

import domosaics.model.workspace.WorkspaceElement;


/**
 * WorkspaceChangeEvent describes events which are fired to registered
 * listeners, when an workspace element changed.
 * 
 * @author Andreas Held
 *
 */
public class WorkspaceChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	/** indicates node change */
	protected boolean changed;
	
	/** indicates node was added */
	protected boolean added;
	
	/** indicates node removal */
	protected boolean removed;
	
	/** the modified Node */
	protected WorkspaceElement node;

	
	/**
	 * Constructor for a new WorkspaceChangeEvenet, where only the changed
	 * element is specified, but not the way it was changed.
	 * 
	 * @param source
	 * 		the changed WorkspaceElement
	 */
	public WorkspaceChangeEvent(WorkspaceElement source) {
		super(source);
		setNode(source);
	}
	
	/**
	 * Constructor for a new WorkspaceChangeEvenet, where also the Change
	 * is specified.
	 * 
	 * @param source
	 * 		the changed WorkspaceElement
	 * @param added
	 * 		whether or not a element was added
	 * @param changed
	 * 		whether or not the element was changed
	 * @param removed
	 * 		whether or not a element was removed
	 */
	public WorkspaceChangeEvent(WorkspaceElement source, boolean added, boolean changed, boolean removed) {
		super(source);
		setNode(source);
		setAdded(added);
		setChanged(changed);
		setRemoved(removed);
	}
	
	/**
	 * Checks whether or not the element was added
	 * 
	 * @return
	 * 		whether or not the element was added
	 */
	public boolean isAdded() {
		return added;
	}
	
	/**
	 * Sets the added flag indicating that the element was added
	 * 
	 * @param added
	 * 		whether or not the element was added
	 */
	public void setAdded(boolean added) {
		this.added = added;
	}
	
	/**
	 * Checks whether or not the element was changed
	 * 
	 * @return
	 * 		whether or not the element was changed
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Sets the changed flag indicating that an element was changed
	 * 
	 * @param changed
	 * 		whether or not an element was changed
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	/**
	 * Checks whether or not the element was removed 
	 * 
	 * @return
	 * 		whether or not the element was removed
	 */
	public boolean isRemoved() {
		return removed;
	}

	/**
	 * Sets the removed flag indicating that the element was removed
	 * 
	 * @param removed
	 * 		whether or not the element was removed
	 */
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	
	/**
	 * Returns the modified element within the workspace tree
	 * 
	 * @return
	 * 		modified element
	 */
	public WorkspaceElement getNode() {
		return node;
	}
	
	/**
	 * Sets the modified workspace element
	 * 
	 * @param node
	 * 		modified workspace element
	 */
	public void setNode(WorkspaceElement node){
		this.node = node;
	}

}
