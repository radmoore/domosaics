package angstd.model.dotplot;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import angstd.algos.DotplotAlgo;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.matrix.Matrix;
import angstd.model.matrix.MatrixType;
import angstd.ui.util.AngstdSlider;

/**
 * Class which represents Dotplots. Generally visuell components, like a
 * DotplotView can be registered as listener to the dotplot, so it will
 * be informed, when recalculations occurred.
 * <p>
 * The Dotplot itsel can be registered to AngstdSliders as ChangeListener
 * to be up to date if the user manipulates a slider.
 * A computation is started by triggering the compute() method and the data
 * can be retrieved by using getDotMatrix().
 * <p>
 * There are three differen computation modes: <br>
 *  - character match without window or substitution matrix <br>
 *  - character match with window but without substitution matrix <br>
 *  - character match with window and substitution matrix <br>
 *  <p>
 *  The algorithm to compute the DotMatrix is described in {@link DotplotAlgo}
 * 
 * 
 * @author Andreas Held
 * 
 */
public class Dotplot implements ChangeListener {
	
	/** character match without window or substitution matrix */
	public static final int MATCHNOMATCH = 0;
	
	/** character match with window but without substitution matrix */
	public static final int MATCHNOMATCHWINDOW = 1;
	
	/** character match with window and substitution matrix */
	public static final int MATCHNOMATCHWINDOWMATRIX = 2;
	
	/** initial calculation mode */
	public static final int INITIALTYPE = MATCHNOMATCHWINDOWMATRIX;
	
	/** horizontal sequence */
	protected char[] horizontalSeq;
	
	/** vertical sequence */
	protected char[] verticalSeq;

	/** length of vertical sequence */
	protected int rows;	
	
	/** length of horizontal sequence */
	protected int cols;	

	/** window size */
	protected int winSize = 11;
	
	/** cutoff threshold */
	protected int cutoffThres = 5;
	
	/** substitution matrix */
	protected Matrix substitutionMatrix;
	
	/** resulting dot matrix */
	protected int[][] dotmatrix;
	
	/** calculation mode */
	protected int type;
	
	/** list of listeners which will be informd when the dotplot changes */
	protected List<DotplotChangeListener> listeners;
	
	
	/**
	 * Constructor for a new Dotplot object. Initializes sequences 
	 * from domain arrangements and the substitution matrix
	 * 
	 * @param horizonatlDA
	 * 		horizontal domain arrangement
	 * @param verticalDA
	 * 		vertical domain arrangement
	 */
	public Dotplot (DomainArrangement horizonatlDA, DomainArrangement verticalDA) {
		listeners = new ArrayList<DotplotChangeListener>();
		
		// get sequences without gaps
		this.horizontalSeq = horizonatlDA.getSequence().getSeq(false).toCharArray();
		this.verticalSeq = verticalDA.getSequence().getSeq(false).toCharArray();
		
		cols = this.horizontalSeq.length;
		rows = this.verticalSeq.length;
		
		substitutionMatrix = MatrixType.BLOSUM65.getMatrix();
		type = INITIALTYPE;
	}
	
	/**
	 * Registers a DotplotChangeListener which will be informed, 
	 * when changes occured
	 * 
	 * @param l
	 * 		the DotplotChangeListener to add
	 */
	public void addDotplotListener(DotplotChangeListener l) {
		listeners.add(l);
	}
	
	/**
	 * removes a DotplotChangeListener 
	 * 
	 * @param l
	 * 		the DotplotChangeListener to remove
	 */
	public void removeDotplotListener(DotplotChangeListener l) {
		listeners.remove(l);
	}
	
