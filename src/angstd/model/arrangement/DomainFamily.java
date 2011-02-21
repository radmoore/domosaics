package angstd.model.arrangement;

/**
 * Class DomainFamily represents just represents the attributes
 * of a domain family.
 * 
 * @author Andreas Held
 *
 */
public class DomainFamily  {

	/** family id */
	protected String id;
	/** family accession number */
	protected String acc;
	/** Required threshold to detect the domain occurrence */
	private double gathThreshByDom;
	/** Required threshold to detect the family (adding all occurrences) */
	private double gathThreshByFam;
	
	/**
	 * Constructor for a new DomainFamily of Pfam database.
	 * 
	 * @param id
	 * 		family id
	 * @param acc
	 * 		family acc
	 * @param gath1
	 * 		Required threshold to detect the domain occurrence
	 * @param gath2
	 * 		Required threshold to detect the family (adding all occurrences)
	 */
	public DomainFamily(String id, String acc, double gath1, double gath2) {
		this.id = id;
		this.acc = acc;
		this.gathThreshByDom = gath1;
		this.gathThreshByFam = gath2;
	}
	
	/**
	 * Constructor for a new DomainFamily with a specified id (from Interpro databese
	 * except Pfam).
	 * 
	 * @param id
	 * 		family id
	 */
	public DomainFamily(String acc) {
		this.id = acc;
	}
	
	/**
	 * Set family id
	 * 
	 * @param id
	 * 		new family id
	 */
	public void setID(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the family id
	 * 
	 * @return
	 * 		family id
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Checks if two domain families are equal. <b>
	 * This is done just by comparing the family IDs.
	 * 
	 * @param fam
	 * 		the domain family to compare
	 * @return
	 * 		Whether or not both families have identical IDs.
	 */
	public boolean equals(DomainFamily fam) {
		return id.toUpperCase().equals(fam.getID().toUpperCase());
	}
	
}
