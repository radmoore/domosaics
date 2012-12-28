package angstd.ui.views.domainview.mousecontroller;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.domainview.components.detectors.ArrangementComponentDetector;
import angstd.ui.views.domainview.components.detectors.DomainComponentDetector;
import angstd.ui.views.domainview.layout.UnproportionalLayout;
import angstd.ui.views.domainview.manager.DomainShiftManager;



/**
 * This mouse handler changes the start point of domain arrangements 
 * and domains making it possible to shift them around. <br>
 * The shifting is only allowed within the proportional view for
 * domain arrangements and within the unproportional view for domains.
 * <p>
 * The DomainShiftManager handles the new shift positions and should be
 * used to process the positions calculated in this calculated.
 * 
 * @author Andreas Held
 *
 */
public class ShiftComponentsMouseController extends MouseAdapter {
	
	/** the view providing the shift functionality */
	protected DomainViewI view;
	
	/** detector for domain components on the screen */
	protected DomainComponentDetector domDetector;
	
	/** detector for arrangement components on the screen */
	protected ArrangementComponentDetector arrangementDetector;
	
	/** dragging mode enables the shift */
	protected boolean dragging = false;
	
	/** the start x coordinate before the dragging started */
	protected int startX;
	
	/** the ArrangementComponent which is shifted */
	protected ArrangementComponent moveDac = null;	
	
	/** the old position of the arrangement component */
	protected int oldshiftX;
	
	/** the DomainComponent which is shifted */
	protected DomainComponent moveDc = null;	
	
	/** the old position of the domain component */
	protected int oldShiftColumn;
	
	/** the view manager handling the shift management */
	protected DomainShiftManager shiftManager;
	
	
	/**
	 * New MouseController providing the shift functionality for 
	 * arrangements and domains.
	 * 
	 * @param view
	 * 		view providing the shift functionality
	 */
	public ShiftComponentsMouseController(DomainViewI view) {
		this.view = view;
		arrangementDetector = new ArrangementComponentDetector(view);
		domDetector = new DomainComponentDetector(view);
		shiftManager = view.getDomainShiftManager();
	}
	
	/* ************************************************************************************ *
	 * 									DETECT COMPONENTS									*
	 * ************************************************************************************ */
	
	/**
	 * Wrapper around the DomainDetector to search for domains at a 
	 * specified point on the screen.
	 * 
	 * @param p
	 * 		the point where the domain search is triggered
	 * @return
	 * 		the domain at the specified point or null
	 */
	private DomainComponent selectOverDomain(Point p) {
		return domDetector.searchDomainComponent(p);
	}
	
	/**
	 * Wrapper around the ArrangementDetector to search for 
	 * arrangements at a specified point on the screen.
	 * 
	 * @param p
	 * 		the point where the arrangement search is triggered
	 * @return
	 * 		the arrangement at the specified point or null
	 */
	private ArrangementComponent selectOverArrangement(Point p) {
		return arrangementDetector.searchArrangementComponent(p);
	}
	
	
	/* ************************************************************************************ *
	 * 									HANDLE MOUSEEVENTS									*
	 * ************************************************************************************ */

	/**
	 * Start of shift process: Depending on proportional
	 * or unproportional view the arrangement or domain component is set.
	 */
	public void mousePressed(MouseEvent e) {	
		if (view.getDomainLayoutManager().isMsaView())
			return;
		
		if (e.getButton() != MouseEvent.BUTTON1) 
			return;
		
		// depending on the view shift arrangements or domains
		if (view.getDomainLayoutManager().isProportionalView()) {
			ArrangementComponent dac = selectOverArrangement(e.getPoint());
			if (dac == null)
				return;
			moveDac = dac;
			oldshiftX = shiftManager.getShift(moveDac);
		}
		 
		if (view.getDomainLayoutManager().isUnproportionalView()) {
			DomainComponent dc = selectOverDomain(e.getPoint());
			ArrangementComponent dac = selectOverArrangement(e.getPoint());
			if (dc == null)
				return;
			moveDac = dac;
			moveDc = dc;
			oldShiftColumn = shiftManager.getShiftCol(moveDc);
		}
		
		startX = e.getPoint().x;
		dragging = true;
	}
	
	
	/**
	 * End of shift process: Reset all used variables and repaint
	 */
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) 
			return;

		dragging = false;
		startX = -1;
		moveDac = null;
		moveDc = null;
		
		view.getViewComponent().repaint();
		return;
	}

	/**
	 * Shift process: Again depending on the actual view 
	 * (proportional or unproportional) the new shift coordinates
	 * are set for arrangements and domains. Delegates to 
	 * DomainShiftManager to actually store process the shifting.
	 */
	public void mouseDragged(MouseEvent e) {
		if (!dragging)
			return;

		// change positions of dragged arrangement component
		if (view.getDomainLayoutManager().isProportionalView() && moveDac != null) {
			int newshiftX = oldshiftX+(e.getPoint().x - startX);
			shiftManager.setShift(moveDac, newshiftX);
			
			view.getDomainLayoutManager().structuralChange();
			
			// layout only shifted arrangement (not working with domain tree view)
//			Rectangle lyoutBounds = view.getDomainLayout().getDomainBounds();
//			view.getDomainLayout().layoutArrangement(moveDac, lyoutBounds.x, moveDac.getY(), lyoutBounds.width, lyoutBounds.height);
//			view.getDomainLayoutManager().visualChange();
		
			return;
		}
		
		// change positions of dragged domains in unprop view
		if (view.getDomainLayoutManager().isUnproportionalView() && moveDc != null) {
			UnproportionalLayout layout = null;
			if (view instanceof DomainTreeViewI)
				layout = (UnproportionalLayout) ((DomainTreeViewI) view).getDomainTreeLayout().getDomainLayout();
			else
				layout = (UnproportionalLayout) view.getDomainLayout();

			int shiftcols = oldShiftColumn+(e.getPoint().x-startX)/layout.getColumnSize();
			shiftManager.setShift(moveDac, moveDc, shiftcols);
			
			view.getDomainLayoutManager().structuralChange();
			
			// layout only shifted arrangement (not working with domain tree view)
//			Rectangle lyoutBounds = view.getDomainLayout().getDomainBounds();
//			view.getDomainLayout().layoutArrangement(moveDac, lyoutBounds.x, moveDac.getY(), lyoutBounds.width, lyoutBounds.height);
//			view.getDomainLayoutManager().visualChange();
			
			return;
		}
	}
}
