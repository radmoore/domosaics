package domosaics.ui.views.domainview.renderer.arrangement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.renderer.domain.DomainRenderer;




/**
 * The basic implementation of the ArrangementRenderer interface.
 * <p>
 * The abstract arrangement renderer can be used to render an 
 * ArrangementComponent based on a DomainArrangement backend data.
 * Therefore a subclass implementing the render method has to be 
 * defined, which draws for instance the arrangements backbone.
 * <p>
 * The rendering of domains is delegated to the domain renderer which
 * must be specified in the constructor of the extending subclass.
 *
 *
 * @author Andreas Held
 *
 */
public abstract class AbstractArrangementRenderer implements ArrangementRenderer {

	/** the used DomainRenderer to render domains */
	protected DomainRenderer domRenderer;
	
	/**
	 * Constructor using a specified DomainRenderer to render domains
	 * within the arrangement
	 * 
	 * @param domRenderer
	 * 		the DomainRenderer used to render domains
	 */
	public AbstractArrangementRenderer (DomainRenderer domRenderer) {
		this.domRenderer =  domRenderer;
	}
	
	/**
	 * @see ArrangementRenderer
	 */
	@Override
	public void setDomainRenderer (DomainRenderer renderer) {
		this.domRenderer = renderer;
	}
	
	/**
	 * @see ArrangementRenderer
	 */
	@Override
	public DomainRenderer getDomainRenderer() {
		return domRenderer;
	}
	
	/**
	 * Basically just checks if the arrangement actually has to be drawn
	 * by checking against the clipping area.
	 * Also restores the graphical context after the arrangement was rendered.
	 * and delegates the domain drawing to the domain renderer.
	 * 
	 * @see ArrangementRenderer
	 */
	@Override
	public void renderArrangement(ArrangementComponent dac, DomainViewI view, Graphics2D g2) {
		// if the arrangement doesn't intersect with the clipping area, don't render it
		if (!dac.getBoundingShape().intersects(g2.getClipBounds()))
			return;
		
		// if the domain arrangement component is not visible at the moment, don't render it either
		if (!dac.isVisible())
			return;
		
		// store old graphic context settings
		Color oldColor = g2.getColor();
		Paint oldPaint = g2.getPaint();
		
		// here the actual rendering of the arrangement is done, e.g. its backbone
		render(dac, view, g2);
		
		// restore graphic context settings
		g2.setPaint(oldPaint);
		g2.setColor(oldColor);

		// now that the arrangement is rendered, the domains are left
		for (DomainComponent dc : view.getArrangementComponentManager().getDomains(dac))
			domRenderer.renderDomain(dc, view, g2);
	}
	
	/**
	 * Method to draw the arrangement, e.g. its backbone.
	 * 
	 * @param dac
	 * 		the domain arrangement component to draw
	 * @param view
	 * 		the view containing the domain arrangement
	 * @param g2
	 * 		the graphics context
	 */
	public abstract void render(ArrangementComponent dac, DomainViewI view, Graphics2D g2);

}