	/**
	 * Fires a DotplotChangeEvent to all registeres listeners
	 */
	public void fireDotplotChangeEvent() {
		DotplotChangeEvent evt = new DotplotChangeEvent(this);
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).dotplotChanged(evt);
	}
	
	/**
	 * Computes itself based on the computation mode and fires a changeevent to all
	 * registered listeners.
	 */
	public void compute() {
		if (type == MATCHNOMATCH)
			dotmatrix = DotplotAlgo.computeMatchNoMatch(this);
		else if (type == MATCHNOMATCHWINDOW)
			dotmatrix = DotplotAlgo.computeMatchNoMatchWithWindow(this);
		else if (type == MATCHNOMATCHWINDOWMATRIX)
			dotmatrix = DotplotAlgo.computeMatchNoMatchWithWindowAndMatrix(this);
		
		if (dotmatrix == null)
			return;
		
		fireDotplotChangeEvent();	
	}
	
	/**
	 * Return the used substitution matrix
	 * 
	 * @return
	 * 		used substitution matrix
	 */
	public Matrix getSubstitutionMatrix() {
		return substitutionMatrix;
	}
	
	/**
	 * Sets a new substitution matrix based on a predefined type and therefore 
	 * a new computation is triggered.
	 * 
	 * @param matrix
	 * 		new substitution matrix to set
	 */
	public void setSubstitutionMatrix(MatrixType matrix) {
		substitutionMatrix = matrix.getMatrix();
		if (type == MATCHNOMATCHWINDOWMATRIX)
			compute();
		
	}
	
	/**
	 * Sets a new substitution matrix and therefore a new computation
	 * is triggered.
	 * 
	 * @param matrix
	 * 		new substitution matrix to set
	 */
	public void setSubstitutionMatrix(Matrix matrix) {
		substitutionMatrix = matrix;
		if (type == MATCHNOMATCHWINDOWMATRIX)
			compute();
	}
	
	/**
	 * Changes the computation mode and therefore a recomputation is triggered.
	 * 
	 * @param type
	 * 		the computation mode
	 */
	public void setType(int type) {
		this.type = type;
		compute();
	}
	
	/** 
	 * The Dotplot can be registered to AngstdSliders as Listeners and
	 * therefore this method is triggered, if the state of the slider changes.
	 * E.G. because the user changed a threshold. A recomputation is triggered.
	 */
	public void stateChanged(ChangeEvent e) {
		AngstdSlider slider = (AngstdSlider)e.getSource();
	    if (!slider.getValueIsAdjusting()) {
	    	if (slider.getName().equals(AngstdSlider.WINSLIDER)) {
	    		if (winSize == slider.getValue())
	    			return;
	    		setWinSize(slider.getValue()); 
	    		compute();
	    		return;
	    	}
	    	if (slider.getName().equals(AngstdSlider.CUTOFFSLIDER))
	    		setCutoffThres(slider.getValue()); 
	    	
	    	fireDotplotChangeEvent();
	    } 
	}

	/**
	 * Returns the horizontal sequence
	 * 
	 * @return
	 * 		horizontal sequence
	 */
	public char[] getHorizontalSeq() {
		return horizontalSeq;
	}
	
	/**
	 * Returns the vertical sequence
	 * 
	 * @return
	 * 		vertical sequence
	 */
	public char[] getVerticalSeq() {
		return verticalSeq;
	}
	
	/**
	 * Returns the window size
	 * 
	 * @return
	 * 		window size
	 */
	public int getWinSize() {
		return winSize;
	}
	
	/**
	 * Sets the window size
	 * 
	 * @param size
	 * 		new window size
	 */
	public void setWinSize(int size) {
		winSize = size;
	}
	
	/**
	 * Returns the cutoff threshold
	 * 
	 * @return
	 * 		cutoff threshold
	 */
	public int getCutoffThres() {
		return cutoffThres;
	}

	/**
	 * Sets the cutoff threshold
	 * 
	 * @param thres
	 * 		new cutoff threshold
	 */
	public void setCutoffThres(int thres) {
		cutoffThres = thres;
	}

	/** 
	 * Returns the dotmatrix values
	 * 
	 * @return
	 * 		dotmatrix values
	 */
	public int[][] getDotMatrix() {
		return dotmatrix;
	}
	
	/**
	 * Returns the dimension of the matrix
	 * 
	 * @return
	 * 		matrix dimensions
	 */
	public Dimension getDim() {
		return new Dimension(cols, rows);
	}
}

