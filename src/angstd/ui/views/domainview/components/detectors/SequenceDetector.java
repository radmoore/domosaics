package angstd.ui.views.domainview.components.detectors;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import angstd.model.sequence.Sequence;
import angstd.model.sequence.SequenceI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.views.domainview.mousecontroller.SequenceSelectionMouseController;



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
					int y = (int) intersection.getBounds().y;
					
					Point p = new Point(x+1, y);
					while(intersection.contains(p)) 
						p.x += 1;
					
					int subwidth = p.x - x;
					int height = dac.getDisplayedShape().getBounds().height;
					
					Rectangle sub = new Rectangle(x, y, subwidth, height);

					// retrieve the sequence from this rectangle
					SequenceI seq;
					if (!view.getDomainLayoutManager().isFitDomainsToScreen())
						seq = selectWithoutFitToScreen(dac, sub);
					else
						seq = selectWithFitToScreen(dac, sub);
					res.add(seq);
					
					// subtract it from the intersection area now and do it again
					intersection.subtract(new Area(sub));
				}

				// retrieve the sequence for the last sub rectangle
				SequenceI seq;
				if (!view.getDomainLayoutManager().isFitDomainsToScreen())
					seq = selectWithoutFitToScreen(dac, intersection.getBounds());
				else
					seq = selectWithFitToScreen(dac, intersection.getBounds());
				
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
	private SequenceI selectWithoutFitToScreen(ArrangementComponent dac, Rectangle intersection) {
		SequenceI seq = new Sequence();
		
		int seqStart = intersection.x-dac.getX();
		int seqEnd= seqStart+intersection.width;
		
		seq.setName(dac.getDomainArrangement().getName()+"_"+(seqStart+1)+"-"+(seqEnd+1));
		seq.setSeq(dac.getDomainArrangement().getSequence().getSeq(seqStart, seqEnd, false));
		
		return seq;
	}
	
	/**
	 * Helper method to retrieve the sequences in normal proportional mode.
	 * 
	 * @param dac
	 * 		the actual domain arrangement view component
	 * @param intersection
	 * 		the rectangle within the view component in which the sequence has to be retrieved
	 * @return
	 * 		the sequence within the specified area
	 */
	private SequenceI selectWithFitToScreen(ArrangementComponent dac, Rectangle intersection) {
		SequenceI seq = new Sequence();
		
		int maxLen = view.getDomainLayout().getMaxLen();
		int width = view.getDomainLayout().getDomainBounds().width;
		double ratio = maxLen / (double) width;
		
		double startX = (Math.abs(dac.getX() - intersection.x));
		double endX = startX + (intersection.width);
		
		int seqStart = (int) Math.round(startX * ratio);
		int seqEnd= (int) Math.round(endX*ratio); 
		
		// rounding error balancing
		if (seqEnd == dac.getDomainArrangement().getSequence().getLen(false)-1)
			seqEnd++;
		if (seqEnd > dac.getDomainArrangement().getSequence().getLen(false))
			seqEnd = dac.getDomainArrangement().getSequence().getLen(false);
		
		seq.setName(dac.getDomainArrangement().getName()+"_"+(seqStart+1)+"-"+(seqEnd+1));
		seq.setSeq(dac.getDomainArrangement().getSequence().getSeq(seqStart, seqEnd, false));
		
		return seq;
	}
}
