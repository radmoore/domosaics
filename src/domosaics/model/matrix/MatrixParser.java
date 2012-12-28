package domosaics.model.matrix;

import java.io.BufferedReader;
import java.io.Reader;

import domosaics.model.configuration.Configuration;
import domosaics.model.io.AbstractDataReader;
import domosaics.ui.util.MessageUtil;





/**
 * MatrixParser can parse substitution matrices obtained from NCBI.
 * 
 * @author Andreas Held
 *
 */
public class MatrixParser extends AbstractDataReader<Matrix> {
	
	/** resulting Matrix */
	protected Matrix[] matrix = new Matrix[1];
	
	/**
	 * Parses the data and returns a 1D array containing the matrix
	 */
	public Matrix[] getData(Reader reader) {
		matrix[0] = new Matrix();
		
		try {
			BufferedReader in = new BufferedReader(reader); 
			boolean firstLine = false;	
			String line;
		
			while((line = in.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				
				if(line.trim().isEmpty())
					continue;
				
				if (!firstLine) {
					char[] alphabet = line.trim().replaceAll("\\s+", "").toCharArray();
					matrix[0].setAlphabet(alphabet);
					firstLine = true;
					continue;
				}
				
				parseLine(line);
			}

			in.close();	
			return matrix;
		} 
		catch (Exception e) {
			MessageUtil.showWarning("Reading Matrix file aborted");
			Configuration.getLogger().debug(e.toString());
		} 
		return null;
	}
	
	private void parseLine(String line) {
		String[] token = line.trim().split("\\s+");
		
		// token[0] is the alphabet character
		Character symbol = token[0].toUpperCase().charAt(0);
		
		// rest of the tokens are the scores
		Integer[] scoreVec = new Integer[token.length-1];
		for(int i = 1; i < token.length; i++)
			scoreVec[i-1] = Integer.parseInt(token[i]);

		matrix[0].addRow(symbol, scoreVec);
	}
}
