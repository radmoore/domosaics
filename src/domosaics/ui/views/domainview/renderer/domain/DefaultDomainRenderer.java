package domosaics.ui.views.domainview.renderer.domain;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import domosaics.ui.util.ColorUtil;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.DomainComponent;


/**
 * Default domain renderer which draws domains with their normal shapes
 * and labels.
 * 
 * @author Andreas Held, Andrew Moore
 *
 */
public class DefaultDomainRenderer extends AbstractDomainRenderer{

	/**
	 * @see AbstractDomainRenderer
	 */
	public Color getColor(DomainComponent dc, DomainViewI view) {
		
		int alpha = 255; 
		
		// make overlaps transparent
		if (dc.getDomain().getArrangement().hasOverlap(dc.getDomain())) 
			alpha = 150;
		
		// check if dom is putative, and draw white if so
		//if (dc.getDomain().isPutative()) {
		//	return ColorUtil.createAlphaColor(Color.white, alpha);
		//}
		
		Color color = view.getDomainColorManager().getDomainColor(dc);
		
		if (view.getDomainLayoutManager().isEvalueColorization())
			return ColorUtil.createEvalueColor(color.darker(), dc.getDomain().getEvalue());

		return ColorUtil.createAlphaColor(color, alpha);

	}

	/**
	 * @see AbstractDomainRenderer
	 */
	public Paint getPaint(DomainComponent dc, DomainViewI view, Graphics2D g2) {
		if (view.getDomainLayoutManager().isEvalueColorization())
			return g2.getPaint();
		
		Color color = g2.getColor();
		Color fadeColor = color.darker();
		
		int x = dc.getX()+dc.getWidth()/2;
		int y1 = dc.getTopLeft(); 
		int y2 = y1 + dc.getHeight();
		
		return new GradientPaint(x , y1, color, x, y2, fadeColor, false);
	}
	
	/**
	 * @see AbstractDomainRenderer
	 */
	public Stroke getStroke(DomainComponent dc, DomainViewI view) {
		if (dc.getDomain().isPutative()) {
			float dash[] = { 5.0f, 10.0f };
//			return new BasicStroke(1.375f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
			return new BasicStroke(1.75f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		}
		return new BasicStroke(1.375f);
	}
	
	/**
	 * @see AbstractDomainRenderer
	 */
	public String getLabel(DomainComponent dc, DomainViewI view) {
		return dc.getLabel();
	}

	/**
	 * @see AbstractDomainRenderer
	 */
	public Color getLabelColor(DomainComponent dc, DomainViewI view) {
		if (dc.getDomain().isPutative())
			return Color.black;
		
		return Color.white;
	}
	
	/**
	 * @see AbstractDomainRenderer
	 */
	public Shape getShape(DomainComponent dc, DomainViewI view) {
		if (view.getDomainLayoutManager().isShowShapes())
			return view.getDomainShapeManager().getDomainShape(dc);
		return dc.getDisplayedShape();
	}

	
}

