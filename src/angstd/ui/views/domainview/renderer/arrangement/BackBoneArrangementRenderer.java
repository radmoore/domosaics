package angstd.ui.views.domainview.renderer.arrangement;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;

import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.domainview.renderer.domain.DefaultDomainRenderer;

/**
 * The default implementation of the ArrangementRenderer interface
 * by extending the AbstractArrangementRenderer.
 * <p>
 * The renderer renders the arrangements backbone, where each pixel 
 * corresponds to one aminoacid in the underlying protein.
 * <p>
 * The initially set DomainRenderer is {@link DefaultDomainRenderer}.
 * 
 * 
 * @author Andreas Held
 *
 */
public class BackBoneArrangementRenderer extends AbstractArrangementRenderer {

	/**
	 * Basic constructor to initialize the arrangement renderer
	 * using the DefaultDomainRenderer for domains.
	 */
	public BackBoneArrangementRenderer () {
		super(new DefaultDomainRenderer());
	}

	/**
	 * This method draws the arrangements backbone.
	 * Because its possible that domains are drawn transparently, the
	 * backbone for the arrangement is drawn just in areas which are not 
	 * intersecting with domains.
	 * 
	 * @see ArrangementRenderer
	 */
	public void render(ArrangementComponent dac, DomainViewI view, Graphics2D g2) {
		// get coordinates for the arrangement component
		int x = dac.getX();
		int y = dac.getY();								// anchor point centered
		int y2 = (int) (y - (dac.getHeight() / 8.0));	// top left corner
		
		// get the hole backbone also intersecting with domains
		Rectangle backBone = null;
		if (view.getDomainLayoutManager().isProportionalView())
			backBone = new Rectangle(x, y2, dac.getWidth(), dac.getHeight()/4);
		
		// create an area containing all domain shapes
		Area domainShapes = new Area();
		for (DomainComponent dc : view.getArrangementComponentManager().getDomains(dac)) {
			if (!dc.getBoundingShape().intersects(g2.getClipBounds()))
				continue;
			else 
				domainShapes = addDomain(domainShapes, view, dc);
		}
		
		// calculate where the backbone has to be rendered
		if (backBone == null) 
			return;
		
		Area intersection = new Area(backBone);
		intersection.intersect(domainShapes);
		Area backBoneArea = new Area(backBone);
		backBoneArea.subtract(intersection);
		
		// and draw the backbone
		Color startColor = new Color (60, 60, 60, 120);
		Color endColor = new Color (60, 60, 60, 255);
		g2.setPaint(new GradientPaint(x+dac.getWidth()/2, y2, startColor, x+dac.getWidth()/2, y2+dac.getHeight()/4, endColor));
		g2.fill(backBoneArea);
	}
	
	/**
	 * Helper method to calculate the hole area in which domains are drawn
	 * 
	 * @param union
	 * 		area used to add the next domain shape
	 * @param view
	 * 		
	 * @param dc
	 * 		domain component which will be added to the domain area union
	 * @return
	 * 		the area containing also the specified domain component
	 */
	protected Area addDomain(Area union, DomainViewI view, DomainComponent dc) {
		Shape shape;
		if (view.getDomainLayoutManager().isShowShapes())
			shape = view.getDomainShapeManager().getDomainShape(dc);
		else
			shape = dc.getDisplayedShape();
		
		union.add(new Area(shape));
		return union;
	}
}
