package angstd.ui.views.domaintreeview.mousecontroller;

import java.awt.event.MouseAdapter;

import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.mousecontroller.ArrangementMouseController;
import angstd.ui.views.domainview.mousecontroller.DomainMouseController;
import angstd.ui.views.domainview.mousecontroller.SequenceSelectionMouseController;
import angstd.ui.views.domainview.mousecontroller.ShiftComponentsMouseController;
import angstd.ui.views.treeview.components.TreeMouseController;

/**
 * A class creating all MouseController which are needed
 * to provide the complete mouse event handling for a DomainTreeView.
 * <p>
 * When initializing the view, it will iterate over all mouse controller entries
 * defined in this factory and creates an instance of it. <br>
 * Therefore to add additional mouse controller to the DomainTreeView you just need
 * to make two entries in this factory and add the getMethod to the DomainTreeView.
 * 
 * @author Andreas Held
 *
 */
public class DomainTreeMouseControllerFactory {

	/**
	 * Enumeration of all mouse controller which can be added to the 
	 * view.
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum DomainTreeMouseControllerType {
		ARRANGEMENTMC, 	 		
		DOMAINMC, 		
		SEQUENCESELECTIONMC,	
		COMPONENTSHIFTMC,
		TREEMC,
		DOMEVENTMC,
		;
	};
	
	/**
	 * Method called to create an instance of a specific mouse adapter.
	 * 
	 * @param type
	 * 		the mouse controllers type
	 * @return
	 * 		instance of the specified mouse controller
	 */
	public static MouseAdapter create(DomainTreeMouseControllerType type, DomainTreeViewI view) {
		switch (type) {
			case ARRANGEMENTMC: 		return new ArrangementMouseController(view);
			case DOMAINMC: 				return new DomainMouseController(view);
			case SEQUENCESELECTIONMC: 	return new SequenceSelectionMouseController(view);
			case COMPONENTSHIFTMC: 		return new ShiftComponentsMouseController(view);
			case TREEMC: 				return new TreeMouseController(view);
			case DOMEVENTMC: 			return new DomainEventMouseController(view);
		}
		return null;
	}
}
