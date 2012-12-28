package domosaics.ui.views.domainview.manager;

import java.awt.Font;

import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.view.manager.DefaultFontManager;
import domosaics.ui.views.view.manager.DefaultSelectionManager;
import domosaics.ui.views.view.manager.ViewManager;




/**
 * A class creating all needed {@link ViewManager} which are needed
 * to provide the complete manager driven functionality for a DomainView.
 * <p>
 * When initializing the view, it will iterate over all enumeration entries
 * defined in this factory and creates an instance to it. <br>
 * Therefore to add additional manager to the DomainView you just need
 * to make two entries in this factory and add the getMethod to the DomainView.
 * 
 * @author Andreas Held
 *
 */
public class DomainViewManagerFactory {

	/**
	 * Enumeration of all manager which should be added to the view
	 * 
	 * @author Andreas Held
	 *
	 */
	public enum DomainViewManager {
		DOMAINCOMPONENTMANAGER, 	 		
		DOMAINCOLORMANAGER, 		
		DOMAINSELECTIONMANAGER,	
		ARRANGEMENTSELECTIONMANAGER,
		DOMAINSHAPEMANAGER,		
		DAFONTMANAGER,		
		DOMAINFONTMANAGER,
		DOMAINSEARCHORTHOLOGSMANAGER,
		DOMAINSHIFTMANAGER,
		COLLAPSESAMEARRANGEMENTSMANAGER,
		DOMAINSIMILARITYMANAGER,
		NOTEMANAGER,
		;
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
	public static ViewManager create(DomainViewManager type) {
		switch (type) {
			case DOMAINCOMPONENTMANAGER: 		return new DomainArrangementComponentManager();
			case DOMAINCOLORMANAGER: 			return new DomainColorManager();
			case DOMAINSELECTIONMANAGER: 		return new DefaultSelectionManager<DomainComponent>();
			case ARRANGEMENTSELECTIONMANAGER: 	return new DefaultSelectionManager<ArrangementComponent>();
			case DOMAINSHAPEMANAGER: 			return new DomainShapeManager();
			case DAFONTMANAGER: 				return new DefaultFontManager<ArrangementComponent>();
			case DOMAINFONTMANAGER: 			return new DefaultFontManager<DomainComponent>(16, 22, Font.BOLD);
			case DOMAINSEARCHORTHOLOGSMANAGER: 	return new DomainSearchOrthologsManager();
			case DOMAINSHIFTMANAGER:			return new DomainShiftManager();
			case COLLAPSESAMEARRANGEMENTSMANAGER: 	return new CollapseSameArrangementsManager();
			case DOMAINSIMILARITYMANAGER:		return new DomainSimilarityManager();
			case NOTEMANAGER:					return new NoteManager();
		}
		return null;
	}
}
