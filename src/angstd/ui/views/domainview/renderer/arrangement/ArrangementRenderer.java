package angstd.ui.views.domainview.renderer.arrangement;

import java.awt.Graphics2D;

import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.renderer.domain.DomainRenderer;

/**
 * The basic ArrangementRenderer interface which defines all needed 
 * methods to render domain arrangements.
 * <p>
 * In general the actually used renderer for arrangements can change
 * during the applications work flow. For instance when the user
 * changes to the MSAView.
 * <p>
 * Each arrangement renderer has a {@link DomeinRenderer} assigned to it
 * which is responsible for the rendering of the domains within an arrangement
 * and can be changed by using the setDomainRenderer method.
 * 
 * @author Andreas Held
 *
 */
public interface ArrangementRenderer {
	
	/**
	 * Renders the domain arrangement.
	 * 
	 * @param dac
	 * 		the arrangement component to be rendered
	 * @param view
	 * 		the view containing the component
	 * @param g2
	 * 		the graphics context
	 */
	public void renderArrangement(ArrangementComponent dac, DomainViewI view, Graphics2D g2);
	
	/**
	 * Sets the domain renderer used to render domains during the
	 * arrangement rendering process.
	 * 
	 * @param renderer
	 * 		the new domain renderer used to render domains
	 */
	public void setDomainRenderer (DomainRenderer renderer);
	
	/**
	 * Return the domain renderer used to render domains within the
	 * arrangement.
	 * 
	 * @return
	 * 		domain renderer used to render domains
	 */
	public DomainRenderer getDomainRenderer();
	
	
	
}
