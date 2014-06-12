package domosaics.ui.tools.dotplot.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.SwingUtilities;

import domosaics.ui.tools.dotplot.DotplotView;
import domosaics.ui.tools.dotplot.components.DefaultDotplotLayout;
import domosaics.ui.tools.dotplot.components.DotplotComponent;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.view.renderer.Renderer;




/**
 * The DefaultDotplotViewRenderer is used to render the domain 
 * dotplot.
 * 
 * @author Andreas Held
 *
 */
public class DefaultDotplotViewRenderer implements Renderer {

	/** the dotplot view to be rendered */
	protected DotplotView view;
	
	/** the arrangement renderer used to render the arrangements */
	protected DotplotArrangementRenderer daRenderer;
	
	/** the font used to draw the protein names on top of the arrangements */
	protected Font labelFont = new Font("Arial", 0, 14);

	
	/**
	 * Constructor for a new DefaultDotplotViewRenderer
	 * 
	 * @param view
	 * 		the dotplot view to be rendered
	 */
	public DefaultDotplotViewRenderer(DotplotView view) {
		this.view = view;
		daRenderer = new DotplotArrangementRenderer();
	}
	
	/**
	 * This method is used to render the hole view by 
	 * delegating to helper methods.
	 */
	@Override
	public void render(Graphics2D g) {
		Color oldColor = g.getColor();
		Font oldFont = g.getFont();
		
		// render the background
		Rectangle clip = g.getClipBounds();
		if (clip == null) 
			clip = view.getViewComponent().getVisibleRect();
		g.setColor(Color.white);
		g.fill(clip);
		
		// render the arrangements and the dotplot itself
		renderFirstArrangement(g);
		renderSecondArrangement(g);
		renderDotplot(g);

		g.setColor(oldColor);
		g.setFont(oldFont);
	}
	
	/**
	 * Helper method to draw the first (horizontal) arrangement 
	 * and position its label.
	 * 
	 * @param g
	 * 		the actual graphics context
	 */
	private void renderFirstArrangement(Graphics2D g) {
		ArrangementComponent dac1 = view.getDa1();
		g.setColor(Color.black);
		g.setFont(labelFont);
		
		// render first label and center it according to the DA
		int labelWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(labelFont), dac1.getLabel());
		int x = dac1.getX()+(dac1.getWidth()-labelWidth)/2;
		g.drawString(dac1.getLabel(), x, dac1.getY()-DefaultDotplotLayout.DIST2DA-DefaultDotplotLayout.LABELHEIGHT);
		
		// render first arrangement
		daRenderer.renderArrangement(dac1, view, g);
	}

	/**
	 * Helper method to draw the second (vertival) arrangement 
	 * and position its label. This arrangement has to be rotated into a
	 * vertical orientation using the AffineTransform before rendering it.
	 * 
	 * @param g
	 * 		the actual graphics context
	 */
	private void renderSecondArrangement(Graphics2D g) {
		ArrangementComponent dac2 = view.getDa2();
		g.setColor(Color.black);
		g.setFont(labelFont);
		
		// render second label by rotating it and centering according to the DA
		int labelX = (int) dac2.getBounds().getCenterX()-DefaultDotplotLayout.DIST2DA-DefaultDotplotLayout.LABELHEIGHT;
		int labelY = (int) dac2.getBounds().getCenterY();

		AffineTransform saveXform = g.getTransform();
		AffineTransform toCenterAt = new AffineTransform();
		AffineTransform rotateAt = new AffineTransform();
		
		rotateAt.rotate(Math.toRadians(270)); 
		toCenterAt.translate(labelX, labelY); 
		toCenterAt.concatenate(rotateAt);

		g.transform(toCenterAt);
		g.drawString(view.getDa2().getLabel(), 0, 0); 
		
		g.setTransform(saveXform);	// restore transformation
	
		// render second arrangement
		saveXform = g.getTransform();
			
		double centerX = dac2.getBounds().getCenterX();
		double centerY = dac2.getBounds().getCenterY();
	
		// build transformation: moving into the center of the DA, rotate by 90� and move back to the origin
		toCenterAt = new AffineTransform();
		rotateAt = new AffineTransform();
		AffineTransform backCenterAt = new AffineTransform();
		
		toCenterAt.translate(centerX, centerY); 
		rotateAt.rotate(Math.toRadians(90)); 
		backCenterAt.translate(-centerX, -centerY); 
		
		rotateAt.concatenate(backCenterAt);
		toCenterAt.concatenate(rotateAt);
		
		g.transform(toCenterAt);
		
		// render the arrangement using the transformation
	    daRenderer.renderArrangement(dac2, view, g);

	    g.setTransform(saveXform);
	}
	
	/**
	 * Helper method to draw the actual dotplot by retrieving the 
	 * image stored within the dotplot model.
	 * 
	 * @param g
	 * 		the actual graphics context
	 */
	private void renderDotplot(Graphics2D g) {
		DotplotComponent dotplotC = view.getDotplotComponent();
		
		// render dotplot border
		g.setColor(Color.black);
		Rectangle dotplotBorder = dotplotC.getBounds().getBounds();
		g.draw(new Rectangle (dotplotBorder.x-1, dotplotBorder.y-1, dotplotBorder.width+2, dotplotBorder.height+2));
		
		if (!view.getDotplotLayoutManager().isShowDotplot())
			return;
		
		// draw the dotplot image
		Rectangle oldClip = g.getClipBounds();
		Rectangle dotplotBounds = dotplotC.getBounds().getBounds();
		Rectangle clip = oldClip.intersection(dotplotBounds);
		g.setClip(clip);
		
		g.drawImage(dotplotC.getImg(), dotplotC.getX(), dotplotC.getY(), null);
	
		g.setClip(oldClip);
		
	}
}
