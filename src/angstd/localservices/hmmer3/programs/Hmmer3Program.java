package angstd.localservices.hmmer3.programs;

import angstd.localservices.executor.Executor;
import angstd.localservices.hmmer3.ui.HmmerServicePanel;

/**
 * This interface describes a program from the hmmer3 package.
 * Currently, it is built only for hmmscan and hmmpress.
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public interface Hmmer3Program {
	
	/**
	 * Prepares the arguments for the executor.
	 * Sets up the arguments in a String[]
	 */
	public void prepare();
	
	/**
	 * Returns the Arguments of the program as
	 * String[] (required for {@link Executor}
	 * @return
	 */
	public String[] getArgs();
	
	/**
	 * Parse results produced by this program.
	 */
	public void parseResults();

	/**
	 * Call to obtain the command string passed to the executor 
	 * @return formatted string correpsonding to the command call
	 */
	public String getCommandCall();

	/**
	 * Method for getting the parent panel harboring
	 * the GUI elements for the current program
	 * @return
	 */
	public HmmerServicePanel getParentServicePanel();
	
	
	/**
	 * Gets the name of the current program
	 * @return name of program
	 */
	public String getName();
	
}
