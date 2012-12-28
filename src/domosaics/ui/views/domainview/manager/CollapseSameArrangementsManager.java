package domosaics.ui.views.domainview.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.view.manager.DefaultViewManager;




/**
 * CollapseSameArrangementsManager manages the collapsing of same 
 * arrangements for a domain view. The collapsing is done
 * by setting redundant arrangement to invisible, which means that
 * they are not processed by the layout or renderer anymore.
 * The decollapsing can be done by triggering
 * the reset() methods which set all arrangements to visible.
 * 
 * @author Andreas Held
 *
 */
public class CollapseSameArrangementsManager extends DefaultViewManager {

	/** map assigning an arrangement the number of same domain compositions within the dataset */
	protected Map<ArrangementComponent, Integer> dac2redundant;

	/** list where only non redundant arrangements may enter */
	protected List<DomainArrangement> nonRedundant;
	
	
	/**
	 * Constructor for a new CollapseSameArrangementsManager.
	 * 
	 * @param view
	 * 		the view providing the collapse functionality
	 */
	public CollapseSameArrangementsManager() {
		dac2redundant = new HashMap<ArrangementComponent, Integer>();
		nonRedundant = new ArrayList<DomainArrangement>();
	}
	
	/**
	 * Returns the redundancy count for arrangement components.
	 * 
	 * @param dac
	 * 		the arrangement component which redundancy count is requested
	 * @return
	 * 		the redundancy count for the specified arrangement component
	 */
	public int getRedundancyCount(ArrangementComponent dac) {
		if (dac2redundant.get(dac) == null)
			return -1;
		return dac2redundant.get(dac);
	}
	
	/**
	 * Resets the redundancy counts to zero.
	 */
	public void reset() {
		dac2redundant = new HashMap<ArrangementComponent, Integer>();
		nonRedundant = new ArrayList<DomainArrangement>();
		structuralChange();
	}
	
	public void refresh(DomainViewI view, DomainArrangement[] daSet) {
		 // reset everything
		dac2redundant = new HashMap<ArrangementComponent, Integer>();
		nonRedundant = new ArrayList<DomainArrangement>();
		
		// make DAs visible again
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) 
			view.getArrangementComponentManager().setVisible(iter.next(), true);

		collapseDaSet (view, daSet);
	}
	
	/**
	 * Collapses a set of domain arrangements so that only non 
	 * redundant arrangements remain visible.
	 * 
	 * @param view
	 * 		the view providing the functionality
	 * @param daSet
	 * 		the set of arrangements to collapse
	 */
	public void collapseDaSet (DomainViewI view, DomainArrangement[] daSet) {
		incRedundancyCount(view, daSet[0]);
		
		for (int i = 1; i < daSet.length; i++) {
			boolean redundant = false;
			
			for (int j = 0; j < nonRedundant.size(); j++) 
				if (nonRedundant.get(j).getDomains().isEqualTo(daSet[i].getDomains())) {
					redundant = true;
					incRedundancyCount(view, nonRedundant.get(j));
					ArrangementComponent redundantDac = view.getArrangementComponentManager().getComponent(daSet[i]);
					view.getArrangementComponentManager().setVisible(redundantDac, false);
					break;
				}
			
			if (!redundant) 
				incRedundancyCount(view, daSet[i]);
			
		}
		structuralChange();
	}
	
	/**
	 * Increases the redundancy counter for DomainArrangements.
	 * 
	 * @param view
	 * 		the view providing the functionality
	 * @param da
	 * 		the arrangement which redundancy count should be increased.
	 */
	protected void incRedundancyCount(DomainViewI view, DomainArrangement da) {
		ArrangementComponent dac = view.getArrangementComponentManager().getComponent(da);
		
		if (dac2redundant.get(dac) == null) {
			dac2redundant.put(dac, 1);
			nonRedundant.add(dac.getDomainArrangement());
		}
		else {
			int newVal = dac2redundant.get(dac)+1;
			dac2redundant.put(dac, newVal);
		}
	}
}
