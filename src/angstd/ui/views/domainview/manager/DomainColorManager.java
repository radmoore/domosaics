package angstd.ui.views.domainview.manager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainFamily;
import angstd.ui.views.domainview.components.DomainColors;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.view.manager.DefaultViewManager;

/**
 * DomainColorManager defines the mapping between DomainFamilys 
 * and their color. This class extends DefaultViewManager to ensure the
 * communication with the managed view.
 * <p>
 * Because the colors are mapped by DomainFamily instead of DomainComponent
 * its not possible to colorize each domain within its color.
 * If this is wanted an extra renderer has to be specified.
 * <p>
 * To request a color for a domain the DomainComponent as well as the family
 * can be used.
 * <p>
 * If no color is associated with the requested DomainFamily a new Color
 * from {@link DomainColors} is associated ensuring that each 
 * DomainColor is used before starting with the first color again.
 * 
 * @author Andreas Held
 *
 */
public class DomainColorManager extends DefaultViewManager {

	/** mapping between DomainFamilies and the associated colors */
	protected Map<String, Color> doms2colors;
	
	
	/**
	 * Basic constructor for a new color manager.
	 */
	public DomainColorManager() {
		doms2colors = new HashMap<String, Color>();
	}
	
  	/**
  	 * Returns the associated color for the domain components 
	 * DomainFamily.<br>
	 * Delegating to the getDomainColor(DomainFamily) method.
  	 * 
  	 * @param dc
  	 * 		the DomainComponent which color is requested
  	 * @return
  	 * 		the associated color for the domain components DomainFamily
  	 */
  	public Color getDomainColor(DomainComponent dc) {
  		return getDomainColor(dc.getDomain().getFamily());
   	}
  	
  	/**
  	 * Returns the associated color for the domains DomainFamily.<br>
	 * Delegating to the getDomainColor(DomainFamily) method.
	 * 
  	 * @param dom
  	 * 		the domain which color is requested
  	 * @return
  	 * 		the associated color for the domains DomainFamily
  	 */
  	public Color getDomainColor(Domain dom) {
  		return getDomainColor(dom.getFamily());
   	}
  	
  	public boolean hasDomainColor(DomainFamily fam) {
  		return doms2colors.containsKey(fam.getAcc());
  	}
  	
	/**
	 * Returns the associated color for the DomainFamily. If
	 * no color is associated yet a new color is chosen.
	 * 
	 * @param fam
	 * 		the DomainFamily which color is requested
	 * @return
	 * 		the associated color for the DomainFamily
	 */
  	public Color getDomainColor(DomainFamily fam) {
//  		System.out.println("Looking up color for dom: "+fam.getAcc());
   		if ( (doms2colors.get(fam.getAcc()) == null) && (doms2colors.get(fam.getID()) == null) ) {
//   			System.out.println("Assigning new color to: "+fam.getAcc());
   			int colorIndex = getNumDomainColors() % DomainColors.values().length;
   			Color color = DomainColors.values()[colorIndex].getColor();
   			doms2colors.put(fam.getAcc(), color);
   			if (! fam.getAcc().equals(fam.getID()) )
   				doms2colors.put(fam.getID(), color);
   			return color;
   		}
   		else
//   			System.out.println("Returning known color for: "+fam.getID());
   			return doms2colors.get(fam.getAcc());
   	}

	/**
	 * Return the number of associated colors.
	 * 
	 * @return
	 * 		number of associated colors.
	 */
	public int getNumDomainColors() {
		return doms2colors.size();
	}

	/**
	 * Maps a new color to the specified DomainFamily. A repaint
	 * event is triggered afterwards.
	 * 
	 * @param dc
	 * 		the DomainComponent which DomainFamily has to be associated with the specified color.
	 * @param color
	 * 		the new color for the specified DomainFamily
	 */
	public void setDomainColor(DomainComponent dc, Color color) {
		doms2colors.put(dc.getDomain().getFamily().getAcc(), color);
		if (! dc.getDomain().getAcc().equals(dc.getDomain().getID()) ) 
			doms2colors.put(dc.getDomain().getID(), color);
		visualChange();
	}	
	
	/**
	 * Sets a new color for a DomainFamily without triggering
	 * a repaint. This method can for instance be used during the
	 * project import process. Both the id and acc are set (unless
	 * they do not differ)
	 * 
	 * @param fam
	 * 		DomainFamily to be associated with the specified color.
	 * @param color
	 * 		the new color for the specified DomainFamily
	 */
	public void setDomainColor(DomainFamily fam, Color color) {
		doms2colors.put(fam.getAcc(), color);
		// make sure that if the id and acc are different, 
		// both a assoc. with the color
		if (! fam.getAcc().equals(fam.getID()) ) 
			doms2colors.put(fam.getID(), color);
	}	

}
