package domosaics.ui.views.domainview.renderer.additional;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.Iterator;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.mousecontroller.SequenceSelectionMouseController;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * The sequence selection renderer renders the selection rectangle
 * as well as the already selected underlying sequences of
 * domain arrangements. 
 * <p>
 * The rendering process only in sequence selection mode triggered.
 * The renderer gets its information from the {@link SequenceSelectionMouseController}.
 * 
 * @author Andreas Held
 *
 */
public class SequenceSelectionRenderer implements Renderer {

	/** the view on which the selection events take place */
	protected DomainViewI view;
	
	/**
	 * Constructor for a new sequence selection renderer.
	 *  
	 * @param view
	 * 		the view on which the selection events take place
	 */
	public SequenceSelectionRenderer(DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * @see Renderer
	 */
	@Override
	public void render(Graphics2D g) {
		// not active in zoom mode
		if (view.isZoomMode())
			return;
		
		// not active if not in sequence selection mode
		if (!view.getDomainLayoutManager().isSelectSequences())
			return;
		
		Color oldColor = g.getColor();
		
		// draw the selected area
		Area selectionArea = view.getSequenceSelectionMouseController().getSelectionArea();
		if (selectionArea!= null) {
			g.setColor(new Color(255, 0 , 0, 50));
			g.fill(selectionArea);
		}

		// draw the selection rectangle
		if (view.getSequenceSelectionMouseController().getSelectionRectangle() == null)
			return;
		
		g.setColor(Color.red);
		Rectangle r = view.getSequenceSelectionMouseController().getSelectionRectangle();
		g.drawRect(r.x, r.y, r.width, r.height);
		
		// mark intersecting regions 
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();

			if (!dac.isVisible())
				continue;
			
			// check if rectangle intersects with the arrangement
			if (dac.getDisplayedShape().intersects(r)) {
				g.setColor(new Color(255, 0 , 0, 50));
			    g.fill(r.intersection(dac.getDisplayedShape().getBounds()));
			}
			
		}	
		
		g.setColor(oldColor);
	}
}
