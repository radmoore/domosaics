package domosaics.ui.tools.domainlegend.components;

import domosaics.ui.io.menureader.AbstractMenuAction;
import domosaics.ui.tools.domainlegend.actions.SortAlphabeticallyAction;
import domosaics.ui.tools.domainlegend.actions.SortByFrequenceAction;
import domosaics.ui.views.view.ViewActionManager;
import domosaics.ui.views.view.manager.ActionEnumeration;
import domosaics.ui.views.view.manager.DefaultLayoutManager;

/**
 * The LegendLayoutManager can be used to react on action events 
 * triggered by the user. For instance if the order in which the domains 
 * should be displayed is changed. Therefore this class keeps track of the 
 * menus action states.
 * 
 * 
 * @author Andreas Held
 *
 */
public class LegendLayoutManager extends DefaultLayoutManager{

	/**
	 * Enumeration of all actions specified for the legend view menu
	 * (except general view actions). <br>
	 * This enumeration is used to get quick access to action states
	 * for the managing of correct action states.
	 * 
	 * @author Andreas Held
	 *
	 */
	enum LegendAction implements ActionEnumeration {
		
		SORTALPHABETICALLY 		(SortAlphabeticallyAction.class),
		SORTBYFREQUUENCY 		(SortByFrequenceAction.class),
		;
		
		private Class<?> clazz;
		
		private LegendAction(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends AbstractMenuAction> Class<T> getActionClass() {
			return (Class<T>) clazz;
		}	
	}
	
	
	/**
	 * Constructor for a new LegendLayoutManager
	 * 
	 * @param manager
	 * 		the views action manager
	 */
	public LegendLayoutManager(ViewActionManager manager) {
		super(manager);
	}
	
	/**
	 * Sets the sorting of domains to alphabetical order
	 */
 	public void setToSortAlphabetically(){
 		setState(LegendAction.SORTBYFREQUUENCY, false);
		structuralChange();
	}
 	
 	/**
 	 * Sets the sorting of domains to ordered by number of occurrences 
 	 * within the dataset
 	 */
	public void setToSortByFrequence(){
		setState(LegendAction.SORTALPHABETICALLY, false);
		structuralChange();
	}
 	
	/**
	 * Returns whether or not the domains are sorted alphabetically
	 * 
	 * @return
	 * 		whether or not the domains are sorted alphabetically
	 */
	public boolean isSortAlphabetically(){
		return getState(LegendAction.SORTALPHABETICALLY);
	}
	
	/**
	 * Returns whether or not the domains are sorted by frequency
	 * 
	 * @return
	 * 		whether or not the domains are sorted by frequency
	 */
	public boolean isSortByFrequency(){
		return getState(LegendAction.SORTBYFREQUUENCY);
	}


}
