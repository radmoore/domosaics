package domosaics.localservices.hmmer3.programs;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JPanel;

import domosaics.localservices.executor.Executor;
import domosaics.localservices.hmmer3.Hmmer3Engine;
import domosaics.localservices.hmmer3.Hmmer3Service;
import domosaics.localservices.hmmer3.ui.HmmerServicePanel;
import domosaics.model.configuration.Configuration;
import domosaics.ui.util.MessageUtil;




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
	 * Crude check whether the passed file is a valid
	 * file with hmmer3 profiles
	 * @param hmmDBFile
	 * @return
	 */
	public static boolean isValidProfileFile(File hmmDBFile) {
		
        BufferedReader inputStream = null;
        String line;
        boolean startProfile = false;

        try {
            inputStream = new BufferedReader(new FileReader(hmmDBFile));
            
            while ((line = inputStream.readLine()) != null) {
				
            	if (line.isEmpty())					
					continue;
				// start of a profile
				if ((!startProfile) && line.startsWith("HMMER3")) {
					startProfile = true;
				}
				// end of a profile
				if (startProfile && line.matches("//")) {
					inputStream.close();
					return true;
				}
            }
		
        }
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
		
		return false;
	}
	
	
	/**
	 * Static method to determine whether the hmmfile
	 * has been pressed. Checks for files with a known
	 * extension in the dir with the hmmdbfile.
	 * 
	 * @param hmmDBFile
	 * @return
	 * 		true if all pressed files (by extension) are present
	 */
	public static boolean hmmFilePressed(File hmmDBFile) {
		if (new File(hmmDBFile+H3F).exists() && (new File(hmmDBFile+H3F).length())!=0)
			if (new File(hmmDBFile+H3I).exists() && (new File(hmmDBFile+H3I).length())!=0)
				if (new File(hmmDBFile+H3M).exists() && (new File(hmmDBFile+H3M).length())!=0)
					if (new File(hmmDBFile+H3P).exists() && (new File(hmmDBFile+H3P).length())!=0)
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
		args = new String[3];
		args[0] = hmmPressBin.getAbsolutePath();
		// Force overwriting
		args[1] = "-f";
		args[2] = hmmDBFile.getAbsolutePath();
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
		System.out.println("*** I: hmmpress run successful.");
		if (parentPanel != null) {
			parentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			MessageUtil.showInformation(null, "HMMERDB sucessfully pressed");
		}
		else if (parentServicePanel != null) {
			parentServicePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			parentServicePanel.setIndetermindateProgressBar(false);
			parentServicePanel.writeToConsole("*** I: "+getName()+ " run successful.\n");
			MessageUtil.showInformation(parentServicePanel.getParent(), "HMMERDB sucessfully pressed");
		}
		parentServicePanel.resetPanel();
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

	public String getCommandCall() {
		StringBuffer commandString = new StringBuffer();
		for (String arg: args)
			commandString.append(arg+" ");
		
		return commandString.toString();
	}
	
	/**
	 * The name of this program
	 * Implementation required by {@link Hmmer3Program} interface.
	 */
	public String getName() {
		return this.name;
	}

	
}
