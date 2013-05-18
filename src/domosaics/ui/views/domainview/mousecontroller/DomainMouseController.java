package domosaics.ui.views.domainview.mousecontroller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.components.DomainPopupMenu;
import domosaics.ui.views.domainview.components.detectors.DomainComponentDetector;
import domosaics.ui.views.view.manager.SelectionManager;




/**
 * DomainMouseController handles the mouse events in which domains
 * are interested. The highlighting of domains is done in this class.
 * <p>
 * The mouse controller can be added to a DomainView and should
 * be set to inactive, if a {@link SequenceSelectionMouseController} is
 * active.
 * 
 * @author Andreas Held
 *
 */
public class DomainMouseController extends MouseAdapter {
	
	/** the domain view to be handled */
	protected DomainViewI view;
	
	/** detector for domains on the screen */
	protected DomainComponentDetector domDetector;
	
	
	/**
	 * Constructor for a new domain view mouse controller
	 * 
	 * @param view
	 * 		the view which mouse events should be handled.
	 */
	public DomainMouseController(DomainViewI view) {
		this.view = view;
		domDetector = new DomainComponentDetector(view);
	}

	/* ************************************************************************************ *
	 * 									HANDLE MOUSEEVENTS									*
	 * ************************************************************************************ */

	/**
	 * Processes right clicks by showing a context menu for domains.
	 * On left click all domains are deselected at once. 
	 */
	public void mouseClicked(MouseEvent e) {
		SelectionManager<DomainComponent> domSelectionManager = view.getDomainSelectionManager();
		// reset domain selection, e.g. selected because of the find action
		//domSelectionManager.clearSelection();
		
		// context menu handling on right click
		if (e.getButton() == MouseEvent.BUTTON3) {
			
			// for domains: show context menu if a component was clicked
			domSelectionManager.setMouseOverComp(null); // no tooltip
			DomainComponent selectedDomain = domDetector.searchDomainComponent(e.getPoint());
			domSelectionManager.setClickedComp(selectedDomain);
			
			// show domain context menu
			if (selectedDomain != null) {
				if(!view.getDomainLayoutManager().isCollapseBySimilarity() && !view.getDomainLayoutManager().isCollapseSameArrangements())
					new DomainPopupMenu(view).show(view.getViewComponent(), e.getX(), e.getY());
				return;
			}
		}
	}

	/**
	 * This method handles the mouse over selection for domains.
	 * A repaint is forced by changing the temporary selection within
	 * the selection manager.
	 */
	public void mouseMoved(MouseEvent e) {	
		// handle domain mouse overs
		DomainComponent dc = domDetector.searchDomainComponent(e.getPoint());
		
		// if there was a domain highlighted, but not anymore => delete mouseOverComp for the domain
		if (dc == null && view.getDomainSelectionManager().getMouseOverComp() != null) {
			view.getDomainSelectionManager().setMouseOverComp(null);// repaint triggered
			return;
		}

		// if a domain is under the cursor
		if (dc != null) {
			if(!dc.isVisible())
				return;
			
			// if there was a domain highlighted but it was the same as now
			if (dc.equals(view.getDomainSelectionManager().getMouseOverComp()))
				return;
			
			// highlight new domain.
			view.getDomainSelectionManager().setMouseOverComp(dc);			// repaint triggered
			return;
		}
	}

}
