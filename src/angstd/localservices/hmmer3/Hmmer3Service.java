package angstd.localservices.hmmer3;

import java.io.BufferedWriter;
import java.io.File;

import angstd.localservices.executor.Executor;
import angstd.localservices.executor.ProcessListener;
import angstd.localservices.executor.StreamHandler;
import angstd.localservices.hmmer3.programs.Hmmer3Program;
import angstd.localservices.hmmer3.ui.HmmerServicePanel;
import angstd.model.arrangement.io.HmmOutReader;

/**
 * A generic class for handeling a {@link Hmmer3Program}. Provides
 * methods to handel the execution of the program via
 * {@link Executor}
 * 
 * TODO
 * - implement start() (behaves exactly as startInBackground())
 * - proper console messages
 * - write output to STDOUT to a logfile
 * - consider implementing Hmmer3Program.validate() and use here
 * 
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class Hmmer3Service implements ProcessListener{
		
	protected String[] cmd;
	protected Executor executor;
		
	protected int completed, totalEntries;
	protected long duration, estimate;
		
	protected BufferedWriter STDOUT;
	protected File tmpOutput;
		
	protected String progPath, progName;
	protected String[] args;
	protected HmmOutReader parser;
		
	protected Hmmer3Program hmmerProgram;
	protected HmmerServicePanel hmmPanel;
	
	
	/**
	 * Constructor. Expects a {@link Hmmer3Program}
	 * @param program
	 */
	public Hmmer3Service(Hmmer3Program program) {
		this.hmmerProgram = program;
		this.hmmPanel = program.getParentServicePanel();
	}

	/**
	 * Stops the current service by a call
	 * to the {@link Executor}
	 */
	public void stop() {
		if (executor != null)
			executor.stop();
	}
		
	/**
	 * Method to determine whether there is a service 
	 * currently running
	 * @return
	 */
	public boolean isRunning() {
		return executor.isRunning();
	}
	
	/**
	 * Prepares the arguments for execution and 
	 * invokes the program by a call to {@link Executor}.
	 * This starts a worker thread and executes in the background,
	 * without waiting for results
	 * 
	 * TODO
	 * - consider using validation method
	 * 
	 */
	public void startInBackground() {
			
		hmmerProgram.prepare(); // prepare the argument for the Executor
		String[] cmd = hmmerProgram.getArgs(); // gets the arguments for the Executor
		
		try {		
			executor = new Executor(cmd, this);
			executor.execute();
			hmmPanel.writeToConsole("Running "+hmmerProgram.getName());
			hmmPanel.writeToConsole("=================================");
		} 
		catch(Exception e) {
			//e.printStackTrace();
			executor.stop();
		}
	}

	/**
	 * TODO
	 * !!currently not implemented!!
	 * -consider using validation
	 * 
	 * Prepares the arguments for execution and 
	 * invokes the program by a call to {@link Executor}.
	 * This method waits for the results before continuing.
	 * 
	 * 
	 */
	public int start() {
			
		hmmerProgram.prepare(); // prepare the argument for the Executor
		String[] cmd = hmmerProgram.getArgs(); // gets the arguments for the Executor
		int retValue = 0; 
		
		try {		
			executor = new Executor(cmd, this);
			executor.execute();
		} 
		catch(Exception e) {
			e.printStackTrace();
			executor.stop();
			return 1;
		}
		return retValue;
	}
	
	/**
	 * Handels error and std output 
	 */
	public void outputRecieved(String out, String type) {
		
		if (type.equals(StreamHandler.ERROR)) {
			System.out.println("ERROR>"+out);
			hmmPanel.writeToConsole("ERROR occurred while running Hmmer3Service: ");
			hmmPanel.writeToConsole(out);
			executor.stop();
			return;
		}
		
		try {
			// TODO: write this to logfile
			// instead of STDOUT
			System.out.println(out);
				
			// writing to the panel is _very_ slow
			//hmmPanel.writeToConsole(out);
			}
			catch(Exception e) {
				System.out.println("*** Problem handling output.");
			}
			
	}

	/**
	 * If the programs terminates normally (0)
	 * this method invokes the parser of the current
	 * program (called by executor.done() once the SwingWorker
	 * thread has terminated) 
	 */
	public void setResult(int res) {
		if (res == 0) {
			hmmerProgram.parseResults();
		}
		else
			System.out.println(hmmerProgram.getName() + " was closed or died unexpectedly.");	
	}

}
