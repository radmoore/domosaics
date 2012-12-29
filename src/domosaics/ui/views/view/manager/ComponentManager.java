package domosaics.ui.views.view.manager;

import java.util.Iterator;

import domosaics.model.DoMosaicsData;
import domosaics.ui.views.view.components.ViewComponent;




/**
 * The ComponentManager interface is a manager which maps backend data
 * to their UI ViewComponents. The actual Types are generic as long as 
 * the bakend data implements the {@link DoMosaicsData} and the
 * UI component the {@link ViewData} interface.
 * <p>
 * The interface provides methods for requesting a view component based on
 * a specified data object, such as DomainArrangement. The creation of
 * the view components should be performed by the implementing manager 
 * classes.
 * <p>
 * The basic implementation for this interface is {@link AbstractComponentManager}
 * and specific implementation can extend this manager to add functionalities.
 * <p>
 * The {@link ViewManager} interface is extended to ensure the communication
 * with the observed view.
 * 
 * @author Andreas Held
 *
 * @param <T>
 * 		generic type for the backend data
 * @param <C>
 * 		generic type for the UI view component
 */
public interface ComponentManager<T extends DoMosaicsData, C extends ViewComponent> extends ViewManager{
	
	/**
	 * Return the UI component for a specified backend data object. 
	 * 
	 * @param backend
	 * 		the backend data (e.g. a domain arrangement object)
	 * @return
	 * 		the corresponding ViewComponent (e.g. ArrangementComponent)
	 */
	public C getComponent(T backend);

	/**
	 * Return an iterator over all stored ViewComponent objects for 
	 * which backend data exists.
	 * 
	 * @return
	 * 		iterator over all stored ViewComponent objects
	 */
   	public Iterator<C> getComponentsIterator();
    
	/**
	 * Clears the manager and removes all mappings.
	 */ 
	public void clear();

	/**
	 * Returns the number of stored mappings between backend data and
	 * view components.
	 * 
	 * @return
	 * 		number of stored mappings between backend data and view components.
	 */
	public int countComponents();
}
