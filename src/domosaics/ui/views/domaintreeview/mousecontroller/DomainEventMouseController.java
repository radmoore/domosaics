package domosaics.ui.views.domaintreeview.mousecontroller;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.components.DomainEventComponent;
import domosaics.ui.views.domaintreeview.components.DomainEventComponentDetector;




/**
 * DomainEventMouseController handles the mouse events which are made on
 * a domain tree view (and specially on domain events). 
 * Therefore this class must decide whether or not on a specified position
 * is a domain event present. To do so a DomainEventComponentDetector 
 * is used.
 * <p>
 * Basically this mouse adapter provides the opportunities to select
 * domain events via mouse over. 
 * 
 * @author Andreas Held
 *
 */
public class DomainEventMouseController extends MouseAdapter {
	
	/** the domain tree view to be handled */
	protected DomainTreeViewI view;

	/** the current cursor position */
	protected Point movePoint;
	
	/** component detector for domain events */
	protected DomainEventComponentDetector domEventComponentDetector;
	

	/**
	 * Constructor for a new DomainEventMouseController
	 * 
	 * @param view
	 * 		the domain tree view to be handled
	 */
	public DomainEventMouseController(DomainTreeViewI view) {
		this.view = view;
		this.domEventComponentDetector = new DomainEventComponentDetector(view);
	}
	
	/**
	 * Helper method to select a mouse over domain event
	 * 
	 * @param p
	 * 		the actual location of the cursor
	 * @return
	 * 		the selected domain event under the cursor (or null)
	 */
	protected DomainEventComponent selectOverDomainEvent(Point p) {
		return domEventComponentDetector.searchDomainEventComponent(p);
	}

	/**
	 * The mouse moved method is used to select domain events.
	 * 
	 * A repaint is forced by changing the temporary selection
	 */
	@Override
	public void mouseMoved(MouseEvent e) {	
		movePoint = e.getPoint();
		if (!view.getTreeLayout().getTreeBounds().contains(movePoint)) {
			if(view.getDomainEventSelectionManager().getMouseOverComp() != null)
				view.getDomainEventSelectionManager().setMouseOverComp(null);
			return;
		}
		
		if (!view.getDomainTreeLayoutManager().isShowInDels())
			return;

		DomainEventComponent dec = selectOverDomainEvent(movePoint);
		
		if (dec == null && view.getDomainEventSelectionManager().getMouseOverComp() == null) 
			return;
		
		if (dec == null && view.getDomainEventSelectionManager().getMouseOverComp() != null) {
			view.getDomainEventSelectionManager().setMouseOverComp(null);
			return;
		}
		
		if (dec != null && dec.equals(view.getDomainEventSelectionManager().getMouseOverComp()))
			return;
			
		view.getDomainEventSelectionManager().setMouseOverComp(dec);
	}
	
	public Point getMovePoint() {
		return movePoint;
	}
}
