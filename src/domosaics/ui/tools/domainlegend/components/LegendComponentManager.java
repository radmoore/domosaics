package domosaics.ui.tools.domainlegend.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import domosaics.ui.views.domainview.components.DomainComponent;




/**
 * The LegendComponentManager maps the domain components to their
 * legend components which get layouted and rendered by the legend view.
 * <p>
 * This class provides two iterators for the registered legend components
 * one which sorts the component in alphabetical order and one which
 * sorts them using the number of occurrences within the dataset.
 * 
 * @author Andreas Held
 *
 */
public class LegendComponentManager {
	
	/** mapping between the backend domain components and their corresponding legend component */
	protected Map<DomainComponent, LegendComponent> backend2components;
	
	
	/**
	 * Constructor for a new LegendComponentManager
	 */
	public LegendComponentManager() {
		backend2components = new HashMap<DomainComponent, LegendComponent>();
	}
	
	/**
	 * Returns the legend component for the specified domain component
	 * 
	 * @param dc
	 * 		the domain component which legend component is asked for
	 * @return
	 * 		the corresponding legend component for the specified domain component
	 */
	public LegendComponent getComponent(DomainComponent dc){
		if (dc == null) 
			return null;

		LegendComponent component = backend2components.get(dc);
		if (component == null) {
			component = new LegendComponent(dc);
			backend2components.put(dc, component);
		}
			
		return component;
	}
	
	/**
	 * Normal iterator over all registered legend components.
	 * 
	 * @return
	 * 		iterator over all registered legend components
	 */
	public Iterator<LegendComponent> getComponentsIterator() {
		return backend2components.values().iterator();
	}
	
	/**
	 * Returns an iterator over all registered legend components 
	 * in alphabetical order
	 * 
	 * @return
	 * 		legend component iterator in alphabetical order
	 */
	public Iterator<LegendComponent> getAlphabeticalOrderedComponentsIterator() {
		List<LegendComponent> lcList = new ArrayList<LegendComponent>();
		Iterator<LegendComponent> iter = backend2components.values().iterator();
		while (iter.hasNext())
			lcList.add(iter.next());
		
		Collections.sort(lcList, new Comparator<LegendComponent>() {
	            public int compare(LegendComponent lc1, LegendComponent lc2) {
	                String label1 = lc1.getLabel();
	                String label2 = lc2.getLabel();
	                return label1.compareTo(label2);
	            }
	        });

		return lcList.iterator();
	}
	
	/**
	 * Returns an iterator over all registered legend components 
	 * sorted by the number of occurrences within the dataset
	 * 
	 * @return
	 * 		legend component iterator sorted by number of occurrences within the dataset
	 */
	public Iterator<LegendComponent> getByFrequenceOrderedComponentsIterator() {
		List<LegendComponent> lcList = new ArrayList<LegendComponent>();
		Iterator<LegendComponent> iter = backend2components.values().iterator();
		while (iter.hasNext())
			lcList.add(iter.next());
		
		Collections.sort(lcList, new Comparator<LegendComponent>() {
	            public int compare(LegendComponent lc1, LegendComponent lc2) {
	                int f1 = lc1.getFrequency();
	                int f2 = lc2.getFrequency();
	                return f2-f1;

	            }
	        });

		return lcList.iterator();
	}

}