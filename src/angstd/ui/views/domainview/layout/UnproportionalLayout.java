package angstd.ui.views.domainview.layout;

import java.awt.Dimension;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;



/**
 * UnproportionalLayout defines the requested properties for 
 * arrangements and domains for AbstractDomainlayout.
 * 
 * @author Andreas Held
 *
 */
public class UnproportionalLayout extends AbstractDomainLayout {
	
	/** the size of a domain within the proportional view */
	public static final int UNPROPSIZE = 40;
	
	/** the distance between two domain within the proportional view */
	public static final int UNPROPDISTANCE = 3;
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getDomainX(Domain dom) {
		DomainComponent dc = view.getArrangementComponentManager().getDomainComponent(dom);
		int domX = dom.getArrangement().getIndexOf(dom) * (UNPROPSIZE + UNPROPDISTANCE);
		domX += view.getDomainShiftManager().getShiftCol(dc)*(UNPROPSIZE + UNPROPDISTANCE);
		return domX;
	}
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getDomainWidth(Domain dom){
		return UNPROPSIZE;
	}	
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getArrangementWidth(DomainArrangement da) {
		int maxShiftCol = view.getDomainShiftManager().getMaxShiftCol(view.getArrangementComponentManager().getComponent(da));
		return (da.countDoms()+maxShiftCol)*(UNPROPSIZE + UNPROPDISTANCE);
	}
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getArrangementX(ArrangementComponent dac) {
		return 0;
	}
	
	/**
	 * Returns the actual column size depending on the fitToScreen mode
	 * 
	 * @return
	 * 		colums size for domains
	 */
	public int getColumnSize() {
		if (view.getDomainLayoutManager().isFitDomainsToScreen() && param.maxLen > getDomainBounds().width)
			return getDomainBounds().width / param.maxDoms;
		return UNPROPSIZE;
	}
	
	//used for inner node arrangements
	public static Dimension getPreferredSize(DomainArrangement da) {
		int width = da.countDoms()*(UNPROPSIZE + UNPROPDISTANCE);
		int height = DomainLayout.MAX_DA_HEIGHT;
		return new Dimension(width, height);
	}
	
}
