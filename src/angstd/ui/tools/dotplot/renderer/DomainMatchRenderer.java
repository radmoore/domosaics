package angstd.ui.tools.dotplot.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;
import angstd.ui.tools.dotplot.DotplotView;
import angstd.ui.tools.dotplot.components.DotplotComponent;
import angstd.ui.views.view.renderer.Renderer;

/**
 * Additional renderer used to renderer the domain match boxes 
 * indicating how similiar representantss of the same domain family
 * are.
 * 
 * @author Andreas Held
 *
 */
public class DomainMatchRenderer implements Renderer {
    
	/** backend dotplot view */
    protected DotplotView view;

    /**
     * Constructor for a new DomainMatchRenderer
     * 
     * @param view
     * 		the backend view in where the similarity boxes should be rendered
     */
    public DomainMatchRenderer(DotplotView view) {
    	this.view = view;
    }

    /**
     * @see Renderer
     */
	public void render(Graphics2D g) {
		if (!view.getDotplotLayoutManager().isShowDomainMatches())
			return;

		Color oldColor = g.getColor();
		
		// make a reference to the needed components
		DomainArrangement dac1 = view.getDa1().getDomainArrangement();
		DomainArrangement dac2 = view.getDa2().getDomainArrangement();
		DotplotComponent dotplot = view.getDotplotComponent();
		
		// iterate over the domains of the horizontal arrangement...
		for (int i = 0; i < dac1.countDoms(); i++) {
			Domain hDom = dac1.getDomain(i);
			
			// ... and compare them to the domains of the vertical arrangement
			for (int j = 0; j < dac2.countDoms(); j++) {
				Domain vDom = dac2.getDomain(j);
				
				// draw a match if the families are equally
				if (vDom.getFamily().equals(hDom.getFamily())) {
					Color domColor = view.getDomainColorManager().getDomainColor(vDom);
					
					// calculate the alpha value depending on the percentage similarity
					int alphaPercent = view.getMatchScores().getPercent(hDom, vDom);
					int alpha = (int) Math.floor(((double) alphaPercent / (double) 100) * (double) 240);
					
					domColor = new Color(domColor.getRed(), domColor.getGreen(), domColor.getBlue(), alpha); //100);
					g.setColor(domColor);
					
					// draw the box
					Rectangle matchBox = new Rectangle(
							dotplot.getX()+ hDom.getFrom(), 
							dotplot.getY()+ vDom.getFrom(),
							hDom.getLen(), 
							vDom.getLen());
					g.fill(matchBox);
					
					// and the similarity label in it
					g.setColor(Color.BLACK);
					int labelX = (int) matchBox.getCenterX()-10; // TODO middle
					int labelY = (int) matchBox.getCenterY()-10;;
					g.drawString(""+alphaPercent+" %", labelX, labelY);
				}
			}
		}
		g.setColor(oldColor);
	}
    
 

}
