package angstd.ui.views.domainview.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.layout.DomainLayout;
import angstd.ui.views.domainview.renderer.arrangement.ArrangementRenderer;
import angstd.ui.views.domainview.renderer.arrangement.BackBoneArrangementRenderer;

/**
 * The DefaultDomainViewRenderer provides all methods necessary to
 * render a DomainView.
 * <p>
 * The rendering of arrangements is delegated to an {@link ArrangementRenderer}.
 * The arrangement renderer can be changed during the applications
 * work flow. For instance if the rendering mode switches to MSA view
 * this renderer has to be changed.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDomainViewRenderer implements DomainViewRenderer{

	/** the view to render */
	protected DomainViewI view;
	
	/** the arrangement renderer used to render the view */
	protected ArrangementRenderer daRenderer;

	
	/**
	 * Basic constructor initializing the view renderer with the
	 * DefaultArrangementRenderer.
	 * 
	 * @param view
	 * 		the view to render
	 */
	public DefaultDomainViewRenderer (DomainViewI view) {
		this (view, new BackBoneArrangementRenderer());
	}
	
	
	/**
	 * Constructor which allows the explicit setting of the 
	 * ArrangementRenderer which should be used.
	 * 
	 * @param view
	 * 		the view to render
	 * 
	 * @param daRenderer
	 * 		the ArrangementRenderer used to render the arrangements
	 */
	public DefaultDomainViewRenderer (DomainViewI view, ArrangementRenderer daRenderer) {
		this.view = view;
		this.daRenderer = daRenderer;
	}
	
	/**
	 * Set the ArrangementRenderer which should be used to render 
	 * views arrangement components.
	 * 
	 * @param daRenderer
	 * 		ArrangementRenderer to render arrangements
	 */
	public void setArrangementRenderer(ArrangementRenderer daRenderer) {
		this.daRenderer = daRenderer;
	}
	
	/**
	 * Returns the ArrangementRenderer which is used to render 
	 * views arrangement components.
	 * 
	 * @return
	 * 		ArrangementRenderer to render arrangements
	 */
	public ArrangementRenderer getArrangementRenderer() {
		return daRenderer;
	}
	
	/* ******************************************************************* *
	 *   						 Renderering methods					   *
	 * ******************************************************************* */
	
	/**
	 * The rendering process is started here. The clipping area is set in here,
	 * the background and arrangements rendered.
	 */
	public void render(Graphics2D g) {
		// get clip bounds or visible rectangle
		Rectangle clip = g.getClipBounds();
		if (clip == null) {
			clip = view.getViewComponent().getVisibleRect();
			g.setClip(clip);
		}
		
		// render the view
		renderBackground(g, clip);
		renderArrangements(g);
	}

	/**
	 * Helper method rendering the views background
	 * 
	 * @param g
	 * 		graphics context
	 * @param r
	 * 		the rectangle (e.g. clip area) to render
	 */
	private void renderBackground(Graphics2D g, Rectangle r) {
		g.setColor(Color.white);
		g.fill(r);
	}

	/**
	 * Helper method which delegates the rendering to the 
	 * arrangement renderer being set.
	 * 
	 * @param g
	 * 		graphics context
	 */
	private void renderArrangements(Graphics2D g) {
		// iterate over the DAs and render each one
		if (view.getDaSet() != null) {
			// using the for loop instead of an iterator fixes the annoying color bug, now all domains are always colored the same way
			for (int i = 0; i < view.getDaSet().length; i++) {
				ArrangementComponent dac = view.getArrangementComponentManager().getComponent(view.getDaSet()[i]);
			
				if (!dac.isVisible())
					continue;
				
				// if arrangement is below clipping area break
				if (dac.getBounds().getY()-dac.getBounds().getHeight() > g.getClipBounds().y+g.getClipBounds().height)
					break;

				boolean state = dac.renderWithID();
				if (state) 
					renderLabel(dac, g);
				daRenderer.renderArrangement(dac, view, g);
			}
		}
	}
	
	/**
	 * Helper method to render the arrangements label
	 * 
	 * @param dac
	 * 		the arrangement which label has to be rendered
	 * @param g
	 * 		graphics context
	 */
	private void renderLabel(ArrangementComponent dac, Graphics2D g) {
		Color oldColor = g.getColor();
		Font oldFont = g.getFont();
	
		int y = (int) (dac.getY() + (g.getFont().getSize2D() / 2.0));
		g.setColor(Color.black);
		g.setFont(DomainLayout.ARRANGEMENTFONT);
		g.drawString(dac.getLabel(), 12, y-1);
		
		g.setColor(oldColor);
		g.setFont(oldFont);
	}
	
}
