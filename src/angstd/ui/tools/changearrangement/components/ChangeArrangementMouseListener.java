package angstd.ui.tools.changearrangement.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import angstd.ui.tools.changearrangement.ChangeArrangementView;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.domainview.components.detectors.DomainComponentDetector;
import angstd.ui.views.view.manager.SelectionManager;

/**
 * Mouse Controller for the ChangeArrangementView which allows
 * selecting domains.
 * 
 * @author Andreas Held
 *
 */
public class ChangeArrangementMouseListener extends MouseAdapter {

	/** the local domain view showing the arrangement of interest*/
	protected DomainViewI domView;
	
	/** the tool view providing important methods to ensure the correct workflow */
	protected ChangeArrangementView view;
	
	/** detector for domains on the screen */
	protected DomainComponentDetector domDetector;
	
	
	/**
	 * Constructor for a new domain view mouse controller
	 * 
	 * @param view
	 * 		the view which mouse events should be handled.
	 */
	public ChangeArrangementMouseListener(DomainViewI domView, ChangeArrangementView view) {
		this.domView = domView;
		domDetector = new DomainComponentDetector(domView);
		this.view = view;
	}
	
	/**
	 * Processes left clicks by filling in the domain information.
	 */
	public void mouseClicked(MouseEvent e) {
		SelectionManager<DomainComponent> domSelectionManager = domView.getDomainSelectionManager();
		domSelectionManager.clearSelection();
		
		if (e.getButton() != MouseEvent.BUTTON1)
			return;

		domSelectionManager.setMouseOverComp(null);
		DomainComponent selectedDomain = domDetector.searchDomainComponent(e.getPoint());
		domSelectionManager.setClickedComp(selectedDomain);
		
		if (selectedDomain == null)
			return;
		
		view.refreshDomain(selectedDomain.getDomain());
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
		if (dc == null && domView.getDomainSelectionManager().getMouseOverComp() != null) {
			domView.getDomainSelectionManager().setMouseOverComp(null);// repaint triggered
			return;
		}

		// if a domain is under the cursor
		if (dc != null) {
			// if there was a domain highlighted but it was the same as now
			if (dc.equals(domView.getDomainSelectionManager().getMouseOverComp()))
				return;
			
			// highlight new domain.
			domView.getDomainSelectionManager().setMouseOverComp(dc);			// repaint triggered
			return;
		}
	}
}
