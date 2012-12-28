package angstd.ui.views.domainview.renderer.additional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Iterator;

import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.view.renderer.Renderer;



/**
 * HighlightDomainRenderer can be added as additional renderer to the 
 * DomainView and is responsible for highlighting domains.
 * 
 * @author Andreas Held
 *
 */
public class HighlightDomainRenderer implements Renderer {

	/** the view on which the highlighting is rendered */
	protected DomainViewI view;
	
	/**
	 * Basic constructor for this renderer.
	 * 
	 * @param view
	 * 		the view on which the highlighting is rendered
	 */
	public HighlightDomainRenderer (DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * Highlights clicked, mouse over and selected domains. Delegates to
	 * hightlightDomain().
	 */
	public void render(Graphics2D g2) {
		// no highlighting in msa view
		if(view.getDomainLayoutManager().isMsaView())
			return;
		
		// highlight the clicked component
		if (view.getDomainSelectionManager().getClickedComp() != null)
			hightlightDomain(view.getDomainSelectionManager().getClickedComp(), view, g2);
		
		// highlight the mouse over component
		if (view.getDomainSelectionManager().getMouseOverComp() != null) 
			hightlightDomain(view.getDomainSelectionManager().getMouseOverComp(), view, g2);
		
		// highlight the selected domains
		if (view.getDomainSelectionManager().getSelection() != null) {
			Iterator<DomainComponent> iter = view.getDomainSelectionManager().getSelection().iterator();
			while (iter.hasNext())
				hightlightDomain(iter.next(), view, g2);
		}
	}
	
	/**
	 * Highlights a domain component within a given view using a graphics 
	 * context.
	 * 
	 * @param view
	 * 		the view containing the domain component
	 * @param dc
	 * 		the domain component to highlight
	 * @param g2
	 * 		the graphics context used to render the domain component
	 */
	public void hightlightDomain(DomainComponent dc, DomainViewI view, Graphics2D g2) { 
		if (!dc.isVisible())
			return;
		
		Color oldColor = g2.getColor();
		Stroke oldStroke = g2.getStroke();
		
		if (view.getDomainSelectionManager().getSelection().contains(dc))
			g2.setColor(Color.red);
		else
			g2.setColor(Color.blue);
		
		g2.setStroke(new BasicStroke(2));
		
		// get shape 
		if (view.getDomainLayoutManager().isShowShapes()) {
			g2.draw(view.getDomainShapeManager().getDomainShape(dc)); 
		} else
			g2.draw(dc.getDisplayedShape()); 
		
		g2.setColor(oldColor);
		g2.setStroke(oldStroke);
	}
	
	
}
