package domosaics.ui.tools.domainmatrix.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.SwingUtilities;

import domosaics.ui.tools.domainmatrix.DomainMatrixView;
import domosaics.ui.util.StringUIUtils;
import domosaics.ui.views.domainview.components.shapes.DoMosaicsPolygon;
import domosaics.ui.views.view.renderer.Renderer;


/**
 * The DefaultDomainMatrixRenderer is used to render the domain matrix.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDomainMatrixRenderer implements Renderer{

	/** the domain matrix view to be rendered */
	protected DomainMatrixView view;
	
	/**
	 * Constructor for a new domain matrix renderer.
	 * 
	 * @param view
	 * 		the domain matrix view to be rendered
	 */
	public DefaultDomainMatrixRenderer(DomainMatrixView view) {
		this.view = view;
	}
	
	/**
	 * Renders the background of the legend view
	 * 
	 * @param g
	 * 		the actual graphics context
	 * @param r
	 * 		the clip area for the view
	 */
	private void renderBackground(Graphics2D g, Rectangle r) {
		g.setColor(Color.white);
		g.fill(r);
	}
	
	/**
	 * Draws the background and then iterates over all
	 * domain components which are registered for the legend view
	 */
	public void render(Graphics2D g2) {		
		Rectangle r = g2.getClipBounds();
		if (r == null) 
			r = view.getViewComponent().getVisibleRect();
		
		renderBackground(g2, r);
		
		// render the matrix entrys
		DomainMatrixEntry[][] data = view.getData();
		int cols = data[0].length;
		int rows = data.length;
		
		
		// determine start and end row within the clipping area and do the same for the columns.
		int 	startrow = 0, 			endrow = 0, 			startcol = 0, 			endcol = 0;
		boolean startrowFound = false, 	endrowFound = false, 	startcolFound = false, 	endcolFound = false;

		for (int row = 0; row < rows; row++) {
			if (startrowFound && endrowFound && startcolFound && endcolFound)
				break;
			
			for (int col = 0; col < cols; col++) {
				if (!startrowFound && (data [row][col].getY() + data [row][col].getHeight()> r.y)) {
					startrow = row;
					startrowFound = true;
				}

				if (!endrowFound && ((data [row][col].getY() + data [row][col].getHeight()> r.y+r.height) || row == rows-1)) {
					endrow = row;
					endrowFound = true;
				}
				
				if (!startcolFound && (data [row][col].getX() + data [row][col].getWidth()> r.x)) {
					startcol = col;
					startcolFound = true;
				}

				if (!endcolFound && ((data [row][col].getX() + data [row][col].getWidth()> r.x+r.width) || col == cols-1)) {
					endcol = col;
					endcolFound = true;
				}
			}
		}
		
		// draw grid
		g2.setColor(Color.black);
		for (int row = startrow+1; row < endrow+1; row++){
			int x1 = data [row][0].getX();
			int y1 = data [row][0].getY(); 
			int x2 = data [row][cols-1].getX()+data [row][cols-1].getWidth();
			g2.drawLine(x1, y1, x2, y1);
		}
			
		for (int col = startcol+1; col < endcol+1; col++) {
			if (data [0][col].getX() < r.x)
				continue;
			if (data [0][col].getX() > r.x+r.width)
				break;
			
			int x1 = data [0][col].getX(); 
			int y1 = data [0][col].getY();
			int y2 = data [rows-1][col].getY()+data [rows-1][col].getHeight();
			g2.drawLine(x1, y1, x1, y2);
		}
		
		// draw data values
		for (int row = startrow; row < endrow+1; row++)
			for (int col = startcol; col < endcol+1; col++) 
				renderMatrixEntry(data[row][col], view, g2);

		g2.dispose();
	}
	
	/**
	 * Renders a domain within the table
	 * 
	 * @param entry
	 * 		the matrix entry to be rendered
	 * @param view
	 * 		the matrix view being rendered
	 * @param g2
	 * 		the actual graphics context
	 */
	public void renderMatrixEntry(DomainMatrixEntry entry, DomainMatrixView view, Graphics2D g2){
		Color oldC = g2.getColor();
		Paint oldPaint = g2.getPaint();

		g2.setColor(Color.black);	
		g2.setFont(new Font("Arial", 0, DefaultDomainMatrixLayout.FONTSIZE));
		
		if (entry.getDomainComponent() != null) {
			renderDomainComponent(entry, view, g2);
			g2.setColor(oldC);
			g2.setPaint(oldPaint);
			return;
		}
		
		if (entry.getLabel() == null)
			return;
		
		String label = entry.getLabel();
		if (entry.getLabel().length() > 4) {
			
			int maxWidth = entry.getWidth();
			int neededWidth = getStringWidth(view, label);
			
			if (neededWidth > maxWidth)
				label = StringUIUtils.clipStringIfNecessary(view.getViewComponent(), g2.getFontMetrics(), label, maxWidth);
		} 
		
		int centeredX = entry.getX() + (entry.getWidth() - getStringWidth(view, label)) / 2;
		int centeredY = entry.getY() + (entry.getHeight() - DefaultDomainMatrixLayout.FONTSIZE) / 2;
		
		g2.drawString(label, centeredX, centeredY+DefaultDomainMatrixLayout.FONTSIZE); 

		g2.setColor(oldC);
		g2.setPaint(oldPaint);
	}
	
	/**
	 * Render the domain
	 * 
	 * @param dme
	 * 		the matrix entry being rendered
	 * @param view
	 * 		within the matrix view
	 * @param g2
	 * 		using the actual graphics context
	 */
	public void renderDomainComponent(DomainMatrixEntry dme, DomainMatrixView view, Graphics2D g2) {
		Color oldC = g2.getColor();
		Paint oldPaint = g2.getPaint();
	
		int centeredX = dme.getX() + (dme.getWidth() - DefaultDomainMatrixLayout.PROPSIZEWIDTH) / 2; // TODO
		int centeredY = dme.getY() + (dme.getHeight() - DefaultDomainMatrixLayout.FONTSIZE) / 2;
		
		int x = centeredX;
		int y = centeredY; 
		int width = DefaultDomainMatrixLayout.PROPSIZEWIDTH;	
		int height = DefaultDomainMatrixLayout.PROPSIZEHEIGHT; 
		
		Color color = view.getDomainView().getDomainColorManager().getDomainColor(dme.getDomainComponent());
		Shape shape = view.getDomainView().getDomainShapeManager().getUnsetShape(dme.getDomainComponent());

		
		if (shape instanceof RoundRectangle2D.Double) 
			((RoundRectangle2D.Double) shape).setRoundRect(x, y, width, height, 20, 20); 
		else if (shape instanceof DoMosaicsPolygon)
			((DoMosaicsPolygon) shape).setPolygon(x, y, width, height); 
		
		g2.setColor(color);
		float fac = 0.5f;
		Color fadeColor = new Color ((int) (color.getRed()*fac), (int) (color.getGreen()*fac), (int) (color.getBlue()*fac));
		g2.setPaint(new GradientPaint(x+width/2, y, color, x+width/2, y+height, fadeColor, false));
		
		g2.fill(shape);
		g2.setColor(Color.black);
		g2.draw(shape);
	
		g2.setColor(oldC);
		g2.setPaint(oldPaint);
	}
	
	/**
	 * Helper method to determine the width of a rendered string
	 * 
	 * @param view
	 * 		the view to be rendered
	 * @param label
	 * 		the label to be rendered
	 * @return
	 * 		the width needed to render the label
	 */
	public int getStringWidth(DomainMatrixView view, String label) {
		Font f = new Font("Arial", 0, DefaultDomainMatrixLayout.FONTSIZE);
		return SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(f), label);
	}
}
