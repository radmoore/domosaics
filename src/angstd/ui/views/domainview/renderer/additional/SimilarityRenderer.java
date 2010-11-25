package angstd.ui.views.domainview.renderer.additional;

import java.awt.Color;
import java.awt.Graphics2D;

import angstd.algos.distance.DistanceMeasureType;
import angstd.ui.util.ColorUtil;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.manager.DomainSimilarityManager;
import angstd.ui.views.view.renderer.Renderer;

/**
 * The SimilarityRenderer draws a transparent white rectangle on top
 * of each arrangement. The transparency grade depends on the
 * percentage similarity to a reference arrangement. The details on that 
 * are handled in {@link DomainSimilarityManager}. The higher the
 * similarity the more transparent is the rectangle.
 * 
 * NOT USED FOR THE TIME BEING
 * 
 * @author Andreas Held
 *
 */
public class SimilarityRenderer  implements Renderer {

	/** view providing the similarity functionality */
	protected DomainViewI view;
	
	/**
	 * Constructor for a new SimilarityRenderer
	 * 
	 * @param view
	 * 		view providing the similarity functionality
	 */
	public SimilarityRenderer (DomainViewI view) {
		this.view = view;
	}
	
	/**
	 * draw a transparent white rectangle on top of each arrangement
	 */
	public void render(Graphics2D g2) {
		return;
//		if(!view.getDomainLayoutManager().isCollapseBySimilarity())
//			return;
//		
//		if(view.getDomainLayoutManager().isMsaView())
//			return;
//		
//		DomainSimilarityManager manager = view.getDomainSimilarityManager();
//		
//		for (int i = 0; i < view.getDaSet().length; i++) {
//			ArrangementComponent dac = view.getArrangementComponentManager().getComponent(view.getDaSet()[i]);
//		
//			if (!dac.isVisible())
//				continue;
//			
//			// if arrangement is below clipping area break
//			if (dac.getBounds().getY()-dac.getBounds().getHeight() > g2.getClipBounds().y+g2.getClipBounds().height)
//				break;
//			
//			// DRAW
//			Color oldColor = g2.getColor();
//			
//			if(manager.getMeasureType() == DistanceMeasureType.DOMAINDISTANCE)
//				g2.setColor(ColorUtil.createDomainDistanceAlphaColor(Color.white, manager.getSimilarity(dac)));
//			else
//				g2.setColor(ColorUtil.createPercentageAlphaColor(Color.white, manager.getSimilarity(dac)));
//			
//			g2.fill(dac.getDisplayedShape()); 
//			g2.draw(dac.getDisplayedShape());
//			g2.setColor(oldColor);
//		}
	}

}
