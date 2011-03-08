package angstd.ui.views.domaintreeview.components;

import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import angstd.model.domainevent.DomainEventI;
import angstd.ui.views.domaintreeview.manager.DomainEventComponentManager;
import angstd.ui.views.view.components.AbstractViewComponent;

/**
 * The DomainEventComponent is the graphical representation for a 
 * backend domain event (such as insertion or deletion).
 * 
 * 
 * @author Andreas Held
 *
 */
public class DomainEventComponent extends AbstractViewComponent {
	
	/** the component manager used to manage domain event components */
	protected DomainEventComponentManager manager;
	
	/** the backend domain event */
	protected DomainEventI event;

	/**
	 * Constructor for a new DomainEventComponent
	 * 
	 * @param event
	 * 		the backend data domain event
	 * @param manager
	 * 		the component manager used to manage domain event components
	 */
    public DomainEventComponent(DomainEventI event, DomainEventComponentManager manager) {
        if(event == null){
        	throw new RuntimeException("Can not create DomainEventComponent without a DomainEvent !");
        }
        this.event = event;
        this.manager = manager;
	}
    
    /**
     * Returns the backend data domain event
     * 
     * @return
     * 		backend data domain event
     */
    public DomainEventI getDomainEvent() {
    	return event;
    }
    
    /**
     * @see AbstractViewComponent
     */
	public Shape getDisplayedShape () {
		int x = getX();
		int y = getY();
		int width = getWidth();
		int height = getHeight();
		return new RoundRectangle2D.Double(x, y, width, height, 20, 20);
	}
	
	/**
	 * Returns the label of the domain event
	 * 
	 * @return
	 * 		label of the domain event
	 */
	public String getLabel() {
		if (event.isInsertion())
			return "Insertion of domain "+event.getDomain().getFamily().getAcc();
		else
			return "Deletion of domain "+event.getDomain().getFamily().getAcc();
	}
    
}