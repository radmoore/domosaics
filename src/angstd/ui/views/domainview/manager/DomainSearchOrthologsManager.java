package angstd.ui.views.domainview.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import angstd.algos.align.SequenceAligner;
import angstd.model.sequence.SequenceI;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.DomainComponent;
import angstd.ui.views.view.manager.DefaultViewManager;

/**
 * The DomainSearchOrthologsManager maps each DomainComponent a 
 * percentage similarity score to a specified query domain. <br>
 * If the manager should be run for all domains of an arrangement
 * just use the process() method on each domain.
 * 
 * 
 * @author Andreas Held
 *
 */
public class DomainSearchOrthologsManager extends DefaultViewManager {

	/** mapping between a domain component and its similarity score */
	protected Map<DomainComponent, Integer> dc2score;

	
	/**
	 * Constructor for a new DomainSearchOrthologsManager
	 */
	public DomainSearchOrthologsManager() {
		dc2score = new HashMap<DomainComponent, Integer>();
	}
	
	/**
	 * Resets the manager
	 */
	public void reset() {
		dc2score.clear();
	}
	
	/**
	 * Calculates the score for all domains of one domain family compared
	 * to the reference domain sequence chosen by the user.
	 * 
	 * @param view
	 * 		the active view
	 * @param queryDom
	 * 		the reference domain on which each member of the domain family is compared to
	 */
	public void process(DomainViewI view, DomainComponent queryDom) {
		List<DomainComponent> targets = new ArrayList<DomainComponent>();
		
		// find all domain instances for the current query domain
		Iterator<DomainComponent> iter = view.getArrangementComponentManager().getDomainComponentsIterator();
		while(iter.hasNext()) {
			DomainComponent dc = iter.next();
			
			// only add instances of the same domain family
			if(!dc.getDomain().getFamily().equals(queryDom.getDomain().getFamily()))
				continue;
			targets.add(dc);
		}
		
		// extract the sequences from those domains
		SequenceI querySeq = queryDom.getDomain().getSequence();
		if (querySeq == null)
			return;
		
		for (DomainComponent dom : targets) {
			if(dom.getDomain().getSequence()!=null){
				int score = new SequenceAligner().align(querySeq, dom.getDomain().getSequence());
				view.getDomainSearchOrthologsManager().setScore(dom, score);
			}
		}
		
		//manually trigger repaint before the next domain is set
		view.getViewComponent().repaint();
		return;
	}
	
  	
  	/**
  	 * Return the similarity score for a specified DomainComponent.
  	 * If no score is assigned -1 is returned.
  	 * 
  	 * @param dc
  	 * 		the DomainComponent which similarity score is requested
  	 * @return
  	 * 		similarity score for the specified DomainComponent (or -1).
  	 */
  	public Integer getDomainScore(DomainComponent dc) {
   		if (dc2score.get(dc) == null) 
   			return -1;
   		else
   			return dc2score.get(dc);
   	}
	
  	/**
  	 * Sets the similarity score for a specified DomainComponent
  	 * 
  	 * @param dc
  	 * 		the DomainComponent which similarity score should be set
  	 * @param score
  	 * 		the new score for the specified DomainComponent
  	 */
	public void setScore(DomainComponent dc, Integer score) {
		dc2score.put(dc, score);
	}	

}
