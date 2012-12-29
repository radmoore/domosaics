package domosaics.ui.views.view.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.view.View;
import domosaics.ui.views.view.ViewActionManager;
import domosaics.ui.views.view.components.ZoomCompatible;




/**
 * The workflow of DoMosaicS is action driven which means that in 
 * various situations the action state is requested and based on the 
 * result the classes interact differently.
 * For instance if the domain ruler action is activated within the
 * domain view, the additional domain ruler renderer will render the
 * amino acid ruler.
 * <p>
 * To get quick access to this actions, the methods in this class can be 
 * used. In each LayoutManager which messes around with actions
 * its recommended to define an enumeration containing the classes
 * of the actions. The enumeration must implement {@link ActionEnumeration}.
 * This is because access is provided by the action class within the
 * program package.
 * <p>
 * For an example the {DomainLayoutManager} is a good one. 
 * <p>
 * Of course its also possible for a LayoutManager to directly extend
 * DefaultViewManager and the action handling can be made with flags
 * manually.
 * 
 * @author Andreas Held
 *
 */
public class DefaultLayoutManager extends DefaultViewManager {

	/** the view action manager to get control over the menu action */
	protected ViewActionManager manager;
	
	protected Map<AbstractMenuAction, Boolean> stateBackup; 
	
	
	/**
	 * Constructor for a new DefaultLayoutManager which takes
	 * the views action manager as parameter.
	 * 
	 * @param manager
	 * 		the views action manager
	 */
	public DefaultLayoutManager(ViewActionManager manager) {
		super();
		this.manager = manager;
	}
	
	
	/**
	 * Method to call whenever a view is going into zoom mode. 
	 * 
	 * @param aView
	 * 		the view which is going to be in zoom mode
	 */
	public void toggleZoomMode(View view) {
		if (view.isZoomMode()) {
			backupActionStates();
			
			// disable all actions of the view except of action implementing the empty Zoomcompatible interface
			Iterator <AbstractMenuAction> actionIter = view.getViewInfo().getActionManager().getActions();
			while (actionIter.hasNext()) {
				AbstractMenuAction action = actionIter.next();
				if (!(action instanceof ZoomCompatible))
					action.setEnabled(false);
			}
		} else {
			// restore the old enable status for action
			restoreActionStates();
		}
	}
	
	/**
	 * Helper method to backup the enable state of actions before the view 
	 * goes into zoom mode.
	 */
	private void backupActionStates() {
		stateBackup = new HashMap<AbstractMenuAction, Boolean>();
		
		Iterator<AbstractMenuAction> iter = manager.getActions();
		while(iter.hasNext()) {
			AbstractMenuAction action = iter.next();
			stateBackup.put(action, action.isEnabled());
		}
	}
	
	/**
	 * Helper method to restore the enable action states after the 
	 * view leaves the zoom mode.
	 */
	private void restoreActionStates() {
		for (AbstractMenuAction action : stateBackup.keySet())
			action.setEnabled(stateBackup.get(action));
	}
	
	/**
	 * Returns the views action manager to allow manual changes on actions.
	 * 
	 * @return
	 * 		the views action manager
	 */
	public ViewActionManager getActionManager() {
		return manager;
	}
	
	/**
	 * Fires a structural change event to the view signaling that it has to 
	 * be relyouted and repainted.
	 */
	public void fireStructuralChange() {
		structuralChange();
	}
	
	/**
	 * Fires a visual change event to the view signaling that it has to 
	 * be repainted.
	 */
	public void firevisualChange() {
		visualChange();
	}
	
	/* ************************************************************ *
	 * 						QUICK ACCESS FOR ACTIONS				*
	 * ************************************************************ */
	
	/**
	 * Wrapper around the ViewActionManager to get fast access to actions
	 * 
	 * @param <T>
	 * 		action class extending AbstractMenuAction
	 * @param type
	 * 		the type used to query an action
	 * @return
	 * 		the action for the requested type
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractMenuAction> T getAction(ActionEnumeration type) {
		return (T) manager.getAction(type.getActionClass());
	}
	
	/**
	 * Checks whether or not the action is initialized within the 
	 * ViewManager.
	 * 
	 * @param type
	 * 		type of the action to be checked for initialization
	 * @return
	 * 		whether or not the action with the specified type is initialized
	 */
	public boolean isInitialized(ActionEnumeration type) {
		return getAction(type) != null;
	}
	
	/**
	 * Sets the enabled state for the specified action to true
	 * 
	 * @param type
	 * 		type for the action which is going to be enabled
	 */
	public void enable(ActionEnumeration type) {
		if (!isInitialized(type))
			return;
		getAction(type).setEnabled(true);
	}
	
	/**
	 * Sets the enabled state for the specified action to false
	 * 
	 * @param type
	 * 		type for the action which is going to be disabled
	 */
	public void disable(ActionEnumeration type) {
		if (!isInitialized(type))
			return;
		getAction(type).setEnabled(false);
	}
	
	/**
	 * Sets the state for the specified action
	 * 
	 * @param type
	 * 		type of the action which state is going to change
	 * @param state
	 */
	public void setState(ActionEnumeration type, boolean state) {
		if (!isInitialized(type))
			return;
		getAction(type).setState(state);
	}
	
	/**
	 * Returns the state of the specified action
	 * 
	 * @param type
	 * 		type of the action which state is requested
	 * @return
	 * 		state of the specified action, false if not initialized
	 * 		
	 */
	public boolean getState(ActionEnumeration type) {
		if (!isInitialized(type))
			return false;
		return getAction(type).getState();
	}
	
	/**
	 * Returns whether or not the action is currently enabled
	 * 
	 * @param type
	 * 		the action to be checked
	 * @return
	 * 		whether or not the action is currently enabled
	 */
	public boolean isEnabled(ActionEnumeration type) {
		if (!isInitialized(type))
			return false;
		return getAction(type).isEnabled();
	}
}
