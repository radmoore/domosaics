package domosaics.model.arrangement;

import java.util.HashMap;
import java.util.Iterator;

import domosaics.model.GO.GeneOntologyTerm;




/**
 * Class DomainFamily represents the attributes
 * of a domain family.
 * 
 * @author Andreas Held
 *
 */
public class DomainFamily  {

	/** family name */
	protected String name;
	
	/** family accession number, primary id */
	protected String id;

	/** Interpro entry */
	protected String interproEntry = null;
	
	/** Required threshold to detect the domain occurrence */
	private double gathThreshByDom = Double.POSITIVE_INFINITY;
	
	/** Required threshold to detect the family (adding all occurrences) */
	private double gathThreshByFam = Double.POSITIVE_INFINITY;
	
    /** Pfam, SMART, superfamily, pir **/
    protected DomainType type;
    
    /** GO Terms **/
    protected HashMap<String, GeneOntologyTerm> goTerms;
    
    
    
	
	/**
	 * Constructor for a new DomainFamily of Pfam database.
	 * 
	 * @param name
	 * 		family name
	 * @param id
	 * 		family id
	 * @param gath1
	 * 		Required threshold to detect the domain occurrence
	 * @param gath2
	 * 		Required threshold to detect the family (adding all occurrences)
	 */
	public DomainFamily(String id, String name, DomainType type, double gath1, double gath2) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.gathThreshByDom = gath1;
		this.gathThreshByFam = gath2;
		
	}
	
	/**
	 * Constructor for a new DomainFamily with a specified name (from Interpro databese
	 * except Pfam).
	 */
	public DomainFamily(String id, String name, DomainType type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Set family name
	 * 
	 * @param name
	 * 		new family name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the family name
	 * 
	 * @return
	 * 		family name
	 */
	public String getName() {
		if (name == null)
			return id;
		
		return name;
	}
	
	
	/**
	 * Set the Interpro Entry
	 * 
	 * @param name
	 * 		Interpro Entry
	 */
	public void setInterproEntry(String ie) {
		this.interproEntry = ie;
	}
	
	/**
	 * Returns the Interpro Entry
	 * 
	 * @return
	 * 		Interpro Entry
	 */
	public String getInterproEntry() {
		return interproEntry;
	}
	
	/**
	 * Returns the family accession number
	 * As we must always be able to return an ID, if its empty
	 * we just return the name. The effect is that if only one _type_
	 * of ID is provided (either ID or name) a call to get either of the two
	 * will _always_ return something (whatever that may be - we can know
	 * whether what is provided is an id or a name if only one identifier is
	 * supplied)
	 * 
	 * @return
	 * 		accession number id
	 */
	public String getId() {
		if (id == null)
			return name;
		return id;
	}
	
	/**
	 * Returns the family gathThreshByDom
	 * 
	 * @return
	 * 		gathThreshByDom gath1
	 */
	public double getGathThreshByDom() {
		return gathThreshByDom;
	}
	
	/**
	 * Returns the family gathThreshByFam
	 * 
	 * @return
	 * 		gathThreshByFam gath2
	 */
	public double getGathThreshByFam() {
		return gathThreshByFam;
	}

	
	/**
	 * Returns the family gathThreshByDom
	 * 
	 * @param
	 * 		gathThreshByDom gath1
	 */
	public void setGathThreshByDom(Double gath1) {
		gathThreshByDom=gath1;
	}
	
	/**
	 * Set the family gathThreshByFam
	 * 
	 * @param
	 * 		gathThreshByFam gath2
	 */
	public void setGathThreshByFam(Double gath2) {
		gathThreshByFam=gath2;
	}
	
	public DomainType getDomainType() {
		return this.type;
	}
	
	
	public void addGoTerm(GeneOntologyTerm goTerm) {
		if (goTerms == null)
			goTerms = new HashMap<String, GeneOntologyTerm>();
		
		goTerms.put(goTerm.getID(), goTerm);
	}
	
	public boolean hasGoAnnotation() {
		return (! (goTerms == null) );
	}
	
	public boolean hasGoTerm(String gid) {
		return (goTerms == null) ? false : goTerms.containsKey(gid);
	}
	
	
	public int totalGoTerms() {
		return (goTerms == null) ? 0 : goTerms.size(); 
	}
	
	public Iterator<GeneOntologyTerm> getGoTerms() {
		return this.goTerms.values().iterator();
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
		id = getId();
		return id.toUpperCase().equals(fam.getId().toUpperCase());
	}
	
}
