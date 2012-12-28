package domosaics.algos.distance;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainSet;

/**
 * Calculates the Jaccard-Index for arrangements. This class offers 
 * methods to calculate the Jaccard distance for two arrangements or 
 * the pairwise distances for a set of arrangements.
 * <p>
 * The Jaccard index is defines as: <br>
 * 1 - |intersection of domains | / |union of domains |
 * 
 * @author Andreas Held
 *
 */
public class JaccardDistance implements DistanceAlgorithm {

	/**
	 * Method actually doing the distance calculation for two domain sets
	 * 
	 * @param set1
	 * 		non redundant domain set for arrangement one
	 * @param set2
	 * 		non redundant domain set for arrangement two
	 * @return
	 * 		jaccard index for the two domain sets
	 */
	private static double calcJacard(DomainSet set1, DomainSet set2) {
		return 1 - set1.intersect(set2).size() / (double) set1.union(set2).size();
	}
	
	/**
	 * Calculates the Jaccard-Index for two arrangements.
	 */
	public double calc(DomainArrangement da1, DomainArrangement da2) {
		// create the domainsets
		DomainSet set1 = new DomainSet();
		set1.add(da1.getDomains());
		
		DomainSet set2 = new DomainSet();
		set2.add(da2.getDomains());
		
		// and calculate the jaccard distance
		return calcJacard(set1, set2);
	}
	
	/**
	 * Calculates the pairwaise Jaccard-Index for a set of arrangements.
	 */
	public double[][] calc(DomainArrangement[] daSet, boolean diagonalMatrix) {
		int N = daSet.length;
		double[][] res = new double[N][N];
		
		// create DomainSet for each arrangement
		DomainSet[] sets = new DomainSet[N];
		for (int i = 0; i < N; i++) {
			sets[i] = new DomainSet();
			sets[i].add(daSet[i].getDomains());
		}
		
		// calculate diagonal matrix by calculating pairwise distances
		for (int r = 0; r < N; r++) 
			for (int c = 0; c <= r; c++) 
				res[r][c] = calcJacard(sets[r], sets[c]);
		
		// fill the other diagonal
		for (int r = 0; r < N-1; r++) 
			for (int c = r+1; c < N; c++) 
				if (diagonalMatrix)
					res[r][c] = Double.NEGATIVE_INFINITY;
				else
					res[r][c] = res[c][r];
		
		return res;
	}

}
