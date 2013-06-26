package domosaics.ui.views.domainview.layout;

import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.views.domainview.components.ArrangementComponent;

/**
 * ProportionalLayout defines the requested properties for 
 * arrangements and domains for AbstractDomainlayout.
 * 
 * @author Andreas Held
 *
 */
public class ProportionalLayout extends AbstractDomainLayout {
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getDomainX(Domain dom) {
		return dom.getFrom();
	}
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getDomainWidth(Domain dom){
		// Nico : the -2 is a trick to correctly select sequences
		return dom.getLen()-2;
	}
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getArrangementX(ArrangementComponent dac) {
		return view.getDomainShiftManager().getShift(dac);
	}
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getArrangementWidth(DomainArrangement da) {
		return da.getLen(false);
	}

}
