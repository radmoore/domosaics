package domosaics.webservices;

import java.rmi.RemoteException;

import javax.swing.SwingWorker;
import javax.xml.rpc.ServiceException;

import domosaics.model.configuration.Configuration;




public abstract class AbstractEBIWebservice extends SwingWorker<String, Void> implements WebservicePrinter {

	/** the services name, very important for displaying messages **/
	protected String serviceName;
	
	/** the output format type of the service, used to get the results from it  **/
	protected String outFormat;
	
	/** output stream, this can be command line or for instance a console within a panel **/
	protected WebservicePrinter out;
	
	/** the job id used to check the query status and getting results etc. */
	protected String jobId;
	
	/** the result before parsing from the webservice */
	protected String result;
	
	protected boolean jobDone;
	

	/* ********************************************************************* *
	 * 								Constructors							 *
	 * ********************************************************************* */
	
	/**
	 * Basic constructor which takes the standard output as 
	 * console for messages.
	 * 
	 * @param type
	 * 		the webservice type, e.g. INTERPROSCAN
	 */
	public AbstractEBIWebservice(EBIServiceType type) {
		this(type, null);
	}
	
	/**
	 * Constructor with a defined WebservicePrinter. This can for instance
	 * be a panel implementing this interface and therefore being able
	 * to print the output to a console (text area).
	 * 
	 * @param type
	 * 		the webservice type, e.g. INTERPROSCAN
	 * @param out
	 * 		implementing class of the WebservicePrinter interface
	 */
	public AbstractEBIWebservice(EBIServiceType type, WebservicePrinter out) {
		this.out = (out == null) ? this : out;

		serviceName = type.getServiceName();
		outFormat = type.getOutFormat();
	}
	
	/* ********************************************************************* *
	 * 	Abstract methods which have to be implementented by the services 	 *
	 * ********************************************************************* */
	
	protected abstract String processResults() throws RemoteException;
	
	protected abstract void createService() throws ServiceException;
	
	protected abstract String startService() throws RemoteException;
	
	protected abstract String getStatus() throws RemoteException;
	
	/* ********************************************************************* *
	 * 							Swingworker Methods							 *
	 * ********************************************************************* */
	
	@Override
    protected String doInBackground() {
		try {
			out.print("Starting "+serviceName+" service... \n");
		
			createService();
			
        	jobId = startService();
        	
        	// Check status and wait if not finished
        	String status = "PENDING";
        	while(status.equals("RUNNING") || status.equals("PENDING")) {
        		status = getStatus();
        		Thread.sleep(2000);  // Wait before polling again.
        	
        		if(status.equals("RUNNING") || status.equals("PENDING")) 
        			out.print("Please wait for "+serviceName+" to finish... \n");
        	}
        
        	if (status.equals("ERROR")) {
        		out.print("An unknown error occured within "+serviceName+" \n");
        		out.print("Job cancelled \n");
        		cancel();
        		return null;
        	} 
	        
	        // process results
	        result = processResults();
	        
	        out.print(serviceName+" job was successfull! \n");
	        return result;
		} 
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		} 
		return null;
    }
	
	@Override
    protected void done() {
		if (isCancelled()) {
			out.print(serviceName+" cancelled. \n");
     		return;
		}
		out.setJobDone(true);
    }
	 
	public void cancel() {
		 cancel(true);
	}
	
	public boolean isRunning() {
		return getState().equals(SwingWorker.StateValue.STARTED);
	}
	
	/* ********************************************************************* *
	 * 							WebservicePrinter methods					 *
	 * ********************************************************************* */

	public void print(String msg) {
		System.out.println(msg);
	}
	
	public void setJobDone(boolean state) {
		jobDone = state;
	}
	
	public boolean isJobDone() {
		return jobDone;
	}
	
	public String getResult() {
		return result;
	}
}
