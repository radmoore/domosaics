package domosaics.ui.tools.domainlegend.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.util.Iterator;

import domosaics.ui.tools.domainlegend.DomainLegendView;
import domosaics.ui.views.domainview.components.shapes.DoMosaicsPolygon;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * The DomainLegendRenderer is used to render the domain legend by
 * iterating over all registered domains.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDomainLegendRenderer implements Renderer {

	/** the domain legend view to be rendered */
	protected DomainLegendView view;

	/**
	 * Constructor for a new domain legend renderer.
	 * 
	 * @param view
	 * 		the domain legend view to be rendered
	 */
	public DefaultDomainLegendRenderer(DomainLegendView view) {
		this.view = view;
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
		
		// render the legend items
		Iterator<LegendComponent> iter = view.getLegendComponentManager().getComponentsIterator();
		while (iter.hasNext()) 
			renderLegendComponent(iter.next(), view, g2);

		g2.dispose();
	}
	
	/**
	 * Renders the background of the legend view
	 * 
	 * @param g
	 * 		the actual graphics context
	 * @param r
	 * 		the clip area for the view
	 */
	protected void renderBackground(Graphics2D g, Rectangle r) {
		g.setColor(Color.white);
		g.fill(r);
	}
	
	/**
	 * Renders a domain within the legend view
	 * 
	 * @param lc
	 * 		the domain component within the legend view
	 * @param view
	 * 		the domain view on which the legend is based
	 * @param g2
	 * 		the actual graphics context
	 */
	protected void renderLegendComponent(LegendComponent lc, DomainLegendView view, Graphics2D g2) {
		Color oldC = g2.getColor();
		Paint oldPaint = g2.getPaint();
	
		int x = lc.getX();
		int y = lc.getY();
		int width = DefaultDomainLegendLayout.PROPSIZEWIDTH;
		int height = lc.getHeight();
		
		Color color = view.getDomainColorManager().getDomainColor(lc.getDomainComponent());
		Shape shape; 
		
		if (view.getDomainLayoutManager().isShowShapes()) 
			shape = view.getDomainShapeManager().getUnsetShape(lc.getDomainComponent());
		else
			shape = new RoundRectangle2D.Double();
		
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
		
		g2.setFont(new Font("Arial", 1, 14));
	
		g2.drawString(lc.getLabel(), lc.getX()+DefaultDomainLegendLayout.PROPSIZEWIDTH+4, lc.getY()+lc.getHeight()-4);

		g2.setColor(oldC);
		g2.setPaint(oldPaint);
	}
	

	
	

}
