package angstd.localservices.hmmer3.programs;

import java.awt.Cursor;
import java.io.File;

import javax.swing.JPanel;

import angstd.localservices.executor.Executor;
import angstd.localservices.hmmer3.Hmmer3Engine;
import angstd.localservices.hmmer3.Hmmer3Service;
import angstd.localservices.hmmer3.ui.HmmerServicePanel;
import angstd.ui.util.MessageUtil;

/**
 * Class to run a local version of hmmpress (Hmmer3).
 * Pressed files are required to run hmmscan. Hmmpress
 * creates a binary files (much link formatdb for BLAST).
 * Implements the {@link Hmmer3Program} interface. Is not
 * executed directly; execution is handled by {@link Hmmer3Engine}
 * which expects a program of type {@link Hmmer3Program}.
 * 
 * TODO
 * - the interaction with the calling parent panel is a serious mess and must be cleaned up
 * - parse results to display the number of successfull pressed models
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class HmmPress implements Hmmer3Program {

	// hmmpress extensions
	protected static final String H3F = ".h3f"; 
	protected static final String H3I = ".h3i";
	protected static final String H3M = ".h3m";
	protected static final String H3P = ".h3p";
	
	protected File hmmPressBin, hmmDBFile;
	protected String[] args;
	protected HmmerServicePanel parentServicePanel;
	protected JPanel parentPanel;
	protected String name;
	

	
	/**
	 * Default constructor to be used by any class
	 * @param hmmPressBin
	 * @param hmmDBFile
	 * @param parent
	 */
	public HmmPress (File hmmPressBin, File hmmDBFile) {
		this.hmmPressBin = hmmPressBin;
		this.hmmDBFile = hmmDBFile;
		this.name = "HMMPRESS";
	}
	
	/**
	 * Constructor to be from a HmmerServicePanel
	 * @param hmmPressBin
	 * @param hmmDBFile
	 * @param parent
	 */
	public HmmPress (File hmmPressBin, File hmmDBFile, HmmerServicePanel parent) {
		this.hmmPressBin = hmmPressBin;
		this.hmmDBFile = hmmDBFile;
		this.name = "HMMPRESS";
		this.parentServicePanel = parent;
		this.parentPanel = null;
	}

	/**
	 * Constructor to be called from any JPanel
	 * @param hmmPressBin
	 * @param hmmDBFile
	 * @param parent
	 */
	public HmmPress (File hmmPressBin, File hmmDBFile, JPanel parent) {
		this.hmmPressBin = hmmPressBin;
		this.hmmDBFile = hmmDBFile;
		this.name = "HMMPRESS";
		this.parentPanel = parent;
		this.parentServicePanel = null;
	}
	
	/**
	 * Static method to determine whether the hmmfile
	 * has been pressed. Checks for files with a known
	 * extension in the parent dir of the hmmfile.
	 * 
	 * @param hmmDBFile
	 * @return
	 */
	public static boolean hmmFilePressed(File hmmDBFile) {
		if (new File(hmmDBFile+H3F).exists())
			if (new File(hmmDBFile+H3I).exists())
				if (new File(hmmDBFile+H3M).exists())
					if (new File(hmmDBFile+H3P).exists())
						return true;
		
		return false;
	}
	
	/**
	 * Set the hmmfile
	 * @param hmmDBFile
	 */
	public void setHmmDBFile(File hmmDBFile) {
		this.hmmDBFile = hmmDBFile;
	}
	
	/**
	 * Prepares the arguments in a String[] for the {@link Executor}
	 * Implementation required by {@link Hmmer3Program} interface.
	 */
	public void prepare() {
		args = new String[2];
		args[0] = hmmPressBin.getAbsolutePath();
		args[1] = hmmDBFile.getAbsolutePath();
	}
	
	/**
	 * Returns the arguments required to run hmmpress
	 * Implementation required by {@link Hmmer3Program} interface.
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * Parses the results returned from {@link Hmmer3Service} and
	 * {@link Executor}
	 * 
	 * Implementation required by {@link Hmmer3Program} interface.
	 * 
	 * TODO
	 * - Process communication sucks here big time
	 * - Clean up messages back to console 
	 * 
	 */
	public void parseResults() {
		System.out.println("hmmpress run successful.");
		if (parentPanel != null) {
			parentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			MessageUtil.showInformation("HMMERDB sucessfully pressed");
		}
		else if (parentServicePanel != null) {
			parentServicePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			parentServicePanel.getProgressBar().setIndeterminate(false);
			parentServicePanel.writeToConsole("=================================");
			parentServicePanel.writeToConsole(getName()+ " run successful.");
			MessageUtil.showInformation("HMMERDB sucessfully pressed");
		}
	}

	/**
	 * The invoking panel hmmerServicePanel. If not invoked
	 * by a hmmerServicePanel, but by a regular JPanel, use
	 * getParentPanel();
	 * Implementation required by {@link Hmmer3Program} interface.
	 */
	public HmmerServicePanel getParentServicePanel() {
		return this.parentServicePanel;
	}
	
	/**
	 * The invoking JPanel (not hmmerServicePanel)
	 */
	public JPanel getParentPanel() {
		return this.parentPanel;
	}

	/**
	 * The name of this program
	 * Implementation required by {@link Hmmer3Program} interface.
	 */
	public String getName() {
		return this.name;
	}

	
}
