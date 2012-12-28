package domosaics.ui.views.view.manager;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.views.domainview.manager.DomainLayoutManager;

/**
 * Interface which must be implemented by all action class enumerations
 * which are defined in layout managers to provide quick access to all
 * view specific menu actions. <br>
 * For details look into {@link DefaultLayoutManager}.
 * 
 * @author Andreas Held
 *
 */
public interface ActionEnumeration {

	/**
	 * Returns the action class for an action. For an example see
	 * {@link DomainLayoutManager}
	 * 
	 * @param <T>
	 * 		generic to ensure that the action class is of type AbstractMenuAction
	 * @return
	 * 		the class for an AbstractMenuAction 
	 */		
	public <T extends AbstractMenuAction> Class<T> getActionClass();
	
}
