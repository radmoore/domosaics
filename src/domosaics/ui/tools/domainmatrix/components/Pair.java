package domosaics.ui.tools.domainmatrix.components;

import domosaics.model.DoMosaicsData;
import domosaics.ui.views.domainview.components.DomainComponent;

/**
 * MatrixEntrys can be labels (would be the case for the
 * header fields) or domain components (all other). 
 * Therefore each matrix entry can be represented by a {@link Pair} of 
 * the label and domain component where one of them is null, e.g. if
 * the domain component is null, it must be a header label. Sounds like
 * a dirty job to me. =)
 * 
 * @author Andreas Held
 *
 */
public class Pair implements DoMosaicsData{

	/** the domain component */
	protected DomainComponent dom;
	
	/** the header label */
	protected String label; 
	
	/**
	 * Constructor for a new Pair where one of the parameters 
	 * should be null
	 * 
	 * @param dom
	 * 		the domain component
	 * @param label
	 * 		or the header label
	 */
	public Pair(DomainComponent dom, String label) {
		this.dom = dom;
		this.label = label;
	}
}
