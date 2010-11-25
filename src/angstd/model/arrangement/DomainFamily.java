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
	
	/**
	 * Constructor for a new DomainFamily with a specified id.
	 * 
	 * @param id
	 * 		family id
	 */
	public DomainFamily(String id) {
		this.id = id;
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
