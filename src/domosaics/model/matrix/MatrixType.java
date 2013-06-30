package domosaics.model.matrix;

import java.io.File;


/**
 * Enumeration for different supported substitution matrices, which
 * are located in the folder resources within this package. <br>
 * The {@link MatrixParser}parses a matrix from a file into a form
 * which makes it usable for dotplot calculations.
 * 
 * @author Andreas Held
 *
 */
public enum MatrixType {
	
	BLOSUM62("resources/BLOSUM62"),
	BLOSUM65("resources/BLOSUM65"),
	IDENTITY("resources/IDENTITY")
	;
	
	private String file;
	
	private MatrixType(String file) {
		this.file = file;
	}
	
	/**
	 * Parses a matrix file from within the jar file into a Matrix object, which
	 * can be used as substitutionmatrix.
	 * 
	 * @return
	 * 		Substitutionmatrix
	 */
	public Matrix getMatrix() {
		Matrix[] matrix = new MatrixParser().getDataFromStream(getClass().getResourceAsStream(file));
		return matrix[0];
	}
}
