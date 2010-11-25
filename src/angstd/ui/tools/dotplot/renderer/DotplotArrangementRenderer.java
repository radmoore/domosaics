package angstd.ui.tools.dotplot.renderer;

import java.awt.Color;
import java.awt.Graphics2D;

import angstd.ui.tools.dotplot.DotplotView;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.domainview.renderer.domain.DefaultDomainRenderer;
import angstd.ui.views.domainview.renderer.domain.DomainRenderer;

/**
 * Renderer used to draw the arrangement on top and aside of the dotplot.
 * By using the Dotplotview there is no inference between the managers
 * of the backend view and the dotplotview itself.
 * 
 * @author Andreas Held
 *
 */
public class DotplotArrangementRenderer {

	/** the used DomainRenderer to render domains */
	protected DomainRenderer domRenderer;
	
	
	/**
	 * Constructor using a specified DomainRenderer to render domains
	 * within the arrangement
	 * 
	 * @param domRenderer
	 * 		the DomainRenderer used to render domains
	 */
	public DotplotArrangementRenderer() {
		this.domRenderer = new DefaultDomainRenderer();
	}
	
	/**
	 * Draws the backbone and delegates the domain drawing to the 
	 * domain renderer.
	 * 
	 * @param dac
	 * 		the arrangement component to be rendered
	 * @param view
	 * 		the dotplot view owning the component
	 * @param g2
	 * 		the graphical context
	 */
	public void renderArrangement(ArrangementComponent dac, DotplotView view, Graphics2D g2) {
		// draw the gray backbone
		int x = dac.getX();
		int y = (int) (dac.getY() - (dac.getHeight() / 8.0));
 
		g2.setColor(Color.lightGray);
		g2.fillRect(x, y, dac.getWidth(), dac.getHeight()/4);
		g2.setColor(Color.black);
		g2.drawRect(x, y, dac.getWidth(), dac.getHeight()/4);
		
		// delegate domain drawing to the domainRenderer, render it only if its within the clipping area
		for (DomainComponent dc : view.getDomainComponentManager().getDomains(dac)) 
			domRenderer.renderDomain(dc, view.getDomView(), g2);
	}
	
}
