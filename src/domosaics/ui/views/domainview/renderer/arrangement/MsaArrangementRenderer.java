package domosaics.ui.views.domainview.renderer.arrangement;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.layout.MSALayout;
import domosaics.ui.views.domainview.renderer.domain.MsaDomainRenderer;




/**
 * The MSA arrangement renderer renders proteins with their
 * underlying sequence.
 * 
 * @author Andreas Held
 *
 */
public class MsaArrangementRenderer extends AbstractArrangementRenderer {

	/**
	 * Constructor initializing the MsaDomainRenderer to render domains
	 */
	public MsaArrangementRenderer () {
		super(new MsaDomainRenderer());
	}
	
	/**
	 * @see AbstractArrangementRenderer
	 */
	public void render(ArrangementComponent dac, DomainViewI view, Graphics2D g2) {
		// render the arrangement only if a sequence is assigned to it
		if (dac.getDomainArrangement().getSequence() == null)
			return;
		
		Font oldFont = g2.getFont();

		// render the sequence of the Arrangement
		g2.setFont(MSALayout.FONT);
		g2.setColor(Color.black);
		
		int x = dac.getX();
		int y = (int) (dac.getY() + (g2.getFont().getSize2D() / 2.0));
		g2.drawString(dac.getDomainArrangement().getSequence().getSeq(true), x, y-2);
		
		g2.setFont(oldFont);
	}

}
