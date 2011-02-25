package angstd.ui.views.domainview.renderer.domain;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import angstd.ui.util.ColorUtil;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;

/**
 * MsaDomainRenderer which draws domains in MSA mode. Basically
 * it just draws a colored and transparent rectangle for each domain.
 * Because the underlying sequence is drawn, no domain label is returned.
 * 
 * @author Andreas Held, Andrew Moore
 *
 */
public class MsaDomainRenderer extends AbstractDomainRenderer {

	/**
	 * @see AbstractDomainRenderer
	 */
	public Color getColor(DomainComponent dc, DomainViewI view) {
		int alpha = 120; 
		Color color = view.getDomainColorManager().getDomainColor(dc);
		color = ColorUtil.createAlphaColor(color, alpha);
		return color;
	}

	/**
	 * @see AbstractDomainRenderer
	 */
	public Paint getPaint(DomainComponent dc, DomainViewI view, Graphics2D g2) {
		return g2.getPaint();
	}
	
	/**
	 * @see AbstractDomainRenderer
	 */
	public Stroke getStroke(DomainComponent dc, DomainViewI view) {
		if (dc.getDomain().isPutative()) {
			float dash[] = { 5.0f, 10.0f };
			return new BasicStroke(1.375f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		}
		return new BasicStroke(1.375f);
	}

	/**
	 * @see AbstractDomainRenderer
	 */
	public String getLabel(DomainComponent dc, DomainViewI view) {
		return null;
	}

	/**
	 * @see AbstractDomainRenderer
	 */
	public Color getLabelColor(DomainComponent dc, DomainViewI view) {
		return null;
	}
	
	/**
	 * @see AbstractDomainRenderer
	 */
	public Shape getShape(DomainComponent dc, DomainViewI view) {
		return new Rectangle(dc.getX(), dc.getTopLeft(), dc.getWidth(), dc.getHeight());
	}
	
	/**
	 * @see AbstractDomainRenderer
	 */
	public void hightlightDomain(DomainComponent dc, DomainViewI view, Graphics2D g2) {
		;
	}


}
