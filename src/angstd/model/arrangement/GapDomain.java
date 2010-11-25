package angstd.model.arrangement;

/**
 * Represents a Gap within a domain alignment (inserted in a domain vector).
 * 
 * @author Andreas Held
 *
 */
public class GapDomain extends Domain{

	/** Family id */
	protected static final String ID = "GAPDOMAIN";
	
	/**
	 * Constructor for a new Gapdomain.
	 * 
	 * @param da
	 * 		the arrangement the domain is associated with
	 */
	public GapDomain(DomainArrangement da) {
		super(0, 0, new DomainFamily(ID));
		setArrangement(da);
	}
}
