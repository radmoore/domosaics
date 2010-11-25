package angstd.ui.views.domaintreeview.manager;

import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.components.DomainEventComponent;
import angstd.ui.views.view.manager.DefaultSelectionManager;
import angstd.ui.views.view.manager.ViewManager;

/**
 * A class creating all needed {@link ViewManager} which are needed
 * to provide the complete manager driven functionality for a DomainTreeView.
 * <p>
 * When initializing the view, it will iterate over all enumeration entries
 * defined in this factory and creates an instance to it. <br>
 * Therefore to add additional manager to the DomainTreeView you just need
 * to make two entries in this factory and add the getMethod to the DomainTreeView.
 * 
 * @author Andreas Held
 *
 */
public class DomainTreeViewManagerFactory {

	/**
	 * Enumeration of all manager which should be added to the view
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum DomainTreeViewManager {
		DOMAINEVENTCOMPONENTMANAGER	,	
		DOMAINEVENTSELECTIONMANAGER 	,	
		COLLAPSESAMEARRANGEMENTSINSUBTREEMANAGER,
		INNERNODEARRANGEMENTMANAGER,
	};
	
	/**
	 * Method called to create an instance of a specific manager.
	 * 
	 * @param type
	 * 		the managers type
	 * @param view
	 * 		the view which might be needed to make necessary calculations within a manager
	 * @return
	 * 		instance of the specified manager
	 */
	public static ViewManager create(DomainTreeViewManager type, DomainTreeViewI view) {
		switch (type) {
			case DOMAINEVENTCOMPONENTMANAGER: 	return new DomainEventComponentManager();
			case DOMAINEVENTSELECTIONMANAGER: 	return new DefaultSelectionManager<DomainEventComponent>();
			case COLLAPSESAMEARRANGEMENTSINSUBTREEMANAGER: return new CSAInSubtreeManager(view);
			case INNERNODEARRANGEMENTMANAGER: return new InnerNodeArrangementManager(view);
		}
		return null;
	}

	;
}
