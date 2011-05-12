package angstd.model.arrangement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import angstd.model.GO.GeneOntologyTerm;

/**
 * Class DomainFamily represents the attributes
 * of a domain family.
 * 
 * @author Andreas Held
 *
 */
public class DomainFamily  {

	/** family id, secondary id */
	protected String id;
	
	/** family accession number, primary id */
	protected String acc;

	/** Interpro entry */
	protected String interproEntry;
	
	/** Required threshold to detect the domain occurrence */
	private double gathThreshByDom;
	
	/** Required threshold to detect the family (adding all occurrences) */
	private double gathThreshByFam;
	
    /** Pfam, SMART, superfamily, pir **/
    protected DomainType type;
    
    /** GO Terms **/
    protected HashMap<String, GeneOntologyTerm> goTerms;
    
    
    
	
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
	public DomainFamily(String id, String acc, DomainType type, double gath1, double gath2) {
		this.id = id;
		this.acc = acc;
		this.type = type;
		this.gathThreshByDom = gath1;
		this.gathThreshByFam = gath2;
		
	}
	
	/**
	 * Constructor for a new DomainFamily with a specified id (from Interpro databese
	 * except Pfam).
	 * 
	 * @param id
	 * 		family acc
	 */
	public DomainFamily(String acc, String id, DomainType type) {
		this.acc = acc;
		this.id = id;
		this.type = type;
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
		if (id == null)
			return acc;
		
		return id;
	}
	
	
	/**
	 * Set the Interpro Entry
	 * 
	 * @param id
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
	 * FIXME
	 * As we must always be able to return an ACC, if its empty
	 * we just return the ID. The effect is that if only one _type_
	 * of ID is provided (either ID or ACC) a call to get either of the two
	 * will _always_ return something (whatever that may be - we can know
	 * wether what is provided is an acc or and ID if only one identifier is
	 * supplied)
	 * 
	 * @return
	 * 		accession number acc
	 */
	public String getAcc() {
		if (acc == null)
			return id;
		return acc;
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
		return gathThreshByDom;
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
	
	public Iterator getGoTerms() {
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
		return id.toUpperCase().equals(fam.getID().toUpperCase());
	}
	
}
