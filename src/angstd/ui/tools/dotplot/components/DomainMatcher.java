package angstd.ui.tools.dotplot.components;

import java.util.ArrayList;
import java.util.List;

import angstd.algos.align.SequenceAligner;
import angstd.model.arrangement.Domain;
import angstd.model.arrangement.DomainArrangement;

/**
 * Helper class to calculate and save the similarity values for the
 * domain matches which are then represented as boxes containing
 * a label showing the similarity.
 * <p>
 * The similarity is calculated for all representants of the same domain
 * family within two arrangements.
 * 
 * @author Andreas Held
 *
 */
public class DomainMatcher {

	
	protected int[][] percent;
	
	/** lists containing the domains of the arrangements used to retrieve the index within the percentage matrix */
	protected List<Domain> rowIndex, colIndex;
	
	/**
	 * Constructor for a new domain similarity manager
	 * 
	 * @param da1
	 * 		the first arrangement containing query domains
	 * @param da2
	 * 		the second arrangement containing query domains
	 */
	public DomainMatcher(DomainArrangement da1, DomainArrangement da2) {
		percent = new int[da1.countDoms()][da2.countDoms()];
		
		// fill the helper lists with the domains
		rowIndex = new ArrayList<Domain>();
		colIndex = new ArrayList<Domain>();
		for (int i = 0; i < da1.countDoms(); i++)
			rowIndex.add(da1.getDomain(i));
		for (int i = 0; i < da2.countDoms(); i++)
			colIndex.add(da2.getDomain(i));
		
		calculateMatchScores(da1, da2);
	}
	
	/**
	 * Method to retrieve the similarity score of two domains
	 * 
	 * @param dom1
	 * 		the first query domain
	 * @param dom2
	 * 		the second query domain
	 * @return
	 * 		the similarity score for the query domains
	 */
	public int getPercent(Domain dom1, Domain dom2) {
		return this.percent[rowIndex.indexOf(dom1)][colIndex.indexOf(dom2)];
	}
	
	/**
	 * Sets the similarity score for two domains
	 * 
	 * @param dom1
	 * 		the first query domain
	 * @param dom2
	 * 		the second query domain
	 * @param percent
	 * 		the similarity score for the query domains to be set
	 */
	public void setPercent(Domain dom1, Domain dom2, int percent) {
		this.percent[rowIndex.indexOf(dom1)][colIndex.indexOf(dom2)] = percent;
	}
	
	/**
	 * Helper method to calculate the similarity scores for domain pairs.
	 * 
	 * @param da1
	 * 		first query arrangement
	 * @param da2
	 * 		second query arrangement
	 */
	private void calculateMatchScores(DomainArrangement da1, DomainArrangement da2) {
		for (int i1 = 0; i1 < da1.countDoms(); i1++)
			for (int i2 = 0; i2 < da2.countDoms(); i2++) {
				Domain dom1 = da1.getDomain(i1);
				Domain dom2 = da2.getDomain(i2);
				int score = new SequenceAligner().align(dom1.getSequence(), dom2.getSequence());
				setPercent(dom1, dom2, score);	
			}
	}
}
