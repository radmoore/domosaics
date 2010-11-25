package angstd.ui.views.domainview.components.detectors;

import java.awt.Point;
import java.util.Iterator;

import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;

/**
 * The ArrangementComponentDetector detects if a an arrangements is 
 * visible at a given Point within the screen.
 * This class can be used to ensure user activity with DomainArrangementComponents. 
 * 
 * @author Andreas Held
 *
 */
public class ArrangementComponentDetector {
	
	/** the view owning the arrangements */
	protected DomainViewI view;

	/**
	 * Basic constructor for a new arrangement resolver.
	 * 
	 * @param view
	 * 		the domain view owning the domain arrangements
	 */
	public ArrangementComponentDetector(DomainViewI view){
		this.view = view; 
	}
		
	/**
	 * Searches if a domain arrangement component is present at a 
	 * specified point.
	 * 
	 * @param p
	 * 		the point to look at
	 * @return
	 * 		the domain arrangement component being present at a specified point or null.
	 */
	public ArrangementComponent searchArrangementComponent(Point p) {
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();

			if (!dac.isVisible())
				continue;
			
			// check if cursor is within circular node shape
			if (dac.getDisplayedShape().contains(p)) 
				return dac;
			
		}	
		return null;
	}
}
