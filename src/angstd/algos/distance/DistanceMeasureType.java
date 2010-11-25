package angstd.algos.distance;

/**
 * Enumeration of supported distance measures for domain arrangements. 
 * The algorithm class which is used to compute the distance as 
 * well as its name is stored.
 * 
 * @author Andreas Held
 *
 */
public enum DistanceMeasureType {

	JACARD(			"Jacard",			new JaccardDistance ()),
	DOMAINDISTANCE(	"Domain Distance",	new DomainDistance ()),
	;
	
	private String name;
	private DistanceAlgorithm algo;
	
	private DistanceMeasureType(String name, DistanceAlgorithm algo) {
		this.name = name;
		this.algo = algo;
	}
	
	public String getName() {
		return name;
	}
	
	public DistanceAlgorithm getAlgo() {
		return algo;
	}
	
	public String toString() {
		return name;
	}
	
}
