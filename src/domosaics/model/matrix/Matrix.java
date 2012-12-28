package domosaics.model.matrix;

import java.util.HashMap;
import java.util.Map;

import domosaics.model.DomosaicsData;




/**
 * Class Matrix represents a substitution matrix which can be used when 
 * computing dotplots. 
 * 
 * @author Andreas Held
 *
 */
public class Matrix implements DomosaicsData {

	/** the alphabet of the substitution matrix */
	protected char[] alphabet;
	
	/** a Mapping of the alphabet characters to their row=col index */
	protected Map<Character, Integer> symbol2index;
	
	/** substitution rates for the matrix */
	protected Map<Character, Integer[]> rows;
	
	
	/**
	 * Constructor for a new Substitutionmatrix
	 */
	public Matrix() {
		rows = new HashMap<Character, Integer[]>();
		symbol2index = new HashMap<Character, Integer>(); 
	}
	
	/**
	 * Returns the substitution rate for two given characters from within the
	 * alphabet.
	 * 
	 * @param sym1
	 * 		first character to specify
	 * @param sym2
	 * 		second character to specify
	 * @return
	 * 		substitution rate for the specified characters.
	 */
	public int get(Character sym1, Character sym2) {
		return rows.get(sym1)[symbol2index.get(sym2)];
	}
	
	/**
	 * Returns all substitution rates for one character
	 * 
	 * @param symbol
	 * 		character from the alphabet
	 * @return
	 * 		all substitution rates for one character
	 */
	public Integer[] getScores(Character symbol) {
		return rows.get(symbol);
	}
	
	/**
	 * Returns the row=col index for a character within the matrix.
	 * 
	 * @param symbol
	 * 		character from the alphabet
	 * @return
	 * 		row=col index for a character within the matrix.
	 */
	public int symbol2index(Character symbol) {
		return symbol2index.get(symbol);
	}
	
	/**
	 * This one is a bit tricky and i can't remember what it exactly does. :)
	 * It is used to generate a help matrix which improves speed when creating
	 * the dotplot. 
	 * 
	 * @param seq
	 * 		good question.
	 * @return
	 * 		unknown
	 */
	public Matrix useSequenceOnMatrix (char[] seq) {
		Matrix res = new Matrix();
		res.setAlphabet(alphabet);
		
		for (int i = 0; i < alphabet.length; i++) {
			Character symbol = new Character(alphabet[i]);
			Integer[] row = rows.get(symbol);

			Integer[] scores = new Integer[seq.length];
			for (int j = 0; j < seq.length; j++)
				scores[j] = row[symbol2index(seq[j])];
			
			res.addRow(symbol, scores);
		}
		return res;
	}
	
	/**
	 * Sets the alphabet for this matrix
	 * 
	 * @param alpha
	 * 		new alphabet
	 */
	public void setAlphabet(char[] alpha) {
		alphabet = alpha;
		for (int i = 0; i < alphabet.length; i++)
			symbol2index.put(new Character(alphabet[i]), i);
	}
	
	/**
	 * Returns the alphabet of the substitution matrix.
	 * 
	 * @return
	 * 		alphabet of the substitution matrix.
	 */
	public char[] getAlphabet() {
		return alphabet;
	}
	
	/**
	 * Adds a row to the substitution matrix.
	 * 
	 * @param symbol
	 * 		character where the row data is added
	 * @param scores
	 * 		all substitutionrates for the specified character
	 */
	public void addRow(Character symbol, Integer[] scores) {
		rows.put(symbol, scores);
	}
	
	/**
	 * Prints the matrix to the console
	 */
	public void print() {
		for (int i = 0; i < alphabet.length; i++) {
			Integer[] scores = rows.get(alphabet[i]);
			System.out.print(alphabet[i]+": ");
			for(int j = 0; j < scores.length; j++)
				System.out.print(" "+scores[j]+" ");
			System.out.println();
		}
	}
}
