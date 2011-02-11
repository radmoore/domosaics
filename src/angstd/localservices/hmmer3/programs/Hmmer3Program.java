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
	 * 
	 * TODO: to ensure design consistency,
	 * we should probably enfore the return of 
	 * String[] as that is what the executor
	 * expects. 
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
	 * Not always applicable.
	 */
	public void parseResults();
	

	/**
	 * Method for getting the parent panel harboring
	 * the GUI elements for the current program
	 * @return
	 */
	public HmmerServicePanel getParentPanel();
	
	
	/**
	 * Gets the name of the current program
	 * @return name of program
	 */
	public String getName();
	
}
