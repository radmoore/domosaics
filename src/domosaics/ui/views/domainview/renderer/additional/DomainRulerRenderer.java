package domosaics.ui.views.domainview.renderer.additional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.SwingUtilities;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * Additional renderer which can be assigned to the DomainView.
 * This renderer renders a ruler on bottom of the arrangements
 * showing the arrangement range in amino acids.
 * <p>
 * The number of ticks is calculated by: <br>
 * max(4, maximal amino acids / 100)
 * 
 * @author Andreas Held
 *
 */
public class DomainRulerRenderer implements Renderer {

	/** font used to draw the tick labels */
	protected final Font tickFont = new Font("Arial", 0, 12);
	
	/** the view containing the amino acid ruler */
	protected DomainViewI view;

	
	/**
	 * Basic constructor for this renderer.
	 * 
	 * @param view
	 * 		the view on which the ruler should be rendered
	 */
	public DomainRulerRenderer (DomainViewI view) {
		this.view = view;
	}

	/**
	 * Renders the ruler on bottom of the view.
	 */
	public void render(Graphics2D g2) {
		// check if the option to render the ruler is enabled
		if (!view.getDomainLayoutManager().isShowLineal())
			return;
		
		// check if there are visible arrangements
		if (view.getDomainLayout().getDomainBounds().width == 0) 
			return;

		// save the graphic context attributes
		Stroke oldStroke = g2.getStroke();
		Color oldColor = g2.getColor();
		Font oldFont = g2.getFont();
		
		// create tick labels based on the maximal length
		int maxLen = view.getDomainLayout().getMaxLen();
		int rulerLength = maxLen;

		if (view.getDomainLayoutManager().isMsaView()) {
			int charwidth = SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(new Font("Courier", 0, 14)), "-");	
			maxLen = maxLen / charwidth;
		}

		// init the location where the ruler starts
		int x = view.getDomainLayout().getDomainBounds().x;
		int y = view.getViewComponent().getSize().height -15;
		
		int numTicks = Math.max(4, maxLen/100);
		if (view.getDomainLayoutManager().isFitDomainsToScreen() && rulerLength > view.getDomainLayout().getDomainBounds().width) {
			rulerLength = view.getDomainLayout().getDomainBounds().width;
			numTicks = 4;
		}
			
		// create the tick labels
		String[] tickLabels = new String[numTicks+1];
		for (int i=0; i <= numTicks; i++) 
			tickLabels[i] = ""+(int) (i*maxLen/numTicks);
		
		
		
		// draw the ruler line
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(1f));
		g2.drawLine(x, y, x + rulerLength, y);
		g2.setFont(tickFont);

		// draw the ticks
		for(int i=0; i<= numTicks; i++){
			int tx = x + i*(rulerLength/numTicks); 
			if (i == numTicks)
				tx = x + rulerLength;
			
			int ty = y;
			
			g2.drawLine(tx, ty, tx, ty+10);
			
			// draw the label
			if (i == numTicks) 
				tx -= (getStringWidth(g2, tickLabels[i])+2);
			g2.drawString(tickLabels[i],(int) (tx+2), (int)(ty+10+3));
		}
		
		g2.setStroke(oldStroke);
		g2.setColor(oldColor);
		g2.setFont(oldFont);
	}
	
	/**
	 * Helper method to calculate the strings width using the 
	 * specified graphics context.
	 * 
	 * @param g2
	 * 		the graphics context being used to draw the string
	 * @param label
	 * 		the label which used width should be determined
	 * @return
	 * 		the used width for the specified label ehen using the specified graphics context
	 */
	private int getStringWidth(Graphics2D g2, String label) {
		return SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(tickFont), label);
	}

}
