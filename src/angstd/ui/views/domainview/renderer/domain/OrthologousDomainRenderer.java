package angstd.ui.views.domainview.renderer.domain;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import angstd.ui.util.ColorUtil;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;

/**
 * OrthologousDomainRenderer draws domains in search orthologous mode.
 * All domains which are not of interest are colored gray.
 * The domains of interest are gradually transparent and their labels
 * correspond to percentage of similarity.
 * 
 * @author Andreas Held, Andrew Moore
 *
 */
public class OrthologousDomainRenderer extends AbstractDomainRenderer {

	/**
	 * @see AbstractDomainRenderer
	 */
	public Color getColor(DomainComponent dc, DomainViewI view) {
		int alpha = 255; 
		/*
		 * There exists within the domainview an option, which searches for
		 * orthologous domains based on a query domain.
		 * If this option is set only the querried domainfamily is
		 * not rendered in gray, but in its color and an alpha value
		 * depending on the similarity.
		 */
		if (view.isCompareDomainsMode() && view.getDomainSearchOrthologsManager().getDomainScore(dc) != -1) {
			int alphaPercent = view.getDomainSearchOrthologsManager().getDomainScore(dc);
			alpha = percent2alpha(alphaPercent);
		}
		
		Color color = view.getDomainColorManager().getDomainColor(dc);
		if (view.getDomainSearchOrthologsManager().getDomainScore(dc) == -1)
			color = Color.GRAY;
		
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
		String label = dc.getLabel();
		
		// change it to percent identity if necessary
		if (view.getDomainSearchOrthologsManager().getDomainScore(dc) != -1) 
			label = view.getDomainSearchOrthologsManager().getDomainScore(dc)+" %";
		
		return label;
	}

	/**
	 * @see AbstractDomainRenderer
	 */
	public Color getLabelColor(DomainComponent dc, DomainViewI view) {
		if (view.getDomainSearchOrthologsManager().getDomainScore(dc) != -1) 
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
	
	/**
	 * Helper method to map a percent value to an alpha value
	 * 
	 * @param percent
	 * 		the percent value to map
	 * @return
	 * 		the corresponding alpha value for a given percentage value
	 */
	private int percent2alpha (int percent) {
		if (percent == 0)
			return 0;
		else if(percent <= 10)
			return 25;
		else if(percent <= 20)
			return 50;
		else if(percent <= 30)
			return 75;
		else if(percent <= 40)
			return 100;
		else if(percent <= 50)
			return 125;
		else if(percent <= 60)
			return 150;
		else if(percent <= 70)
			return 175;
		else if(percent <= 80)
			return 200;
		else if(percent <= 90)
			return 225;
		else if(percent < 100)
			return 250;
		else 
			return 255;
	}

}
