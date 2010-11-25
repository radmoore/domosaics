package angstd.ui.views.domainview.components.detectors;

import java.awt.Point;
import java.util.Iterator;

import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;

/**
 * The DomainComponentDetector detects if a an domain is 
 * visible at a given Point within the screen.
 * This class can be used to ensure user activity with DomainComponents. 
 * 
 * @author Andreas Held
 *
 */
public class DomainComponentDetector {
	
	/** the view owning the domains */ 
	protected DomainViewI view;

	/**
	 * Basic constructor for a new domain resolver.
	 * 
	 * @param view
	 * 		the domain view owning the domains
	 */
	public DomainComponentDetector(DomainViewI view){
		this.view = view; 
	}
		
	/**
	 * Searches if a domain component is present at a specified point.
	 * 
	 * @param p
	 * 		the point to look at
	 * @return
	 * 		the domain component being present at a specified point or null.
	 */
	public DomainComponent searchDomainComponent(Point p) {
		Iterator<DomainComponent> iter = view.getArrangementComponentManager().getDomainComponentsIterator();
		while(iter.hasNext()) {
			DomainComponent dc = iter.next();

			if (!dc.isVisible())
				continue;
			
			if (dc.getDisplayedShape().contains(p)) 
				return dc;
		}	
		return null;
	}
}
