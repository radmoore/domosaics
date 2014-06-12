package domosaics.ui.views.domainview.mousecontroller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.ArrangementPopupMenu;
import domosaics.ui.views.domainview.components.detectors.ArrangementComponentDetector;
import domosaics.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;
import domosaics.ui.views.view.manager.SelectionManager;




/**
 * ArrangementMouseController handles the mouse events in which domain
 * arrangements are interested. The selection of arrangements per mouse 
 * as well as the highlighting of domain arrangements is done in this class.
 * <p>
 * The mouse controller can be added to a DomainView and should
 * be set to inactive, if a {@link SequenceSelectionMouseController} is
 * active.
 * 
 * @author Andreas Held
 *
 */
public class ArrangementMouseController extends MouseAdapter{

	/** the domain view to be handled */
	protected DomainViewI view;
	
	/** detector for arrangements on the screen */
	protected ArrangementComponentDetector arrangementDetector;
	
	
	/**
	 * Constructor for a new selection mouse controller
	 * 
	 * @param view
	 * 		the view which mouse events should be handled.
	 */
	public ArrangementMouseController(DomainViewI view) {
		this.view = view;
		arrangementDetector = new ArrangementComponentDetector(view);
	}
	
	/**
	 * Processes right clicks by showing a context menu for domain arrangements.
	 * left clicks are processed by adding arrangements to a 
	 * user defined selection, if the SelectArrangements mode
	 * is active. <br>
	 * if its clicked on empty space all selected arrangements are deselcted at once
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// get a grap on the arrangement selection manager
		SelectionManager<ArrangementComponent> arrangementSelectionManager = view.getArrangementSelectionManager();
		
		// show context menu on rightclick
		if (e.getButton() == MouseEvent.BUTTON3) {
			// for arrangements: show popup menu if a component was clicked
			arrangementSelectionManager.setMouseOverComp(null); // no tooltip
			ArrangementComponent selectedArrangement = arrangementDetector.searchArrangementComponent(e.getPoint());
			arrangementSelectionManager.setClickedComp(selectedArrangement);
			
			// show arrangement context menu
			if (selectedArrangement != null) {
				// show arrangement context menu
				if(!view.getDomainLayoutManager().isCollapseBySimilarity() && !view.getDomainLayoutManager().isCollapseSameArrangements())
					new ArrangementPopupMenu(view).show(view.getViewComponent(), e.getX(), e.getY());
				return;
			}
			return;
		}
		
		// select an arrangement and delete the previous one in colorSimilarArrangements mode
		if (view.getDomainLayoutManager().isCollapseBySimilarity()) {
			ArrangementComponent dac = arrangementDetector.searchArrangementComponent(e.getPoint());
			if (dac == null) 
				return;

			view.getDomainLayoutManager().deselectAll();
			view.getArrangementSelectionManager().clearSelection();
			arrangementSelectionManager.addToSelection(dac);
			view.getDomainSimilarityManager().init(view, dac.getDomainArrangement());
			return;
		}
		
		// Exit from the sequence comparison mode
		if (view.isCompareDomainsMode())
		{
			view.getDomainViewRenderer().setArrangementRenderer(new BackBoneArrangementRenderer());
			view.setCompareDomainsMode(false);
			view.getDomainLayoutManager().toggleCompareDomainsMode(view.isCompareDomainsMode());
			view.getDomainLayoutManager().deselectAll();
			view.getArrangementSelectionManager().clearSelection();
			view.getDomainSearchOrthologsManager().reset();
			view.getViewComponent().repaint();
			return;
		}
		
		// selection of domain arrangements within selection mode
		if (!view.getDomainLayoutManager().isSelectArrangements())
			return;
		
		// check if there is an arrangement to select at the current position
		ArrangementComponent dac = arrangementDetector.searchArrangementComponent(e.getPoint());
		if (dac == null) {
			view.getDomainLayoutManager().deselectAll();
			view.getArrangementSelectionManager().clearSelection();
			view.getViewComponent().repaint();
			return;
		}

		// add arrangement to the selection if its not already in there
		if (!arrangementSelectionManager.getSelection().contains(dac)) {
			arrangementSelectionManager.addToSelection(dac);	// triggers repaint
		} else { // else remove it from selection
			arrangementSelectionManager.removeFromSelection(dac); // triggers repaint
			view.getDomainLayoutManager().deselectAll();
		}
		
		// Nico: if selected is the last then update "SelectAll status"
		Collection<ArrangementComponent> toSelection = new ArrayList<ArrangementComponent>();
		for (int i = 0; i < view.getDaSet().length; i++) {
			ArrangementComponent ac = view.getArrangementComponentManager().getComponent(view.getDaSet()[i]);
			if(!ac.isVisible())
				continue;
			toSelection.add(ac);
		}

		boolean select = false;
		Iterator<ArrangementComponent> mem = toSelection.iterator();
		while(mem.hasNext() && !select) {
			ArrangementComponent ac = mem.next();
			if(!arrangementSelectionManager.getSelection().contains(ac))
				select = true;
		}
		if(!select) {
			view.getDomainLayoutManager().selectAll();
		}

	}
	
	/**
	 * This method handles the mouse over selection for arrangements.
	 * A repaint is forced by changing the temporary selection within
	 * the selection manager.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {	
		SelectionManager<ArrangementComponent> arrangementSelectionManager = view.getArrangementSelectionManager();

		// if mouse is not in view area but a component is highlighted, undo the highlight
		if (!view.getDomainLayout().getDomainBounds().contains(e.getPoint())) {
			
			if(arrangementSelectionManager.getMouseOverComp() != null)
				arrangementSelectionManager.setMouseOverComp(null);	// repaint triggered

			return;
		}
		
		// check if the cursor on top of an arrangement
		ArrangementComponent dac = arrangementDetector.searchArrangementComponent(e.getPoint());
		
		// in selectionMode highlight only complete arrangements
		if (view.getDomainLayoutManager().isSelectArrangements()) {
			dac = arrangementDetector.searchArrangementComponent(e.getPoint());
		
			// if the cursor is not on top of a protein, but one is selected, deselect it
			if (dac == null && arrangementSelectionManager.getMouseOverComp() != null) {
				arrangementSelectionManager.setMouseOverComp(null);	// repaint triggered
				return;
			}
			
			// if the cursor is on top of an arrangement and this not the already the selected one
			if (dac != null) {
				if (dac.equals(arrangementSelectionManager.getMouseOverComp()))
					return;
				arrangementSelectionManager.setMouseOverComp(dac);  // repaint triggered
			}
			return;
		}
		
		/* normal mode arrangements*/
		// if there was a arrangement highlighted, but not anymore => delete mouseOverComp for the arrangement
		if (dac == null && arrangementSelectionManager.getMouseOverComp() != null) {
			arrangementSelectionManager.setMouseOverComp(null);			// repaint triggered
			return;
		}
		
		// if an arrangement is under the cursor
		if (dac != null) {
			if (dac.equals(arrangementSelectionManager.getMouseOverComp()))
				return;
			arrangementSelectionManager.setMouseOverComp(dac);		  // repaint triggered
		}

	}
}
