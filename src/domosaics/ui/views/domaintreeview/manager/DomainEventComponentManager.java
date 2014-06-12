package domosaics.ui.views.domaintreeview.manager;

import domosaics.model.domainevent.DomainEventI;
import domosaics.ui.views.domaintreeview.components.DomainEventComponent;
import domosaics.ui.views.view.manager.AbstractComponentManager;
import domosaics.ui.views.view.manager.ComponentManager;

/**
 * The DomainEventComponentManager maps domain events like
 * insertions and deletions to their graphical components 
 * (DomainEventComponent). <br>
 * Therefore it is possible to access all DomainEventComponents
 * provided by a DomainTreeView.
 * <p>
 * The DomainEventComponentManager extends the 
 * {@link AbstractComponentManager} which handles the component mapping.
 * The getComponent() method is extended in this manager to manage the
 * initialization of new DomainEventComponents. 
 * <p>
 * 
 * @author Andreas Held
 *
 */
public class DomainEventComponentManager extends AbstractComponentManager<DomainEventI, DomainEventComponent> {
	
	/**
	 * Returns the mapped DomainEventComponent for the specified
	 * backend data object. If no DomainEventComponent exists
	 * yet this method creates a new instance.
	 * 
	 * @see ComponentManager
	 */
	@Override
	public DomainEventComponent getComponent(DomainEventI evt){
		if (evt == null) 
			return null;

		DomainEventComponent component = backend2components.get(evt);
		if (component == null) {
			component = new DomainEventComponent(evt, this);
			backend2components.put(evt, component);
		}
		return component;
	}

}
