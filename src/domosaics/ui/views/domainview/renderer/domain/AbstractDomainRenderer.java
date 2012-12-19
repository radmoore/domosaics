package domosaics.ui.views.domainview.renderer.domain;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.SwingUtilities;

import domosaics.ui.util.StringUIUtils;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;
import domosaics.ui.views.domainview.layout.DomainLayout;


/**
 * The basic implementation of the DomainRenderer interface.
 * All DomainRenderer should extend this abstract class. This
 * class ensures the correct handling of the graphical context and
 * reduces the number of rendering processes to those which are 
 * necessary.
 * <p>
 * Do render a domain this class tries to collect all attributes
 * which are needed to do so. The getMethods to those attributes must
 * be implemented by the subclasses and nothing more.
 * 
 * @author Andreas Held, Andrew Moore
 *
 */
public abstract class AbstractDomainRenderer implements DomainRenderer {
	
	/**
	 * @see DomainRenderer
	 */
	public void renderDomain(DomainComponent dc, DomainViewI view, Graphics2D g2) {
		// if the domain doesn't intersect with the clipping area, don't render it
		if (!dc.getBoundingShape().intersects(g2.getClipBounds()))
			return;

		// if the domain domain component is not visible at the moment, don't render it either
		if (!dc.isVisible())
			return;
		
		// draw the domain
		drawDomain(dc, view, g2);
		
		// draw the label only in proportional view and if not shapes is set
		if (view.getDomainLayoutManager().isUnproportionalView() || view.getDomainLayoutManager().isShowShapes()) 
			if (!view.isCompareDomainsMode())
				return;
		
		// draw the label
		drawLabel(dc, view, g2);	
	}
	
	/**
	 * Rendering process triggered for domains. This method gathers
	 * the attributes needed to draw the domain properly. To do so
	 * it calls abstract methods which must be implemented by sub classes.
	 * 
	 * @param dc
	 * 		the domain component to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @param g2
	 * 		the graphical context
	 */
	protected void drawDomain(DomainComponent dc, DomainViewI view, Graphics2D g2) {
		// store old graphic context settings
		Color oldColor = g2.getColor();
		Paint oldPaint= g2.getPaint();
		Stroke oldStroke = g2.getStroke();

		// get the setting for rendering the domain from a subclass
		g2.setColor(getColor(dc, view));
		g2.setPaint(getPaint(dc, view, g2));
		g2.setStroke(getStroke(dc, view));
		
		// get the domains shape
		Shape shape = getShape(dc, view);
		
		// and render the domain
		g2.fill(shape);
		g2.setPaint(oldPaint);
		if ( dc.getDomain().isPutative() )
			g2.setColor(Color.red);
			else 
				g2.setColor(Color.black);
		g2.draw(shape);
		
		// restore the settings then
		g2.setColor(oldColor);
		g2.setStroke(oldStroke);
		g2.setPaint(oldPaint);
	}
	
	/**
	 * Rendering process triggered for domains to render their label. 
	 * This method gathers the attributes needed to draw the 
	 * domain label properly. To do so it calls abstract methods 
	 * which must be implemented by sub classes.
	 * 
	 * @param dc
	 * 		the domain component which label has to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @param g2
	 * 		the graphical context
	 */
	protected void drawLabel(DomainComponent dc, DomainViewI view, Graphics2D g2) {
		// store graphic context
		Font oldFont = g2.getFont();
		Color color = g2.getColor();

		// get the label
		String label = getLabel(dc, view);
		
		// render it only if everything is ok
		if(label == null || label.length() == 0) 
			return;
		
		g2.setFont(DomainLayout.DOMAINFONT);

		// get the label color from the extending domain renderer
		g2.setColor(getLabelColor(dc, view));
		
		// Center the label and actually draw it
		int totalLabelWidth = dc.getWidth()-4; 	// 2 pixel at the beginning and end
		int neededLabelWidth = SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(DomainLayout.DOMAINFONT), label);
		
		int x2 = (int) (dc.getX() + (totalLabelWidth-neededLabelWidth) / 2.0);
		int y2 = (int) (dc.getY() + (g2.getFont().getSize2D() / 2.0));
		
		if (neededLabelWidth > totalLabelWidth) {
			label = StringUIUtils.clipStringIfNecessary(view.getViewComponent(), g2.getFontMetrics(), label, totalLabelWidth);
			if(label.equals("...")) 
				return;
			x2 = dc.getX()+1;
		}
		
		g2.drawString(label, x2+2, y2-1);
		
		// restore graphic context
		g2.setFont(oldFont);
		g2.setColor(color);
	}

	/* ************************************************************* *
	 * 			methods which must be implemented by a subclass      *
	 * ************************************************************* */
	
	/**
	 * Return the color in which the domain will be rendered
	 * 
	 * @param dc
	 * 		the domain component to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @return
	 * 		the color in which the domain will be rendered
	 */
	public abstract Color getColor(DomainComponent dc, DomainViewI view);
	
	/**
	 * Return the paint in which the domain will be rendered
	 * 
	 * @param dc
	 * 		the domain component to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @param g2
	 * 		the graphical context
	 * @return
	 * 		the paint in which the domain will be rendered
	 */
	public abstract Paint getPaint(DomainComponent dc, DomainViewI view, Graphics2D g2);
	
	/**
	 * Return the stroke used on the domain edge
	 * 
	 * @param dc
	 * 		the domain component to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @return
	 * 		the stroke used on the domain edge
	 */
	public abstract Stroke getStroke(DomainComponent dc, DomainViewI view);
	
	/**
	 * Return the stroke used on the domain edge
	 * 
	 * @param dc
	 * 		the domain component to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @return
	 * 		the stroke used on the domain edge
	 */
	//public abstract Stroke getPutativeStroke(DomainComponent dc, DomainViewI view);
	
	/**
	 * Return the domains shape
	 * 
	 * @param dc
	 * 		the domain component to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @return
	 * 		the domains shape
	 */
	public abstract Shape getShape(DomainComponent dc, DomainViewI view);
	
	/**
	 * Return the domains label
	 * 
	 * @param dc
	 * 		the domain component to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @return
	 * 		the domains label
	 */
	public abstract String getLabel(DomainComponent dc, DomainViewI view);
	
	/**
	 * Return the domains label color
	 * 
	 * @param dc
	 * 		the domain component to be rendered
	 * @param view
	 * 		the view containing the domain component
	 * @return
	 * 		the label color
	 */
	public abstract Color getLabelColor(DomainComponent dc, DomainViewI view);
	
}
