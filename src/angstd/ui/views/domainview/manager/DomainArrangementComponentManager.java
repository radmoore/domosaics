package angstd.ui.views.domainview.manager;

import java.util.Iterator;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.view.manager.AbstractComponentManager;
import angstd.ui.views.view.manager.ComponentManager;



/**
 * The DomainArrangementComponentManager maps DomainArrangementComponents
 * to their backend data DomainArrangement as well as DomainComponents
 * to their backend data Domain. <br>
 * Therefore it is possible to access all DomainArrangementComponents
 * provided by the DomainView as well as their DomainComponents.
 * <p>
 * The DomainArrangementComponentManager extends the 
 * {@link AbstractComponentManager} which handles the component mapping.
 * The getComponent() method is extended in this manager to manage the
 * initialization of new DomainArrangementComponents. <p>
 * Also this class extends the AbstractComponentManager by 
 * providing methods to manage the DOmainComponents.
 * 
 * @author Andreas Held
 *
 */
public class DomainArrangementComponentManager extends AbstractComponentManager<DomainArrangement, ArrangementComponent> {

	/** internal domain component manager */
	protected DomainComponentManager dcm;
	
	/**
	 * Constructor for a new DomainArrangementComponentManager
	 * which initializes the internal DomainComponentManager.
	 */
	public DomainArrangementComponentManager() {
		super();
		dcm =  new DomainComponentManager();
	}
	
	/**
	 * Returns the component manager used to manage the domain mappings
	 * 
	 * @return
	 * 		the component manager used to manage the domain mappings	
	 */
	public DomainComponentManager getDomainComponentManager() {
		return dcm;
	}

	/**
	 * Returns the mapped DomainArrangementComponent for the specified
	 * backend data object. If no DomainArrangementComponent exists
	 * yet this method creates a new instance.
	 * 
	 * @see ComponentManager
	 */
	public ArrangementComponent getComponent(DomainArrangement backend) {
		if (backend == null) 
			return null;

		ArrangementComponent component = backend2components.get(backend);
		if (component == null) {
			component = new ArrangementComponent(backend, this);
			backend2components.put(backend, component);
			component.setVisible(true);
		}
		
		return component;
	}
	
	/**
	 * Method to change the visibility status for a domain arrangement.
	 * The visibility status for the domains contained by the arrangement
	 * are set automatically.
	 * 
	 * @param dac
	 * 		the arrangement component which visibility status should be changed
	 * @param visible
	 * 		the new visibility status for the specified arrangement component
	 */
	public void setVisible(ArrangementComponent dac, boolean visible) {
		dac.setVisible(visible);
    	for (DomainComponent dc :getDomains(dac))
    		dc.setVisible(visible);
	}
	
	/**
	 * Return iterator over all stored DOmainArrangementComponents
	 * 
	 * @return
	 * 		iterator over all stored DOmainArrangementComponents
	 */
   	public Iterator<DomainComponent> getDomainComponentsIterator(){  
   		return dcm.getComponentsIterator();
   	}
   	
   	/**
   	 * Wrapper method around the DomainComponentmanagers
   	 * getComponent() method to  retrieve the mapped DomainComponent
   	 * for the specified Domain.
   	 * 
   	 * @param dom
   	 * 		the backend Domain mapped to the DomainComponent.
   	 * @return
   	 * 		the DomainComponent mapped to the specified Domain.
   	 */
   	public DomainComponent getDomainComponent(Domain dom) {
   		return dcm.getComponent(dom);
   	}

   	/**
   	 * Return the DomainComponents embedded within a DomainArrangementComponent.
   	 * 
   	 * @param da
   	 * 		the DomainArrangementComponent which DomainComponents are requested
   	 * @return
   	 * 		Iterable over all DomainComponents embedded within the specified DomainArrangementComponent
   	 */
	public Iterable<DomainComponent> getDomains(ArrangementComponent dac) {
		return new DomainIterable(dac.getDomainArrangement());
	}
	
	/**
	 * Iterable class for DomainComponents within a DomainArrangement.
	 * This class provides all iterator functionalities
	 * to retrieve successively the domains components embedded within a 
	 * DomainArrangementComponent.
	 * 
	 * @author Andreas Held
	 *
	 */
	class DomainIterable implements Iterator<DomainComponent>, Iterable<DomainComponent> {
		protected Iterator<Domain> it;

		public DomainIterable(DomainArrangement da) {
			it = da.getDomainIter();
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		public DomainComponent next() {
			return dcm.getComponent(it.next());
		}
		
		public void remove() {
			throw new RuntimeException("Method not supported");
		}

		public Iterator<DomainComponent> iterator() {
			return this;
		}
	}
	
	/**
	 * Internal class to map DomainComponents to their backend data
	 * represented by Domain objects. This class is choosed to be
	 * internal to provide all methods for managing
	 * view components within a DOmainView in one class.
	 * 
	 * @author Andreas Held
	 *
	 */
	public class DomainComponentManager extends AbstractComponentManager<Domain, DomainComponent> {
		
		/**
		 * Returns the mapped DomainComponent for the specified
		 * backend data object. If no DomainComponent exists
		 * yet this method creates a new instance.
		 */
		public DomainComponent getComponent(Domain dom){
			if (dom == null) 
				return null;

			DomainComponent component = backend2components.get(dom);
			if (component == null) {
				component = new DomainComponent(dom);
				backend2components.put(dom, component);
				component.setVisible(true);
			}

			return component;
		}
	}

}