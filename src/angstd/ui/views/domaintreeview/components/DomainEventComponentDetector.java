package angstd.ui.views.domaintreeview.components;

import java.awt.Point;
import java.util.Iterator;

import angstd.ui.views.domaintreeview.DomainTreeViewI;



/**
 * The DomainEventComponentDetector detects if a an domain event component is 
 * visible at a given Point within the screen.
 * This class can be used to ensure user activity with DomainEventComponents. 
 * 
 * @author Andreas Held
 *
 */
public class DomainEventComponentDetector {

	/** the view containing the domain event components */
	protected DomainTreeViewI view;

	
	/**
	 * Constructor for a new DomainEventComponentDetector
	 * 
	 * @param view
	 * 		the view containing the domain event components 
	 */
	public DomainEventComponentDetector(DomainTreeViewI view){
		this.view = view; 
	}
		
	/**
	 * Method which does the searching at the specified location.
	 * 
	 * @param p
	 * 		the point to look at
	 * @return
	 * 		the domain event component being present at a specified point or null.
	 */
	public DomainEventComponent searchDomainEventComponent(Point p) {
		Iterator<DomainEventComponent> iter = view.getDomainEventComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			DomainEventComponent dec = iter.next();
			
			// check if cursor is within circular node shape
			if (dec.getDisplayedShape().contains(p)) 
				return dec;
		}	
		return null;
	}
	
}
