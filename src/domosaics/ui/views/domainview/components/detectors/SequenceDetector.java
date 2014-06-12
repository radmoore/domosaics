package domosaics.ui.views.domainview.components.detectors;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import domosaics.model.sequence.Sequence;
import domosaics.model.sequence.SequenceI;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.views.domainview.layout.MSALayout;
import domosaics.ui.views.domainview.mousecontroller.SequenceSelectionMouseController;




/**
 * The SequenceDetector can be used to retrieve underlying sequences
 * of drawn domain arrangements. This detector works only in the
 * proportional view, which is no problem, because the sequence selection
 * mouse controller is only enabled in this view.
 * <p>
 * Both fit domains to screen and the unfitted view is compatible. 
 * Because its possible that more than one part of a sequence is
 * selected the sequence detector resolves each sub rectangle from the
 * hole selected area.
 * <p>
 * For more details on the selection process see {@link SequenceSelectionMouseController}.
 * 
 * @author Andreas Held
 *
 */
public class SequenceDetector {
	
	/** the view owning the arrangements */
	protected DomainViewI view;

	/**
	 * Basic constructor for a new sequence resolver.
	 * 
	 * @param view
	 * 		the domain view providing the selection mode
	 */
	public SequenceDetector(DomainViewI view){
		this.view = view; 
	}
		
	/**
	 * Retrieves all underlying sequences from an selection area.
	 * 
	 * @param selArea
	 * 		the view area selected and to be searched for sequences
	 * @return
	 * 		all sequences found within the search area
	 */
	public SequenceI[] searchSequenceComponents(Area selArea) {
		List<SequenceI> res = new ArrayList<SequenceI>();
		System.out.println("MaxLen "+view.getDomainLayout().getMaxLen());
		System.out.println("DomBounds "+view.getDomainLayout().getDomainBounds().width);
		Iterator<ArrangementComponent> iter = view.getArrangementComponentManager().getComponentsIterator();
		while(iter.hasNext()) {
			ArrangementComponent dac = iter.next();

			if (!dac.isVisible())
				continue;
			
			if (dac.getDomainArrangement().getSequence() == null)
				continue;

			// check if rectangle intersects with the arrangement
			if (selArea.intersects(dac.getDisplayedShape().getBounds2D())) {
				// create intersection of selection area and actual domain arrangement
				Area intersection = new Area(selArea);
				intersection.intersect(new Area(dac.getDisplayedShape().getBounds()));
				
				/*
				 * its possible that that two or more parts of the sequence
				 * were selected. We run in problems then, because we have to 
				 * gain all the sub rectangles and create the sequences out of them
				 */
				while (!intersection.isSingular()) {
					// create the first sub rectangle
					int x = intersection.getBounds().x;
					int y = intersection.getBounds().y;
					
					Point p = new Point(x+1, y);
					while(intersection.contains(p)) 
						p.x += 1;
					
					int subwidth = p.x - x;
					int height = dac.getDisplayedShape().getBounds().height;
					
					Rectangle sub = new Rectangle(x, y, subwidth, height);

					// retrieve the sequence from this rectangle
					SequenceI seq = selectSequence(dac, intersection.getBounds());
					res.add(seq);
					
					// subtract it from the intersection area now and do it again
					intersection.subtract(new Area(sub));
				}

				// retrieve the sequence for the last sub rectangle
				SequenceI seq = selectSequence(dac, intersection.getBounds());
				
				res.add(seq);
			}
		}	
		
		return res.toArray(new Sequence[res.size()]);
	}
	
	/**
	 * Helper method to retrieve the sequences in fitToScreen mode.
	 * 
	 * @param dac
	 * 		the actual domain arrangement view component
	 * @param intersection
	 * 		the rectangle within the view component in which the sequence has to be retrieved
	 * @return
	 * 		the sequence within the specified area
	 */
	private SequenceI selectSequence(ArrangementComponent dac, Rectangle intersection) {
		SequenceI seq = new Sequence();
		
		int seqStart = intersection.x-dac.getX();
		int seqEnd= seqStart+intersection.width;

		if(view.getDomainLayoutManager().isMsaView()) {
			int charwidth = SwingUtilities.computeStringWidth(view.getViewComponent().getFontMetrics(MSALayout.FONT), "-");
			seqStart = seqStart/charwidth;
			seqEnd= seqEnd/charwidth;
			
			seq.setSeq(dac.getDomainArrangement().getSequence().getSeq(seqStart, seqEnd, true));			
		} else
		{
			if (view.getDomainLayoutManager().isFitDomainsToScreen())
			{
				int maxLen = view.getDomainLayout().getMaxLen();
				int width = view.getDomainLayout().getDomainBounds().width;
				double ratio = maxLen / (double) width;				
				if(maxLen>width) {
					seqStart = (int) (seqStart * ratio);
					seqEnd = (int) (seqEnd * ratio);
				}
				
			}
			
			seq.setSeq(dac.getDomainArrangement().getSequence().getSeq(seqStart, seqEnd, false));
		}
			
		seq.setName(dac.getDomainArrangement().getName()+"_"+(seqStart+1)+"-"+seqEnd);

		/*System.out.println(intersection.toString());
		System.out.println(intersection.x+" - "+dac.getX()+" = "+seqStart);
		System.out.println(seqStart+" + "+intersection.width+" = "+seqEnd);
		System.out.println(dac.getDomainArrangement().getName());
		System.out.println(dac.getDomainArrangement().getSequence().getSeq(true));
		System.out.println(seq.getSeq(true));/**/
		
		return seq;
	}
	
}
