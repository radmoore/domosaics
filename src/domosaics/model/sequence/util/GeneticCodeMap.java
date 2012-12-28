package domosaics.model.sequence.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Class which helps to convert DNA sequences into amino acid sequences.
 * 
 * @author Andreas Held
 *
 */
public class GeneticCodeMap {

	/** mapping between triplets and their corresponding amino acid */
	protected Map<String, Character> code = new HashMap<String, Character>();
	
	
	/**
	 * Constructor for a new GeneticCodeMap
	 */
	public GeneticCodeMap() {
		init();
	}
	
	/**
	 * Method to convert a codon into an amino acid.
	 * 
	 * @param codon
	 * 		the DNA triplet to be converted
	 * @return
	 * 		the encrypted amino acid
	 */
	public char getAS(String codon) {
		return code.get(codon);
	}
	
	/**
	 * Helper method initializin the genetic code
	 */
	private void init() {
		code.put("AUG", new Character('M'));	// start
		code.put("UGG", new Character('W'));
		code.put("UAU", new Character('Y'));
		code.put("UAC", new Character('Y'));
		code.put("UUU", new Character('F'));
		code.put("UUC", new Character('F'));
		code.put("UGU", new Character('C'));
		code.put("UGC", new Character('C'));
		code.put("AAU", new Character('N'));
		code.put("AAC", new Character('N'));
		code.put("GAU", new Character('D'));
		code.put("GAC", new Character('D'));
		code.put("CAA", new Character('Q'));
		code.put("CAG", new Character('Q'));
		code.put("GAA", new Character('E'));
		code.put("GAG", new Character('E'));
		code.put("CAU", new Character('H'));
		code.put("CAC", new Character('H'));
		code.put("AAA", new Character('K'));
		code.put("AAG", new Character('K'));
		code.put("AUU", new Character('I'));
		code.put("AUC", new Character('I'));
		code.put("AUA", new Character('I'));
		code.put("GGU", new Character('G'));
		code.put("GGC", new Character('G'));
		code.put("GGA", new Character('G'));
		code.put("GGG", new Character('G'));
		code.put("GCU", new Character('A'));
		code.put("GCC", new Character('A'));
		code.put("GCA", new Character('A'));
		code.put("GCG", new Character('A'));
		code.put("GUU", new Character('V'));
		code.put("GUC", new Character('V'));
		code.put("GUA", new Character('V'));
		code.put("GUG", new Character('V'));
		code.put("ACU", new Character('T'));
		code.put("ACC", new Character('T'));
		code.put("ACA", new Character('T'));
		code.put("ACG", new Character('T'));
		code.put("CCU", new Character('P'));
		code.put("CCC", new Character('P'));
		code.put("CCA", new Character('P'));
		code.put("CCG", new Character('P'));
		code.put("CUU", new Character('L'));
		code.put("CUC", new Character('L'));
		code.put("CUA", new Character('L'));
		code.put("CUG", new Character('L'));
		code.put("UUA", new Character('L'));
		code.put("UUG", new Character('L'));
		code.put("UCU", new Character('S'));
		code.put("UCC", new Character('S'));
		code.put("UCA", new Character('S'));
		code.put("UCG", new Character('S'));
		code.put("AGU", new Character('S'));
		code.put("AGC", new Character('S'));
		code.put("CGU", new Character('R'));
		code.put("CGC", new Character('R'));
		code.put("CGA", new Character('R'));
		code.put("CGG", new Character('R'));
		code.put("AGA", new Character('R'));
		code.put("AGG", new Character('R'));
		code.put("UAA", new Character('*'));					// stop
		code.put("UAG", new Character('*'));					// stop
		code.put("UGA", new Character('*'));					// stop
	}
	
	
}
            