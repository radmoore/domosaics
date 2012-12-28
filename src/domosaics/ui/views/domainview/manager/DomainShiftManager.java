package domosaics.ui.views.domainview.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.view.manager.DefaultViewManager;


/**
 * The DomainShiftManager manages the shift coordinates for 
 * arrangement and domain view components. <br>
 * Its possible to shift an arrangement left and right to align them
 * manually. Therefore a new start position has to be assigned to
 * the view components. <br>
 * DomainComponents can be shifted within the unproportional view 
 * column wise. <b>
 * This manager stores the shifted start positions and allows easy managing.
 * It also triggers a relayout just for the shifted domain arrangement.
 * <p>
 * Events are received by {@link ShiftComponentMouseController}. 
 * 
 * @author Andreas Held
 *
 */
public class DomainShiftManager extends DefaultViewManager {

	/** map assigning shifted arrangements its shift position */
	protected Map<ArrangementComponent, Integer> dac2shift;
	
	/** map assigning shifted domain components its shift position */
	protected Map<DomainComponent, Integer> dc2shiftCol;
	
	/** map assigning maximal shift column to an arrangement */
	protected Map<ArrangementComponent, Integer> dac2maxShiftCol;
	
	/**
	 * Constructor for a new ShiftManager.
	 */
	public DomainShiftManager() {
		dac2shift = new HashMap<ArrangementComponent, Integer>();
		dc2shiftCol = new HashMap<DomainComponent, Integer>();
		dac2maxShiftCol = new HashMap<ArrangementComponent, Integer>();
	}
	
	/**
	 * Resets the DomainShiftManager resulting in all shift operations
	 * being undone.
	 */
	public void reset() {
		dac2shift.clear();
		dc2shiftCol.clear();
		dac2maxShiftCol.clear();
		structuralChange();
	}
	
	/**
	 * Returns whether or not at least one arrangement or domain 
	 * is shifted
	 * 
	 * @return
	 * 		whether or not at least one arrangement or domain is shifted
	 */
	public boolean isActive() {
		if (!dac2shift.isEmpty())
			return true;
		if (!dc2shiftCol.isEmpty())
			return true;
		return false;
	}
	
	/**
	 * Sets a new shift position for an ArrangementComponent.
	 * If the new shift position would exceed the left border for
	 * layouting arrangements its set to zero. This method triggers a 
	 * relayout for the shifted arrangement component only.
	 * 
	 * @param dac
	 * 		the domain arrangement being shifted
	 * @param shift
	 * 		the new shift position for the arrangement
	 */
	public void setShift(ArrangementComponent dac, int shift) {
		if (getShift(dac)+shift < 0) 
			dac2shift.put(dac, 0);
		else
			dac2shift.put(dac, shift);
	}
	
	/**
	 * Returns the shift coordinate modification for arrangement 
	 * components.
	 * 
	 * @param dac
	 * 		the arrangement component which shift coordinate is requested
	 * @return
	 * 		the shift coordinate modification for the specified arrangement component
	 */		 
	public int getShift(ArrangementComponent dac) {
		if (dac2shift.get(dac) == null)
			return 0;
		return dac2shift.get(dac);
	}
	
	/**
	 * Sets the maximal shift column for arrangement components
	 * 
	 * @param dac
	 * 		the arrangement component which maximal shift is set
	 * @param maxShiftCol
	 * 		new maximal shift column for the specified arrangement component
	 */
	public void setMaxShiftCol(ArrangementComponent dac, int maxShiftCol) {
		dac2maxShiftCol.put(dac, maxShiftCol);
	}
	
	/**
	 * Returns the maximal shift column for arrangement component
	 * 
	 * @param dac
	 * 		the arrangement component which maximal shift columns is requested
	 * @return
	 * 		maximal shift column for the specified arrangement component
	 */
	public int getMaxShiftCol(ArrangementComponent dac) {
		if (dac2maxShiftCol.get(dac) == null)
			return 0; 
		return dac2maxShiftCol.get(dac);
	}
	
	/**
	 * Sets the shift column for domain components in 
	 * unproportional view. This method triggers a relayout
	 * for the shifted arrangement component only.
	 * 
	 * @param dac
	 * 		the arrangement component in which a domain was shifted
	 * @param dc
	 * 		the shifted domain component
	 * @param shiftCol
	 * 		the column to which the domain was shifted
	 */
	public void setShift(ArrangementComponent dac, DomainComponent dc, int shiftCol) {
		int oldShiftCol = getShiftCol(dc);
		
		// determine a forward or backward shift
		if (shiftCol <= 0)
			setShiftDomBackward(dac, dc, 0);
		else {
			if (shiftCol > oldShiftCol)
				setShiftDomForward(dac, dc, shiftCol);
			else
				setShiftDomBackward(dac, dc, shiftCol);
		}
	}

	/**
	 * Return the shift column for the specified domain component.
	 * 
	 * @param dc
	 * 		domain component which shift column is requested
	 * @return
	 * 		shift column for the specified domain component.
	 */
	public int getShiftCol(DomainComponent dc) {
		if (dc2shiftCol.get(dc) == null) 
			return 0;
		return dc2shiftCol.get(dc);
	}
	
	/**
	 * Helper method to shift a domain component to the right (forward).
	 * If domains are to the right of the shifted domain they have to
	 * be shifted also. 
	 * 
	 * @param dac
	 * 		the arrangement component in which a domain is shifted
	 * @param dc
	 * 		the shifted domain component
	 * @param shiftCol
	 * 		the column to which the specified domain component is shifted
	 */
	protected void setShiftDomForward(ArrangementComponent dac, DomainComponent dc, int shiftCol) {
		// set the new shift column
		dc2shiftCol.put(dc, shiftCol);
		
		// check if domains after the current one have to be shifted as well
		boolean startShifting = false;
		Iterator<DomainComponent> iter = dac.getDomainComponents();
		DomainComponent domC = null;
		
		while(iter.hasNext()) {
			domC = iter.next();

			if (startShifting && shiftCol >= getShiftCol(domC)) 
				dc2shiftCol.put(domC, shiftCol);

			if (domC.equals(dc)) 
				startShifting = true;
		}
		
		dac2maxShiftCol.put(dac, dc2shiftCol.get(domC));
	}	
  
	/**
	 * Helper method to shift a domain component to the left (backward).
	 * If domains are to the left of the shifted domain they have to
	 * be shifted also. 
	 * 
	 * @param dac
	 * 		the arrangement component in which a domain is shifted
	 * @param dc
	 * 		the shifted domain component
	 * @param shiftCol
	 * 		the column to which the specified domain component is shifted
	 */
	public void setShiftDomBackward(ArrangementComponent dac, DomainComponent dc, int shiftCol) {
		// set the new shift column
		dc2shiftCol.put(dc, shiftCol);
  	
		// check if domains after the current one have to be shifted as well
		boolean stopShifting = false;
		Iterator<DomainComponent> iter = dac.getDomainComponents();
		DomainComponent domC = null;
		
		while(iter.hasNext()) {
			domC = iter.next();
  		
			if (domC.equals(dc)) 
				stopShifting = true;
  		
			if (!stopShifting && shiftCol <= getShiftCol(domC))
				dc2shiftCol.put(domC, shiftCol);

		}
		dac2maxShiftCol.put(dac, dc2shiftCol.get(domC));
	}

}
