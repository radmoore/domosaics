package domosaics.ui.views.view.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import domosaics.model.AngstdData;
import domosaics.ui.views.view.components.ViewComponent;


/**
 * Basic implementation for the ComponentManager interface.
 * <p>
 * This class implements the basic functionalities for a ComponentManager.
 * The getComponent() method is abstract, because the ViewComponent creation has to be managed
 * in there.
 * <p>
 * The {@link DefaultViewManager} is extended to ensure the communication
 * with the observed view.
 * <p>
 * For further details see {@link ComponentManager} interface.
 * 
 * 
 * @author Andreas Held
 *
 * @param <T>
 * 		@see ComponentManager
 * @param <C>
 * 		@see ComponentManager
 */
public abstract class AbstractComponentManager <T extends AngstdData, C extends ViewComponent> 
	   		extends DefaultViewManager
	   		implements ComponentManager<T, C>
{
	
	/** mapping data structure between backend data and their view component */
	protected Map<T, C> backend2components;

	
	/**
	 * Basic Constructor initializing the mapping data structure
	 */
	public AbstractComponentManager() {
		backend2components = new HashMap<T, C>();
	}

	/**
	 * @see ComponentManager
	 */ 
	public Iterator<C> getComponentsIterator() {
		return backend2components.values().iterator();
	}

	/**
	 * @see ComponentManager
	 */ 
	public void clear(){
		backend2components.clear();
	}
	
	/**
	 * @see ComponentManager
	 */ 
	public int countComponents() {
		return backend2components.size();
	}
	
	/**
	 * @see ComponentManager
	 */ 
	public void removeComponent(T id) {
		if (backend2components.get(id) != null)
			backend2components.remove(id);
	}
	
	/**
	 * @see ComponentManager
	 */ 
	public abstract C getComponent(T backend);
	
}
