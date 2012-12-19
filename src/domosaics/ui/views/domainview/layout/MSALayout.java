package domosaics.ui.views.domainview.layout;

import java.awt.Font;

import javax.swing.SwingUtilities;

import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.views.domainview.components.ArrangementComponent;


/**
 * MSALayout defines the requested properties for 
 * arrangements and domains for AbstractDomainlayout.
 * 
 * @author Andreas Held
 *
 */
public class MSALayout extends AbstractDomainLayout {

	/** the font used to draw the underlying sequences */
	public static final Font FONT = new Font("Courier", 0, 14);	

	/**
	 * @see AbstractDomainLayout
	 */
	public int getDomainX(Domain dom) {
		if (dom.getSequence() == null)
			return -1;
		
		int charwidth = SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(FONT), "-");
		return charwidth * (dom.getFromWithGaps());	 // -1?	
	}
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getDomainWidth(Domain dom){
		if (dom.getSequence() == null)
			return 0;
		
		int charwidth = SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(FONT), "-");
		return charwidth * dom.getSequence().getLen(true);
	}
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getArrangementWidth(DomainArrangement da) {
		if (da.getSequence() == null) 
			return 0;
		
		int charwidth = SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(FONT), "-");	
		return da.getSequence().getSeq(true).length()*charwidth;
	}
	
	/**
	 * @see AbstractDomainLayout
	 */
	public int getArrangementX(ArrangementComponent dac) {
		return 0;
	}
	
}
