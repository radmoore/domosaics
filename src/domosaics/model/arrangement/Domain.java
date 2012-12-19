package domosaics.model.arrangement;

import java.text.DecimalFormat;

import domosaics.model.AngstdData;
import domosaics.model.sequence.SequenceI;


/**
 * Class Domain is the representing data structure for domains. <br>
 * <p>
 * the parameters gained from the input format are stored in here and the
 * domain sequences are automatically assigned, if a sequence is assigned to the arrangement.
 * <p>
 * Each domain is assigned to just one arrangement and the cross reference is made
 * because it is needed in layouting details. 
 * 
 * 
 * @author Andreas Held
 *
 */
public class Domain implements Comparable<Domain>, Cloneable, AngstdData {
	
	/** position specific parameters */ 
    protected int from, fromWithGaps, to;
    
    /** e-value of the annotation */
    protected double evalue = Double.POSITIVE_INFINITY;

    /** logg odds score of the annotation */
    protected double score = Double.NEGATIVE_INFINITY;
    
    /** domain family */
    protected DomainFamily fam;
    
    /** assigned domain arrangement */
    protected DomainArrangement da;
    
    /** assigned sequence data */
    protected SequenceI seq;

    /** Double format */
    protected DecimalFormat df = new DecimalFormat("0.00E0");

    /** if the domain bit score does not satisfy the Pfam gathering threshold */
    protected boolean putative = false;
    
	/**
	 * constructor for a new domain object without e-value specification.
	 * 
	 * @param from
	 * 		from index within the protein sequence
	 * @param to
	 * 		to index within the protein sequence
	 * @param fam
	 * 		domain family
	 */
	public Domain(int from, int to, DomainFamily fam) {
		this.fam = fam;
		this.to = to;
		this.from = from;
	}
	
	/**
	 * constructor for a new domain object with e-value specification.
	 * 
	 * @param from
	 * 		from index within the protein sequence
	 * @param to
	 * 		to index within the protein sequence
	 * @param fam
	 * 		domain family
	 * @param evalue
	 * 		evalue 
	 */
	public Domain (int from, int to, DomainFamily fam, double evalue) {
		if (to < from) {
			int help = from;
			from = to;
			to = help;
		}
		this.fam = fam;
		this.to = to;
		this.from = from;
		this.fromWithGaps = from;
		this.evalue = Double.parseDouble(df.format(evalue));
	}
	
	/**
	 * Sets the amino acid sequence for this domain and a new starting point.
	 * This is because the parameters from xdom files never take gaps into
	 * account.
	 * 
	 * @param fromWithGaps
	 * 		new start point in a gapped protein sequence
	 * @param seq
	 * 		sequence from which a subsequence is gained
	 */
	public void setSequence (int fromWithGaps, SequenceI seq) {
		this.fromWithGaps = fromWithGaps;
		this.seq = seq;
	}

	/**
	 * Returns the sequence of the domain 
	 * 
	 * @return
	 * 		sequence of the domain
	 */
	public SequenceI getSequence () {
		return seq;
	}
   
	/**
	 * Clones a domain, which is automatically called, when an arrangement
	 * is called, for all of its contained domains.
	 */
	public Object clone() throws CloneNotSupportedException {
        Domain copy = (Domain) super.clone();
		return copy;
    }
	
	/**
	 * Assigns the domain to its containing arrangement
	 * 
	 * @param da
	 * 		arrangement to which the domain is assigned
	 */
	public void setArrangement(DomainArrangement da) {
		this.da = da;
	}
	
	/**
	 * Returns the arrangement, to which the domain is assigned
	 * 
	 * @return
	 * 		the arrangement, to which the domain is assigned
	 */
	public DomainArrangement getArrangement() {
		return da;
	}
	
	/**
	 * Returns the family id of the domain
	 * 
	 * @return
	 * 		domain family id
	 */
	public String getName() {
		return fam.getName();
	}
	
