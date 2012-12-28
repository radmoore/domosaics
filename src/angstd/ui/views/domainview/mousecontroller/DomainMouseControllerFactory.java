package angstd.ui.views.domainview.mousecontroller;

import java.awt.event.MouseAdapter;

import angstd.ui.views.domainview.DomainViewI;



/**
 * A class creating all MouseController which are needed
 * to provide the complete mouse event handling for a DomainView.
 * <p>
 * When initializing the view, it will iterate over all mouse controller entries
 * defined in this factory and creates an instance of it. <br>
 * Therefore to add additional mouse controller to the DomainView you just need
 * to make two entries in this factory and add the getMethod to the DomainView.
 * 
 * @author Andreas Held
 *
 */
public class DomainMouseControllerFactory {

	/**
	 * Enumeration of all mouse controller which can be added to the 
	 * view.
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum DomainMouseControllerType {
		ARRANGEMENTMC, 	 		
		DOMAINMC, 		
		SEQUENCESELECTIONMC,	
		COMPONENTSHIFTMC,
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
	public static MouseAdapter create(DomainMouseControllerType type, DomainViewI view) {
		switch (type) {
			case ARRANGEMENTMC: 		return new ArrangementMouseController(view);
			case DOMAINMC: 				return new DomainMouseController(view);
			case SEQUENCESELECTIONMC: 	return new SequenceSelectionMouseController(view);
			case COMPONENTSHIFTMC: 		return new ShiftComponentsMouseController(view);
		}
		return null;
	}
}