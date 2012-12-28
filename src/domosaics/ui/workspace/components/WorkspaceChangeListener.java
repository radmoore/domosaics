package domosaics.ui.workspace.components;

import java.util.EventListener;

/**
 * Interface WorkspaceChangeListener defines methods for Listeners
 * which can be registered to a workspace element.
 * 
 * @author Andreas Held
 *
 */
public interface WorkspaceChangeListener extends EventListener {
	
	/**
	 * Fired if a workspace element was added
	 * 
	 * @param evt
	 * 		the ChangeEvenet describing the change
	 */
	public void nodeAdded(WorkspaceChangeEvent evt);
		
	/**
	 * Fired if a workspace element was changed
	 * 
	 * @param evt
	 * 		the ChangeEvenet describing the change
	 */
	public void nodeRemoved(WorkspaceChangeEvent evt);
		
	/**
	 * Fired if a workspace element was removed
	 * 
	 * @param evt
	 * 		the ChangeEvenet describing the change
	 */
	public void nodeChanged(WorkspaceChangeEvent evt);
}
