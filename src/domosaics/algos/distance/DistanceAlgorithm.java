package domosaics.algos.distance;

import domosaics.model.arrangement.DomainArrangement;

/**
 * Interface for a distance or similarity distance algorithm.
 * The algorithm should be defined for two and a set of domain arrangements, so
 * it can be used for creating distance matrices.
 * 
 * @author Andreas Held
 *
 */
public interface DistanceAlgorithm {

	/**
	 * Calculates the distance between two arrangements
	 * 
	 * @param da1
	 * 		first arrangement to specify
	 * @param da2
	 * 		second arrangement to specify
	 * @return
	 * 		distance between the specified arrangements
	 */
	public double calc(DomainArrangement da1, DomainArrangement da2);
	
	/**
	 * Calculates the distance between a set of arrangements
	 * 
	 * @param daSet
	 * 		the set of arrangements to be specified
	 * @param diagonalMatrix
	 * 		whether or not just one diagonal should be filled
	 * @return
	 * 		distancem atrix holding pairwise distances for the specified arrangements
	 */
	public double[][] calc(DomainArrangement[] daSet, boolean diagonalMatrix) throws OutOfMemoryError;
}
