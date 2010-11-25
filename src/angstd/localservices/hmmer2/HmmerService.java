package angstd.localservices.hmmer2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import angstd.localservices.executor.Executor;
import angstd.localservices.executor.ProcessListener;
import angstd.localservices.executor.StreamHandler;
import angstd.localservices.hmmer2.ui.HmmerServicePanel;
import angstd.model.arrangement.io.HmmOutReader;
import angstd.model.arrangement.io.XdomReader;
import angstd.ui.ViewHandler;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;


/**
/**
 * Hackalarm: Very rough HMMer wrapper.
 * TODO:
 * 
 * @author Andrew Moore
 *
 
public class HmmerService implements ProcessListener {
	
	protected String[] cmd;
	protected Executor executor;
	
	protected int completed, totalEntries;
	protected long duration, estimate;
	
	protected BufferedWriter STDOUT;
	protected File tmpOutput;
	
	protected String progPath, progName;
	protected String[] args;
	protected HmmOutReader parser;
	
	protected HmmerServicePanel hmmPanel;

	
	public HmmerService(Hmmer2ProgramType type, String programPath, File inputFile, String[] args) {
		this.progPath = programPath;
		this.progName = type.getName();
		this.hmmPanel = type.getPanel();
		this.args = args;
			
		totalEntries = countEntries(inputFile, type.getFileType());
		completed = 0;
	}
	
	/**
	 * Helper method counting the entries of the input file
	 * 
	 * @param input
	 * 		the input file
	 * @param type
	 * 		the file type e.g. fasta
	 * @return
	 * 		the entries contained by the specified file

	private int countEntries(File input, String type) {
		if (type.equals("FASTA")) {
			String line = new String();
			int entries = 0;
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(input));
				while((line = br.readLine())!= null) 
					if (line.substring(0, 1).equals(">")) 
						entries++;
				
				br.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			return entries;
		}
		return -1; // Unknown format
	}
	
	public void setResult(int exitVal) {
		// TODO process exitVal
		if (exitVal == 0) {
//			hmmPanel.setProgressType(false);
			
			System.out.println("Needed Time: "+executor.getEllapsedTime());
			try {
				STDOUT.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// parse the results
			parser = new HmmerParser(tmpOutput);
			parser.parse();
			String xdom = parser.toXdom(10);
			
			// create domain view
			DomainViewI view = ViewHandler.getInstance().createView(ViewType.DOMAINS, "hmmerTest");
			view.setDaSet(new XdomReader().getDataFromString(xdom));
			ViewHandler.getInstance().addView(view, null);
		
			System.out.println(parser.toXdom(1e-01));
			System.out.println("*** Output written to "+tmpOutput);
		}
		else {
			System.out.println("*** ERROR here.");
		}
	}

	public void stop() {
		if (executor != null)
			executor.stop();
	}
	
	public boolean isRunning() {
		return executor.isRunning();
	}
	
	public void start() {
		// prepare command
		cmd = new String[args.length+1];
		cmd[0] = progPath;
			
		for (int i = 0; i < args.length; i++) {
			System.out.println("Adding arg: "+args[i]);
			cmd[i+1] = args[i];
		}	
		
		try {
			tmpOutput	= File.createTempFile("hmmerJob_", ".hmmout");
			STDOUT = new BufferedWriter(new FileWriter(tmpOutput));
			
		
			completed = 0;
			
			// run hmmer
			executor = new Executor(cmd, this);
			executor.execute();
			hmmPanel.writeToConsole("STARTING "+progName);
			hmmPanel.writeToConsole("=================================");
			hmmPanel.setProgressType(true);
			
			
		} catch(Exception e) {
			e.printStackTrace();
			executor.stop();
		}
	}

	// handle the process output here
	/**
	 * For hmmpfam output, the number of results is equal to the number of fasta entries
	 * (ie sequences with no hits will still return a complete entry)
	 * 

	public void outputRecieved(String out, String type) {
		
		if (type.equals(StreamHandler.ERROR)) {
			System.out.println("ERROR>"+out);
			executor.stop();
			return;
		}
	
		try {
			STDOUT.write(out+System.getProperty("line.separator"));
			STDOUT.flush();
			
			if (HmmerParser.entrySep(out, "hmmpfam")) {
				completed++;
				
				if (completed < totalEntries) {
					hmmPanel.setProgressType(false);
					hmmPanel.updateProgress(0);
					int progress = (100/totalEntries)*completed;
					hmmPanel.updateProgress(progress);
				}
				else {
					int progress = (100/totalEntries)*completed;
					hmmPanel.updateProgress(progress);
				}
				
				
//				if (completed == 1) {
//					//TODO calculate job
//					duration = executor.getEllapsedTime();
//					estimate = totalEntries * duration;
//					
//					
//					//TODO set bar to progress
//					hmmPanel.setProgressType(false);
//					hmmPanel.updateProgress(0);
//					int progress = Math.round((100/estimate)*duration);
//					
//					//TODO update bar
//					
//				}
//				else if (completed < totalEntries) {
//					//TODO update progress
//					hmmPanel.writeToConsole("*** Completed: "+completed+" out of "+totalEntries);
//				}
//				else {
//					//TODO progress complete
//					hmmPanel.writeToConsole("*** Run completed ["+completed+" out of "+totalEntries+"]");
//				}
				
//				int duration = Math.round(executor.getEllapsedTime());
//				int completeEstimate = totalEntries * duration; // how long for whole job
//				int remainingEstimate = duration*remaining; // how long for remaining job
//				int jobDone = (100/completeEstimate) * remaining;
//				System.out.println("Duration of completed entry: "+duration+"[entries: "+completed+"], estimated time for complete job: "+remainingEstimate+"[remaining entires: "+remaining+"] " );
//				System.out.println("Estimated time for complete job: "+completeEstimate+"[actually required: ["+executor.getEllapsedTime() +"]");
			}
		} 
		catch( Exception e) {
			e.printStackTrace();
			executor.stop();
		}
		
		

		
//		if (pid.matcher(out).matches()) {
//			completed++;
//			System.out.println("*** COMPLETED: "+completed);
//			if (completed < totalEntries) {
//				int remaining = totalEntries - completed;
//				int todo = Math.round(100/totalEntries*(remaining));	
////				hmmPanel.updateProgress(todo);
//				System.out.println("Procent done: "+todo);
//				System.out.println("Line written: "+out);
//			}
			
	}
		

	
 	private void printEstimate() {
 		int remaining = totalEntries - completed;
 		int duration = Math.round((executor.getEllapsedTime()/completed) / 1000);
 		System.out.println("\r*** HMMER estimated runtime is another: "+(duration*remaining)+" seconds [remaing entires to scan: "+remaining+"]");

 	}
 	
}
 */