package domosaics.ui.views.sequenceview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import domosaics.model.sequence.SequenceI;
import domosaics.ui.views.view.renderer.Renderer;




public class DefaultSequenceViewRenderer implements Renderer {
	
	private SequenceView view;
	
	/* ******************************************************************* *
	 *   						Constructor methods						   *
	 * ******************************************************************* */

	public DefaultSequenceViewRenderer (SequenceView view) {
		this.view = view;
	}
	
	/* ******************************************************************* *
	 *   						 Renderering methods					   *
	 * ******************************************************************* */
	
	@Override
	public void render(Graphics2D g) {
		// get clip bounds or visible rectangle
		Rectangle r = g.getClipBounds();
		if (r == null) 
			r = view.getViewComponent().getVisibleRect();

		// render the view
		renderBackground(g, r);
		renderSequencess(g, r);
	}

	private void renderBackground(Graphics2D g, Rectangle r) {
		g.setColor(Color.white);
		g.fill(r);
	}

	private void renderSequencess(Graphics2D g2, Rectangle clip) {
		Shape oldClip = g2.getClip();
		Font oldFont = g2.getFont();
		Color oldCOlor = g2.getColor();
		
		Font font = new Font("Courier", 0, 14);
		g2.setClip(clip.x, clip.y, clip.width, clip.height);	
		g2.setFont(font);
		g2.setColor(Color.black);
		
		// iterate over the DAs and render each one
		if (view.getSeqs() != null) {
			int x = 10;
			int y = 15;
			
			for (int i = 0; i < view.getSeqs().length; i++) {
				SequenceI seq = view.getSeqs()[i];
				
				// TODO check intersect clip
				;
				
				// render the sequence 
				g2.drawString(">"+seq.getName(), x, y);
				y+=font.getSize()+4;
				g2.drawString(seq.getSeq(true), x, y);
				
//				for (int p = 0; p < seq.getLen(true); p+=40) {
//					g2.drawString(seq.getSeq(p, Math.min(seq.getLen(true), p+40), true), x, y);
//					y+=font.getSize()+4;
//				}
				
				y+=font.getSize()+4;
			}
		}
		
		g2.setClip(oldClip);
		g2.setFont(oldFont);
		g2.setColor(oldCOlor);
	}
}
