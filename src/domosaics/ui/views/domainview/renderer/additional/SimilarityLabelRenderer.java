package domosaics.ui.views.domainview.renderer.additional;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domosaics.algos.distance.DistanceMeasureType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.layout.DomainLayout;
import domosaics.ui.views.domainview.manager.DomainSimilarityManager;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * 
 * Renders the percentage of similarity behind each arrangement compared
 * to a reference arrangement (the actually selected one). 
 * 
 * @author Andreas Held
 *
 */
public class SimilarityLabelRenderer implements Renderer {

	/** the domain view showing the similarity percentages */
	protected DomainViewI view;
	
	/**
	 * Constructor for a new SimilarityLabelRenderer
	 * 
	 * @param view
	 * 		view showing the similarity percentages
	 */
	public SimilarityLabelRenderer(DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * Renders the similarity percentages behind arrangements.
	 * The renderer tries to determine the best column to draw all
	 * numbers.
	 */
	public void render(Graphics2D g) {
		// don't draw if no arrangements were collapsed
		if (!view.getDomainLayoutManager().isCollapseBySimilarity())
			return;
		
		if(view.getDomainLayoutManager().isMsaView())
			return;
		
		// get a list of all arrangements within the clip height
		Rectangle clip = g.getClip().getBounds();
		List<ArrangementComponent> toDraw = new ArrayList<ArrangementComponent>();
		
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();
			
			if (!dac.isVisible())
				continue;
			
			if (dac.getY() > clip.y && dac.getY() < clip.y+clip.height)
				toDraw.add(dac);
		}
		
		// if this list is empty return
		if (toDraw.isEmpty())
			return;
		
		// now determine the best location to draw the numbers within a column
		int colX = 0;
		for (ArrangementComponent dac : toDraw) {
			if (dac.getX()+dac.getWidth() + 30 > clip.x + clip.width) {
				colX = clip.x + clip.width - 30;
				break;
			}

			// get the place directly behind the arrangement and take the maximum for all visible arrangements
			int x = (int) (dac.getBounds().getX()+ dac.getBounds().getWidth() + 10);
			colX = (colX < x) ? x : colX;
		}
		
		// if the colums x coordinate is left to the clip are set it to the clip areas beginning so that the user can see it
		if (colX < clip.x)
			colX = clip.x;
		
		// finally draw the numbers
		DomainSimilarityManager manager = view.getDomainSimilarityManager();
		
		Font oldFont = g.getFont();
		Color oldColor = g.getColor();
		g.setColor(Color.black);

		for (ArrangementComponent dac : toDraw) {
			if (dac.getX()+dac.getWidth() + 30 > clip.x + clip.width) 
				continue;
			
			int height =  DomainLayout.MIN_DA_HEIGHT-2;
			int y = (int) (dac.getBounds().getY()) + height/4;
			
			g.setFont(new Font("Arial", Font.BOLD, height));
			
			// draw percent not in edit distance
			if (view.getDomainSimilarityManager().getMeasureType() == DistanceMeasureType.DOMAINDISTANCE) 
				g.drawString(""+manager.getSimilarity(dac), colX, y);
			else
				g.drawString(""+manager.getSimilarity(dac)+"%", colX, y);
		}
		
		g.setFont(oldFont);
		g.setColor(oldColor);
	}

}
