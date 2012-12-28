package domosaics.ui.views.view;

import java.util.Iterator;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.io.menureader.DefaultMenuActionManager;
import domosaics.ui.io.menureader.MenuReader;


/**
 * The view specific action manager extending the  {@link DefaultMenuActionManager}
 * from {@link MenuReader}. This manager provides all loaded actions for
 * the view specific menu like the DDefaultActionManager does, but also 
 * provides the method to retrieve all actions at once. <br>
 * This is useful when changing into ZoomMode and checking for each action
 * if it should still be active. 
 * 
 * @author Andreas Held
 *
 */
public class ViewActionManager extends DefaultMenuActionManager{

	/**
	 * Return an iterator object iterating over all loaded actions
	 * 
	 * @return
	 * 		iterator object iterating over all loaded actions
	 */
	public Iterator<AbstractMenuAction> getActions() {
		return actions.values().iterator();
	}
	
}
