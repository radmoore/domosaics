package domosaics.model.sequence;

import domosaics.model.AngstdData;

/**
 * Interface SequenceI describes the basic data structure to store sequences. <br>
 * Its important to notice that sequences may contain gaps, but the values in
 * xdom formats are always gap free. Therefore in some methods a flag indicates
 * whether or not gaps should be eliminated.
 * 
 * @author Andreas Held
 *
 */
public interface SequenceI extends AngstdData{
	
	/**
	 * Method for cloning sequence objects
	 * 
	 * @return
	 * 		a copy of the cloned sequence
	 * @throws CloneNotSupportedException
	 * 		error which may occur during the cloning process
	 */
	 public Object clone() throws CloneNotSupportedException; 
	
	/**
	 * Sets the name for the sequence
	 * 
	 * @param name
	 * 		name for the sequence
	 */
	public void setName(String name);
	
	/**
	 * Sets the sequence data
	 * 
	 * @param seq
	 * 		sequnce data
	 */
	public void setSeq (String seq);
	
	/**
	 * Returns the name for the sequence
	 * 
	 * @return
	 * 		sequence name
	 */
	public String getName();
	
	/**
	 * Returns the sequence data, with or without gaps.
	 * 
	 * @param gaps
	 * 		flag whether or not gaps should be eliminated
	 * @return
	 * 		sequence with or without gaps
	 */
	public String getSeq (boolean gaps);
	
	/**
	 * Returns the data of a subsequence, with or without gaps.
	 * 
	 * @param from
	 * 		start of the subsequence
	 * @param to
	 * 		end of the subsequence
	 * @param gaps
	 * 		flag whether or not gaps should be eliminated
	 * @return
	 * 		sub sequence with or without gaps
	 */
	public String getSeq (int from, int to, boolean gaps);
	
	/**
	 * Returns the length of the sequence with or without counting gaps.
	 * 
	 * @param gaps
	 * 		flag whether or not gaps should be counted
	 * @return
	 * 		length of the sequence with or without counting gaps.
	 */
	public int getLen(boolean gaps);
	
	public String toOutputString();
	
	/**
	 * Returns the fasta formatted sequence
	 * 
	 * @return FASTA representation of the sequence
	 */
	public String toFasta(boolean gaps);
	
}
