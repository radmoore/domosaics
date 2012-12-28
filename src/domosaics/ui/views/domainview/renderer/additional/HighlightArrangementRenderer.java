package domosaics.ui.views.domainview.renderer.additional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Iterator;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * HighlightArrangementRenderer can be added as additional renderer to 
 * the DomainView and is responsible for highlighting arangements.
 * 
 * @author Andreas Held
 *
 */
public class HighlightArrangementRenderer implements Renderer {

	/** the view on which the highlighting is rendered */
	protected DomainViewI view;
	
	/**
	 * Basic constructor for this renderer.
	 * 
	 * @param view
	 * 		the view on which the highlighting is rendered
	 */
	public HighlightArrangementRenderer (DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * Highlights mouse over and selected arrangements. Delegates to 
	 * hightlightArrangement().
	 */
	public void render(Graphics2D g2) {
		// do not highlight in MSA view
		if(view.getDomainLayoutManager().isMsaView())
			return;
		
		// highlight the mouse over component
		if (!view.getDomainLayoutManager().isUnproportionalView() &&
		     view.getArrangementSelectionManager().getMouseOverComp() != null) 
			hightlightArrangement(view, view.getArrangementSelectionManager().getMouseOverComp(), g2);
		
		// highlight the selected arrangements
		if (view.getArrangementSelectionManager().getSelection() != null) {
			Iterator<ArrangementComponent> iter = view.getArrangementSelectionManager().getSelection().iterator();
			while (iter.hasNext())
				hightlightArrangement(view, iter.next(), g2);
		}
	}
	

	/**
	 * This methods highlights a domain arrangement by just drawing a 
	 * border around it. The borders color is red if the component 
	 * was added to a  selection and blue for instance if its just on 
	 * mouse over.
	 * 
	 * @param view
	 * 		the view containing the domain arrangement
	 * @param dac
	 * 		the domain arrangement to be highlighted
	 * @param g2
	 * 		the graphical context
	 */
	public void hightlightArrangement(DomainViewI view, ArrangementComponent dac, Graphics2D g2) {
		Color oldColor = g2.getColor();
		Stroke oldStroke = g2.getStroke();
		
		if (!dac.isVisible())
			return;
		
		if (view.getArrangementSelectionManager().getSelection().contains(dac))
			g2.setColor(Color.red);
		else
			g2.setColor(Color.blue);
		
		g2.setStroke(new BasicStroke(2));
		g2.draw(dac.getDisplayedShape()); 
		
		g2.setColor(oldColor);
		g2.setStroke(oldStroke);
	}
	

}
