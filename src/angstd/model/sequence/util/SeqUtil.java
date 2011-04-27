package angstd.model.sequence.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public static final String NONVALIDRESEXP =  "[^FLIMVSPTAYHQNKDECWRGX*-]";
	
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
		for (int i = 0; i < charSeq.length; i++) {
			if (!letters.contains(charSeq[i]))
				letters.add(charSeq[i]);
		}
		
		// check for PROTEIN (all AA + '-', '*', 'X')
		if ( (letters.size() > 4) && (letters.size() <= 23) ) {
			
			Pattern nonRes = Pattern.compile(NONVALIDRESEXP);
		    Matcher m = nonRes.matcher(seq);
			
		    // find *non-valid* characters
		    if (m.find())
		    	return UNKNOWN;
			else
				return PROT;
				
		}
			
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
	
	
//	public static boolean checkFastaFileFormat(File fastaFile) {
//		
//		FileReader reader = new FileReader(fastaFile);
//		BufferedReader in = new BufferedReader(reader);
//		
//		String line, entry;
//		
//		// the initial sequence type before guessing the format
//		int type = UNKNOWN;
//		
//		// do the parsing now
//		while((line = in.readLine()) != null) {
//			if (line.isEmpty())				// skip empty lines
//				continue;
//			if (line.startsWith(";"))		// skip comment lines
//				continue;
//			
//			if (line.startsWith(">")) {		// parse header line
//				
//					
//			// guess the format
//			type = SeqUtil.checkFormat(seqBuf.toString().replace("*", ""));
//
//			}
//		
//		seq = seq.toUpperCase();
//		
//		// first create a list with all occuring letters within the sequence
//		List<Character> letters = new ArrayList<Character>();
//		char[] charSeq = seq.toCharArray();
//		for (int i = 0; i < charSeq.length; i++)
//			if (!letters.contains(charSeq[i]))
//				letters.add(charSeq[i]);
//		
//		// check for PROTEIN
//		if (letters.size() > 4)		
//			return PROT;
//		
//		// check for RNA
//		if (letters.contains('U') && !letters.contains('T'))
//			return RNA;
//		
//		// check for DNA
//		if (letters.contains('T') && !letters.contains('U')) 
//			return CDNA;
//		
//		return UNKNOWN;
//		
//		
//		if (type == SeqUtil.UNKNOWN) {
//			MessageUtil.showWarning("Can't determine the sequence format.");
//			return false;
//		}
//
//
//		
//	}
	
}
