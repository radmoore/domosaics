package angstd.algos.distance;

import angstd.algos.align.nw.NW4DomainsAffine;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.DomainVector;

/**
 * Calculates the domain-edit distance (Bjoerklund et al.) for 
 * arrangements using Needleman-Wunsch for domains. This class offers 
 * methods to calculate the distance for two arrangements or 
 * the pairwise distances for a set of arrangements.
 * <p>
 * 
 * 
 * @author Andreas Held
 *
 */
public class DomainDistance implements DistanceAlgorithm {

	/**
	 * Method actually doing the distance calculation for two arrangements
	 * 
	 * @param da1
	 * 		the first query arrangement
	 * @param da2
	 * 		the second query arrangement
	 * @return
	 * 		domain edit distance for the specified arrangements
	 */
	private static double calcDomainDistance(DomainArrangement da1, DomainArrangement da2) {
		// align arrangements first using needleman wunsch
		DomainVector[] aligned = new NW4DomainsAffine(da1, da2).getMatch(); //DomainDistanceNeedlemanWunsch(AbstractAlign.GAPOPENING, da1, da2).getMatch();
		
		int len = Math.min(aligned[0].size(), aligned[1].size());
		int res = Math.max(aligned[0].size(), aligned[1].size()) - len; // zero if both alignments have the same length
		for (int i = 0; i < len; i++) {
			if (!aligned[0].get(i).getFamily().equals(aligned[1].get(i).getFamily()))
				res++;
		}
		if (res == len)	// no domain family in common
			return Double.POSITIVE_INFINITY;
		return res;
	}
	
	/**
	 * Calculates the domain-edit distance (Bjoerklund et al.) for 
	 * two arrangements using Needleman-Wunsch for domains.
	 */
	public double calc(DomainArrangement da1, DomainArrangement da2) {
		return calcDomainDistance(da1, da2);
	}
	
	/**
	 * Calculates the domain-edit distance (Bjoerklund et al.) for 
	 * a set of arrangements using Needleman-Wunsch for domains.
	 */
	public double[][] calc(DomainArrangement[] daSet, boolean diagonalMatrix) {
		int N = daSet.length;
		double[][] res = new double[N][N];
		
		// calculate diagonal matrix by calculating pairwise distances
		for (int r = 0; r < N; r++) 
			for (int c = 0; c <= r; c++) 
				res[r][c] = calcDomainDistance(daSet[r], daSet[c]);
		
		// fill the other diagonal by taking the values from the first diagonal matrix
		for (int r = 0; r < N; r++) 
			for (int c = r; c < N; c++) 
				if (diagonalMatrix)
					res[r][c] = Double.NEGATIVE_INFINITY;
				else
					res[r][c] = res[c][r];
		
		return res;
	}

	
}
