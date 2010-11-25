package angstd.model.sequence.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Some sequence utils which are useful to convert DNA, CDNA and 
 * RNA sequences into amino acid sequences.
 * 
 * @author Andreas Held
 *
 */
public class SeqUtil {

	/** flag for an unknown sequence type */
	public static final int UNKNOWN = -1;
	
	/** flag for a DNA sequence type */
	public static final int DNA     = 0;
	
	/** flag for a CDNA sequence type */
	public static final int CDNA    = 1;
	
	/** flag for a RNA sequence type */
	public static final int RNA     = 2;
	
	/** flag for an amino acid sequence type */
	public static final int PROT    = 3;
	
	/** the genetic code which can be used to convert codons into amino acids */
	public static GeneticCodeMap geneticCode = new GeneticCodeMap();
	
	/**
	 * Method automatically checking the type of a sequence, 
	 * e.g. DNA, Protein ... .
	 * 
	 * @param seq
	 * 		the sequence string to be checked
	 * @return
	 * 		the format type of the specified sequence
	 */
	public static int checkFormat (String seq) {
		seq = seq.toUpperCase();
		
		// first create a list with all occuring letters within the sequence
		List<Character> letters = new ArrayList<Character>();
		char[] charSeq = seq.toCharArray();
		for (int i = 0; i < charSeq.length; i++)
			if (!letters.contains(charSeq[i]))
				letters.add(charSeq[i]);
		
		// check for PROTEIN
		if (letters.size() > 4)		
			return PROT;
		
		// check for RNA
		if (letters.contains('U') && !letters.contains('T'))
			return RNA;
		
		// check for DNA
		if (letters.contains('T') && !letters.contains('U')) 
			return CDNA;
		
		return UNKNOWN;
	}
	
	/**
	 * Method converting a RNA sequence into an amino acid sequence.
	 * 
	 * @param seq
	 * 		the sequence to be converted
	 * @return
	 * 		the converted amino acid sequence
	 */
	public static String rna2as (String seq) {
		StringBuffer result = new StringBuffer();
		
		for (int begin=0; begin+3 < seq.length(); begin+=3) 
			result.append(geneticCode.getAS(seq.substring(begin, begin+3)));
		
		return result.toString().replace("*", "");
	}
	
	/**
	 * Method converting a CDNA sequence into an amino acid sequence.
	 * Delegates to rna2as().
	 * 
	 * @param seq
	 * 		the sequence to be converted
	 * @return
	 * 		the converted amino acid sequence
	 */
	public static String cdna2amino(String seq) {
		return rna2as(seq.replace("T", "U"));
	}
}
