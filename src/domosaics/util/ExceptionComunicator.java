package domosaics.util;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.SwingWorker;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.BugzillaConnectionException;
import com.j2bugzilla.rpc.LogIn;
import com.j2bugzilla.rpc.ReportBug;

import domosaics.model.configuration.Configuration;

/**
 * The ExceptionComunicator is used for submitting
 * exceptions to a preconfigured BUGZILLA instance. 
 * 
 * @author <a href="http://radm.info">Andrew D. Moore</a>
 *
 */
public class ExceptionComunicator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String reportUrl = "http://iebservices.uni-muenster.de/bugzilla";
	private final String reportUser = "domosaics@uni-muenster.de";
	private final String reportPass = "pass4domosaics;";
	private final int queueLength = 0;
	
	
	private static ExceptionComunicator instance = null;
	private static boolean reportToConsole = false;
	
	private BugzillaConnector bugCon = null;
	private Exception exception;
	private ArrayList<Bug> reportedBugs = new ArrayList<Bug>(); 
	private boolean isSending = false;
	

	
	/**
	 * 
	 * @return instance of the exception communicator
	 */
	public static ExceptionComunicator getInstance() {
		if (instance == null)
			instance = new ExceptionComunicator();
		return instance;
	}
	
	
	public static void setReportToConsole(boolean report) {
		reportToConsole = report;
	}
	
	
	/**
	 * Get the connector used to communicate with the
	 * BUGZILLA instance
	 * 
	 * @return the BUGZILLA connected BugzillaConnector 
	 */
	public BugzillaConnector getBugzillaConnector() {
		return bugCon;
	}
	
	
	/**
	 * reports a bug. If send is true, bug will be immediately
	 * submitted. Else, the bug will be appended to a running buglist
	 * and submitted latest upon program exit.
	 * 
	 * @param e - the exception
	 */
	public void reportBug(Exception e) {
		exception = e;
		Bug bug = createBug();
		if (reportedBugs.size() >= queueLength) {
			while (isSending)
				delaySending(500);
				
			sendBugs();
			Configuration.getLogger().debug("Sending exception message");
		}
		reportedBugs.add(bug);
		
		// report to console, if requested
		if ( reportToConsole )
			e.printStackTrace();
		
		// report to log file no matter what
		Configuration.getLogger().debug(e.getStackTrace());
	}
	
	
	/**
	 * Sends all bugs that have been reported
	 */
	public void sendBugs(){
		
		isSending = false;
		connect();
		
		SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
			protected Integer doInBackground() throws Exception {
				isSending = true;
				int sent = 0;
				for (Bug b : reportedBugs) {
					ReportBug report = new ReportBug(b);
					try {
						bugCon.executeMethod(report);
						sent ++;
					} 
					catch (BugzillaException e) {
						reportedBugs.add(b);
					}
				}
				return sent;	
			}

			public void done() {
				reportedBugs = new ArrayList<Bug>();
				isSending = false;
			}
		};
		worker.execute();
	}
	
	
	/*
	 * Private constructor: singleton pattern
	 */
	private ExceptionComunicator() {
		bugCon = new BugzillaConnector();
	}

	
	/*
	 * Connects to the remote BUGZILLA instance (via RPC/XML)
	 */
	private void connect() {
		
		try {
//			System.out.println("Trying to connect!");
			bugCon.connectTo(reportUrl, reportUser, reportPass);
//			System.out.println("Ran connectTo. Trying to login");
			LogIn logIn = new LogIn(reportUser, reportPass);
//			System.out.println("Created new login!");
			bugCon.executeMethod(logIn);			
//			System.out.println("Excuted login");
		}
		catch (BugzillaConnectionException ce) {
			System.out.println("Connection exception FAILED!");
		}
		catch (Exception e) {
//			System.out.println("FAILED!");
			Configuration.getLogger().debug(e.toString());
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Creates a bug based on the reported exception
	 */
	private Bug createBug() {
		
        String issue = exception.toString() + " ["+ System.getProperty("os.name") +"]";
        String systemParams = formatMessage();
		
		BugFactory factory = new BugFactory();
		Bug bug = factory.newBug()
		    .setOperatingSystem(System.getProperty("os.name"))
		    .setPlatform("PC")
		    .setProduct("DoMosaics")
		    .setComponent("autosubmission")
		    .setSummary(issue)
		    .setVersion("RC")
		    .setDescription(systemParams)
		    .createBug();

		return bug;
	}
	
	
	/*
	 * Creates a formatted message for use as the
	 * bug description on submission
	 */
	private String formatMessage() {
		
		StringBuffer systemParams = new StringBuffer();

        systemParams.append("\nEXCEPTION DETAILS: \n");
		systemParams.append("=================================\n");
		systemParams.append(ExceptionUtils.getStackTrace(exception));
		systemParams.append("\n");
		systemParams.append("SYSTEM PARAMETERS: \n");
		systemParams.append("=================================\n");
        for ( Map.Entry<Object, Object> entry : System.getProperties().entrySet() )
            systemParams.append(entry+"\n\n");
		
        if ( reportToConsole )
        	Configuration.getLogger().debug(systemParams.toString());
		return systemParams.toString();
	}
	
	
	private void delaySending(long ms) {
		try {
			Thread.sleep(ms);
		} 
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
	}
}
