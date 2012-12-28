package domosaics.ui.views.domaintreeview.renderer.additional;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.manager.CSAInSubtreeManager;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.layout.DomainLayout;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.view.renderer.Renderer;


/**
 * 
 * Renders a number behind each arrangement, if the view is in 
 * CollapseSameArrangementsMode. This number represents the number
 * of same domain combinations which were collapsed during the collapse
 * process.
 * 
 * @author Andreas Held
 *
 */
public class CollapseNumberRenderer implements Renderer {

	/** the domain tree view showing the collapse count numbers */
	protected DomainTreeViewI view;
	
	
	/**
	 * Constructor for a new CollapseNumberRenderer
	 * 
	 * @param view
	 * 		view showing the collapse count numbers
	 */
	public CollapseNumberRenderer(DomainTreeViewI view) {
		this.view = view;
	}
	
	/**
	 * Renders the collapse count numbers behind arrangements.
	 * The renderer tries to determine the best column to draw all
	 * numbers. <br>
	 * If you want to change this, don't even try to understand this, 
	 * just write it new. =)
	 */
	public void render(Graphics2D g) {
		if (!view.getCSAInSubtreeManager().isActive())
			return;
		
		if(view.getDomainLayoutManager().isMsaView())
			return;
		
		CSAInSubtreeManager csaManager = view.getCSAInSubtreeManager();
		List<NodeComponent> csaNodes =  csaManager.getCSAnodes();
		
		for (int j = 0; j < csaNodes.size(); j++) {
			NodeComponent nc = csaNodes.get(j);
			if (!nc.isVisible())
				continue;
			
			// get a list of all arrangements within the clip height
			List<ArrangementComponent> toDraw = csaManager.getArrangements(csaNodes.get(j));
			Rectangle clip = g.getClip().getBounds();

			// determine the best location to draw the numbers within a column
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
			Font oldFont = g.getFont();
			Color oldColor = g.getColor();
			g.setColor(Color.black);

			for (ArrangementComponent dac : toDraw) {
				if (dac.getX()+dac.getWidth() + 30 > clip.x + clip.width) 
					continue;
				
				int height = DomainLayout.MIN_DA_HEIGHT-2;
				int y = (int) (dac.getBounds().getY()) + height/4;
				
				g.setFont(new Font("Arial", Font.BOLD, height));
				g.drawString(""+csaManager.getRedundancyCount(nc, dac)+"x", colX, y);
			}
			
			g.setFont(oldFont);
			g.setColor(oldColor);
		}
	}

}