	/**
	 * Returns the domain acc
	 * 
	 * @return domain accession
	 * 
	 */
	public String getID() {
		return fam.getId();
	}
	
	/**
	 * Sets the domain family
	 * 
	 * @param fam
	 * 		domain family of the domain
	 */
	public void setFamily(DomainFamily fam) {
		this.fam = fam;
	}
	
	/**
	 * Returns the domain family for this domain.
	 * 
	 * @return
	 * 		family for this domain.
	 */
	public DomainFamily getFamily() {
		return fam;
	}
	
	/**
	 * Sets the evalue for this domain
	 * 
	 * @param evalue
	 * 		evalue for this domain
	 */
	public void setEvalue (double evalue) {
		this.evalue = Double.parseDouble(df.format(evalue));;
	}
	
	/**
	 * Return the evalue for this domain
	 * 
	 * @return
	 * 		evalue for this domain
	 */
	public double getEvalue() {
		return evalue;
	}

	/**
	 * Sets the evalue for this domain
	 * 
	 * @param evalue
	 * 		evalue for this domain
	 */
	public void setScore (double score) {
		this.score = score;
	}
	
	/**
	 * Return the log odds score for this domain
	 * 
	 * @return
	 * 		score for this domain
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * Changes the beginning of the domain within the arrangement
	 * 
	 * @param from
	 * 		the beginning of the domain within the arrangement
	 */
	public void setFrom (int from) {
		this.from = from;
	}

	/**
	 * Changes the end position of the domain within the arrangement
	 * 
	 * @param from
	 * 		the end position of the domain within the arrangement
	 */
	public void setTo (int to) {
		this.to = to;
	}
	
	/**
	 * Returns the starposition of the domain within a gapped arrangement sequence
	 * 
	 * @return
	 * 		start position within a gapped arrangement sequence
	 */
	public int getFromWithGaps() {
		return fromWithGaps;
	}
	
	/**
	 * Returns the from position gained from the xdom format.
	 * 
	 * @return
	 * 		from position gained from the xdom format.
	 */
	public int getFrom() {
		return from;
	}
	
	/**
	 * Returns the to position gained from the xdom format.
	 * 
	 * @return
	 * 		to position gained from the xdom format.
	 */
	public int getTo() {
		return to;
	}
	
	/**
	 * Returns the domain length in amino acid sequences
	 * 
	 * @return
	 * 		domain length in amino acid sequences
	 */
	public int getLen() {
		return to - from;
	}
	
	/* Additional method for overlap searches */
	public boolean intersect(Domain dom) {
		if (this.to < dom.getFrom())
			return false;
		if (dom.getTo()< this.from)
			return false;
		return true;
	}

	/**
	 * Checks what domain comes before the other within
	 * an arrangement.
	 * FIXME there is a problem here if from is identical
	 * for both (as in place)
	 */
	public int compareTo (Domain dom) {
		return this.from - dom.from;
	}
	
	public String toString() {
		return getFrom()+"\\t"+getTo()+"\\t"+getID()+"\\t"+getEvalue();
	}
	
	/**
	 * Putting the "putative" flag
	 */
	public void setPutative (boolean b) {
		putative = b;
	}
	
	/**
	 * Checks if the domain is known (below Pfam thresholds) or not.
	 */
	public boolean isPutative() {
		return putative;
	}
	

}


//public boolean contains(Domain dom) {
//if (this.from <= dom.getFrom() && this.to >= dom.getTo())
//	return true;
//return false;
//}

//public int getIntersectLen(Domain dom) {
//return this.to - dom.getFrom();
//}

//public String toString() {
//String res = "Domainfamily id: "+fam.getID();
//if (evalue == Double.POSITIVE_INFINITY)
//	res = res+"(from to eval len): "+ getFrom() + " "+ getTo() +" N/A "+ getLen()+"\n";
//else
//	res = res+"(from to eval len): "+ getFrom() + " "+ getTo() +" "+ evalue +" - "+getLen()+"\n";
//return res;
//}
