package angstd.localservices.hmmer3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import angstd.localservices.executor.Executor;
import angstd.localservices.executor.ProcessListener;
import angstd.localservices.executor.StreamHandler;
import angstd.localservices.hmmer3.programs.Hmmer3Program;
import angstd.localservices.hmmer3.ui.HmmerServicePanel;
import angstd.model.arrangement.io.HmmOutReader;
import angstd.model.configuration.Configuration;
import angstd.model.workspace.ProjectElement;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;

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
	protected File tmpOutput, logFile;
		
	protected String progPath, progName;
	protected String[] args;
	protected HmmOutReader parser;
		
	protected Hmmer3Program hmmerProgram;
	protected HmmerServicePanel hmmPanel;
	
	protected Date startTime;
	
	protected Configuration config;
	protected ProjectElement currentProject;
	
	protected BufferedWriter logFileWriter;
	
	
	/**
	 * Constructor. Expects a {@link Hmmer3Program}
	 * @param program
	 */
	public Hmmer3Service(Hmmer3Program program) {
		this.hmmerProgram = program;
		this.hmmPanel = program.getParentServicePanel();
		currentProject = WorkspaceManager.getInstance().getSelectionManager().getSelectedProject();
		config = Configuration.getInstance();
		
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
	
	
	public File getLogFile() {
		return this.logFile;
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
			
		hmmerProgram.prepare(); 				// prepare the argument for the Executor
		String[] cmd = hmmerProgram.getArgs(); 	// gets the arguments for the Executor
		createLogFileWriter(new Date()); 		// creates a log file with a writer
		
		try {		
			executor = new Executor(cmd, this);
			logFileWriter.write("##################################\n");
			logFileWriter.write("## Run triggered by AnGSTD\n");
			logFileWriter.write("## Command: "+hmmerProgram.getCommandCall()+"\n");
			logFileWriter.write("##################################"+"\n");
			logFileWriter.write("\n");
			logFileWriter.write("\n");
			executor.execute();
			hmmPanel.writeToConsole("*** I: Started "+hmmerProgram.getName()+" run\n");
		} 
		catch(Exception e) {
			Configuration.getLogger().debug(e.toString());
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
			
		hmmerProgram.prepare(); 				// prepare the argument for the Executor
		String[] cmd = hmmerProgram.getArgs(); 	// gets the arguments for the Executor
		createLogFileWriter(new Date()); 		// creates a log file with a writer
		int retValue = 0; 
		
		try {
			logFileWriter.write("##################################\n");
			logFileWriter.write("## Run triggered by AnGSTD\n");
			logFileWriter.write("## Command: "+hmmerProgram.getCommandCall()+"\n");
			logFileWriter.write("##################################"+"\n");
			logFileWriter.write("\n");
			logFileWriter.write("\n");
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
			System.out.println("*** E: "+out);
			//hmmPanel.writeToConsole("*** E: occurred while running Hmmer3Service: ");
			hmmPanel.writeToConsole("*** E: "+ out);
			hmmPanel.writeToConsole("\n");
			executor.stop();
			return;
		}
		try {
			logFileWriter.write(out+"\n");
		    System.out.println(out);
		}
		catch(Exception e) {
			System.out.println("*** E: Problem handling output.");
			e.printStackTrace();
		}
			
	}

	/**
	 * If the programs terminates normally (0)
	 * this method invokes the parser of the current
	 * program (called by executor.done() once the SwingWorker
	 * thread has terminated) 
	 */
	public void setResult(int res) {
		
		try {		
			logFileWriter.flush();
			logFileWriter.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (res == 0) {
			hmmerProgram.parseResults();
		}
		else {
			MessageUtil.showWarning(hmmPanel, hmmerProgram.getName()+" was killed or died unexpectedly.");
			hmmPanel.resetPanel();
			System.out.println(hmmerProgram.getName() + " was closed or died unexpectedly.");
		}
		
	}

	
	private void createLogFileWriter(Date startTime) {
		File logFile = null;
		BufferedWriter writer = null;
		
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyMMddHHmmssZ");
		StringBuilder dateString = new StringBuilder( dateformatYYYYMMDD.format( startTime ) );

		String workspace = config.getWorkspaceDir();
		String logDir = workspace+"/logs";
		String projectDir = logDir+"/"+currentProject.getTitle();
		
		try {
			if (!new File(logDir).exists())
				new File(logDir).mkdir();
			
			if (!new File(projectDir).exists())
				new File(projectDir).mkdir();
		
			logFile = new File(projectDir+"/"+hmmerProgram.getName()+"_"+dateString+".log");
			
			FileWriter fstream = new FileWriter(logFile.getAbsolutePath());
	        writer = new BufferedWriter(fstream);
			
		} catch(Exception e) {
			System.out.println("*** E: Problem creating log file: ");
			e.printStackTrace();
		}
		System.out.println("*** I: log file of run: "+logFile.getAbsolutePath());
		this.logFile = logFile;
		logFileWriter = writer;
	
	}
	
	
	
}
