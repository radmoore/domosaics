package domosaics.algos;

import domosaics.model.dotplot.Dotplot;
import domosaics.model.matrix.Matrix;

/**
 * Algorithm to compute dotplots. 
 * <p>
 * There are three different computation modes: <br>
 *  - character match without window or substitution matrix <br>
 *  - character match with window but without substitution matrix <br>
 *  - character match with window and substitution matrix <br>
 * 
 * @author Andreas Held
 * 
 *
 */
public class DotplotAlgo {

	/** horizontal and vertical sequence */
	private static char[] hSeq, vSeq;
	
	/** number of rows and columns */
	private static int rows, cols;
	
	/** size of the window and half its size */
	private static int winSize, winHalf;
	
	/** used substitutionmatrix */
	private static Matrix subMatrix;
	
	/** resulting dotmatrix */
	private static int[][] dotMatrix;
	

	/**
	 * initializes the class variables based on the dotplot
	 * 
	 * @param plot
	 * 		the dotplot to be computed.
	 */
	private static void init (Dotplot plot) {
		hSeq = plot.getHorizontalSeq();
		vSeq = plot.getVerticalSeq();
		
		rows = vSeq.length;
		cols = hSeq.length;
		
		dotMatrix = new int[rows][cols];
		
		winSize = plot.getWinSize();
		
		// only odd window sizes are allowed so cheat on even sizes
		if (winSize % 2 == 0)
			winSize-=1;
		winHalf = (int) Math.floor(winSize/2);
		
		subMatrix = plot.getSubstitutionMatrix();
	}
	
	/**
	 * Computes the dotplot without window and substitutionmatrix
	 * 
	 * @param plot
	 * 		plot to compute
	 * @return
	 * 		computed plot
	 */
	public static int[][] computeMatchNoMatch(Dotplot plot) {
		init(plot);
		
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				dotMatrix[r][c] = (hSeq[c] == vSeq[r]) ? 1 : 0;
		
		return dotMatrix;
	}
	
	/**
	 * Computes the dotplot with window and without substitutionmatrix
	 * 
	 * @param plot
	 * 		plot to compute
	 * @return
	 * 		computed plot
	 */
	public static int[][] computeMatchNoMatchWithWindow(Dotplot plot) {
		init(plot);
		
		for (int r = winHalf; r < rows-winHalf; r++)
			for (int c = winHalf; c < cols-winHalf; c++) {
				
				int sum = 0;
				for (int w = -winHalf; w <= winHalf; w++) 
					sum += (hSeq[c+w] == vSeq[r+w]) ? 1 : 0;
			
				dotMatrix[r][c] = sum;				
			}
		return dotMatrix;
	}
	
	/**
	 * Computes the dotplot with window and substitutionmatrix
	 * 
	 * @param plot
	 * 		plot to compute
	 * @return
	 * 		computed plot
	 */
	public static int[][] computeMatchNoMatchWithWindowAndMatrix(Dotplot plot) {
		System.out.println("la");
		init(plot);
		Integer[][] scoreMatrix = new Integer[rows][cols];

		// init resulting matrix, TODO set to minimal possible threshold, get it from the dotplot
		for (int r = 0; r < rows; r++) 
			for (int c = 0; c < cols; c++)
				dotMatrix[r][c] = -20;

		// for each symbol create a vector using the horizontal seq as input
		Matrix helpMatrix = subMatrix.useSequenceOnMatrix(hSeq);
		
		// create score matrix for the vertical residues in the dot matrix
		for (int r = 0; r < rows; r++) 
			scoreMatrix[r] = helpMatrix.getScores(new Character(vSeq[r]));
		
		// now we can slide the window using the score matrix
		for (int r = winHalf; r < rows-winHalf; r++) 
			for (int c = winHalf; c < cols-winHalf; c++) {
				
				// sum up the score within the window
				int sum = 0;
				for (int w = -winHalf; w <= winHalf; w++)  
					sum += scoreMatrix[r+w][c+w];
				
				dotMatrix[r][c] = sum;
			}
		
		return dotMatrix;
	}
}

