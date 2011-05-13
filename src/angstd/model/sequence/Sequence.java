package angstd.model.sequence;

/**
 * Basic implementation of the {@link SequenceI} interface. For further
 * details see the interface description.
 * 
 * @author Andreas Held
 *
 */
public class Sequence implements SequenceI, Cloneable{
	
	/** name of the sequence */
	protected String name;
	
	/** the sequence string */
	protected String seq;
	
	/** number of gaps within the sequence */
	protected int gaps = 0;
	
	
	/**
	 * Constructor for a new empty sequence.
	 */
	public Sequence () {
		this(null, null);
	}
	
	/**
	 * Constructor for a new sequence with a name and sequence data.
	 * 
	 * @param name
	 * 		name of the sequence
	 * @param seq
	 * 		sequence data
	 */
	public Sequence (String name, String seq) {
		this.name = name;
		this.seq = seq;
		this.gaps = countGaps();
	}
	
	/**
	 * Count the number of gaps within the sequence
	 * 
	 * @return
	 * 		number of gaps within the sequence
	 */
	private int countGaps() {
		if (seq == null)
			return 0;
		return seq.replaceAll("[^-]","").length();
	}
	
	/**
	 * Clones the sequence object. 
	 * 
	 * @return
	 * 		a cloned copy of the sequence.
	 */
    public Object clone() throws CloneNotSupportedException {
    	return (Sequence) super.clone();
    }
	
    /**
     * @see SequenceI
     */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @see SequenceI
	 */
	public void setSeq (String seq) {
		this.seq = seq;
		this.gaps = countGaps();
	}
	
	/**
	 * @see SequenceI
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @see SequenceI
	 */
	public String getSeq (boolean gaps) {
		String res = new String(seq);
		if (!gaps)
			res = res.replace("-", "");
		return res;
	}
	
	/**
	 * @see SequenceI
	 */
	public String getSeq (int from, int to, boolean gaps) {
		if (!gaps) 
			return seq.replace("-", "").substring(from, to);
		return seq.substring(from, to);
	}
	
	/**
	 * @see SequenceI
	 */
	public int getLen(boolean gaps) {
		if (!gaps)
			return seq.length() - this.gaps;
		return seq.length();
	}
	
	/**
	 * @see SequenceI
	 */
	public String toOutputString() {
		return ">"+name+"\n"+getSeq(true)+"\n";
	}
	
	public String toFasta(boolean gaps) {
		return ">"+name+"\n"+getSeq(gaps)+"\n";
	}
	
	public String toString() {
		return getName();
	}

}
