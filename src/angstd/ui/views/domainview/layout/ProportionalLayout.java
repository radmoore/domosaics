package angstd.ui.views.domainview.layout;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.views.domainview.components.ArrangementComponent;

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
		return dom.getLen();
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
