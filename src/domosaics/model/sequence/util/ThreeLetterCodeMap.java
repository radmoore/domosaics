package domosaics.model.sequence.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Class which makes it possible to convert an 1 letter coded amino acid
 * sequence into a three letter coded amino acid sequence.
 * 
 * @author Andreas Held
 *
 */
public class ThreeLetterCodeMap {

	/** mapping between an amino acid and its three letter code */
	protected Map<Character, String> code = new HashMap<Character, String>();
	
	/**
	 * Constructor for a new ThreeLetterCodeMap
	 */
	public ThreeLetterCodeMap() {
		code.put(new Character('G'), "Gly");
		code.put(new Character('A'), "Ala");
		code.put(new Character('V'), "Val");
		code.put(new Character('L'), "Leu");
		code.put(new Character('I'), "Ile");
		code.put(new Character('C'), "Cys");
		code.put(new Character('M'), "Met");
		code.put(new Character('F'), "Phe");
		code.put(new Character('Y'), "Tyr");
		code.put(new Character('W'), "Trp");
		code.put(new Character('P'), "Pro");
		code.put(new Character('S'), "Ser");
		code.put(new Character('T'), "Thr");
		code.put(new Character('N'), "Asn");
		code.put(new Character('Q'), "Gln");
		code.put(new Character('D'), "Asp");
		code.put(new Character('E'), "Glu");
		code.put(new Character('H'), "His");
		code.put(new Character('K'), "Lys");
		code.put(new Character('R'), "Arg");
	}
	
	/**
	 * Method converting an amino acid into its three letter code represantation
	 * 
	 * @param as
	 * 		the amino acid to be converted
	 * @return
	 * 		converted amino acid
	 */
	public String get3Letter(Character as) {
		return code.get(as);
	}
}
