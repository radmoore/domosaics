package domosaics.ui.views.domainview.renderer.domain;

import java.awt.Graphics2D;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.renderer.arrangement.ArrangementRenderer;




/**
 * The basic DomainRenderer interface which defines all needed 
 * methods to render domains.
 * <p>
 * A domain renderer is called by an arrangement renderer when
 * the rendering method is triggered. For more details on this see
 * {@link ArrangementRenderer}.
 * 
 * 
 * @author Andreas Held
 *
 */
public interface DomainRenderer {

	/**
	 * Renders a domain component within a given view using a graphics 
	 * context.
	 * 
	 * @param dc
	 * 		the domain component to render
	 * @param view
	 * 		the view containing the domain component
	 * @param g2
	 * 		the graphics context used to render the domain component
	 */
	public void renderDomain(DomainComponent dc, DomainViewI view, Graphics2D g2);
	
}
