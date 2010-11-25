package angstd.algos.treecreation;

/**
 * Available algorithms for tree creation, such as UPGMA or NJ.
 * 
 * @author Andreas Held
 *
 */
public enum TreeCreationAlgoType {
	
	NJ ("Neighbor Joining"),
	UPGMA ("UPGMA");
	;
		
	private String name;
	
	private TreeCreationAlgoType(String name){
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
}
