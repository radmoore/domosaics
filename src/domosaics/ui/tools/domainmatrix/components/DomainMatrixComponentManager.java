package domosaics.ui.tools.domainmatrix.components;

import domosaics.ui.views.view.manager.AbstractComponentManager;

/**
 * The DomainMatrixComponentManager manages the mapping of Pairs to 
 * DomainMatrixEntries. 
 * 
 * @author Andreas Held
 *
 */
public class DomainMatrixComponentManager extends AbstractComponentManager<Pair, DomainMatrixEntry> {
		
	/**
	 * Returns the domain matrix entry component for the specified 
	 * pair.
	 * 
	 * @param pair
	 * 		the pair which domain entry component is requested
	 * @return
	 * 		the corresponding domain entry component for the specified pair
	 */
	public DomainMatrixEntry getComponent(Pair pair){
		if (pair == null) 
			return null;

		DomainMatrixEntry component = backend2components.get(pair);
		if (component == null) {
			component = new DomainMatrixEntry(pair);
			backend2components.put(pair, component);
		}
			
		return component;
	}
}
