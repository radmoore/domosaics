package domosaics.webservices.interproscan;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;

import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;



import uk.ac.ebi.webservices.axis1.stubs.iprscan.InputParameters;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.JDispatcherService_PortType;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.JDispatcherService_Service;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.JDispatcherService_ServiceLocator;


/**
 * AnnotationThread is the interface between the iprscan API 
 * and AnGSTD.
 * (see http://www.ebi.ac.uk/Tools/webservices/services/pfa/iprscan_soap#getresult_jobid_type_parameters)
 * <p>
 * Annotation threads are created by a {@link AnnotationThreadSpawner}. <br>
 * A valid email address and method must be set to run the 
 * annotation thread successfully.
 * <p>
 * The result is the raw output, see
 * (see http://www.ebi.ac.uk/Tools/webservices/services/pfa/iprscan_soap#getresult_jobid_type_parameters)
 * 
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * @author Andreas Held
 * 
 *
 */
public class AnnotationThread extends SwingWorker<String, Void> {

	private InputParameters params;
	private JDispatcherService_PortType srvProxy = null;
	private String result, email, fasta;
	private SequenceI seq;		
	private AnnotationThreadSpawner spawner;
	
	/**
	 * Constructor for an annotation against InterPro.
	 * 
	 * @param spawner
	 * 		spawner object which created this annotation thread
	 */
	public AnnotationThread(AnnotationThreadSpawner spawner) {
		this.spawner = spawner;
	}
	
	/**
	 * Returns the query sequence of the annotation process
	 * 
	 * @return
	 * 		query sequence
	 */
	public SequenceI getQuerySequence() {
		return seq;
	}
	
	/**
	 * Sets the query sequence for the annotation run
	 * 
	 * @param seq
	 * 		query sequence
	 */
	public void setQuerySequence(SequenceI seq) {
		this.seq = seq;
		// get fasta without gaps
		this.fasta = seq.toFasta(false);
	}
	
	/**
	 * Sets the input parameters, which are needed for a query against InterPro.
	 * 
	 * @param email
	 * 		a valid email address
	 * @param methods
	 * 		an annotation method, e.g. hmmpfam
	 */
	public void setParams(String email, String methodname) {
		this.email = email;
		params = new InputParameters();
		params.setNocrc(true);
		params.setGoterms(true);
		String[] methods = new String[1];
		methods[0] = methodname;
		params.setAppl(methods);
	}
	
	/**
	 * SwingWorker method which defines the annotation process. 
	 * Uses the WSInterProScanService library to make a query against InterPro.
	 * 
	 * @return
	 * 		the annotation result for the sequence
	 */
	@Override
    protected String doInBackground() {
		try {
			
			params.setSequence(fasta);
			if (Configuration.isDebug())
				System.out.println("*** Submitting search for\n"+fasta+"\n");
			srvProxyConnect();
			String jobId = srvProxy.run(email, seq.getName(), params);
			String status = srvProxy.getStatus(jobId);
			spawner.out.print("Starting scan [ JOBID " + jobId +" ]\n");
			Configuration.getInstance().setServiceRunning(true);
			
	        while(status.equals("RUNNING")) {
	        	Thread.sleep(1000);
	        	status = srvProxy.getStatus(jobId);
	        	if (status.equals("RUNNING"))
	        		spawner.out.print("waiting for results... \n");
	        }
	       
	        if (status.equals("ERROR")) {
	        	spawner.out.print("[ ERROR occurred when obtaining job status for job " + jobId +" ]\n");
	        	if (Configuration.isDebug())
	        		System.out.println("*** an error occurred attempting to get the job status [jobid: "+ jobId +"]");
	        	return null;
			}
	        else if (status.equals("FAILURE")) {
	        	spawner.out.print("[ ERROR job " + jobId +"  failed ]\n");
				if (Configuration.isDebug())
					System.out.println("*** The job failed [jobid: "+ jobId +"]");
	        	return null;
			}
	        else if (status.equals("NOT_FOUND")) {
	        	spawner.out.print("[ NOT_FOUND could not find job with id " + jobId +" ]\n");
	        	if (Configuration.isDebug())
	        		System.out.println("*** job not found [jobid: "+ jobId +"]");
	        	return null;
	        }  
	        
	        // AXIS exception can be thrown here
	        byte[] resultBytes = srvProxy.getResult(jobId, "out", null);
	        return (resultBytes == null) ? null : new String(resultBytes);
	        
	       
		}
		// axis fault caught, but not handled 
		catch (AxisFault af) {
			Configuration.getLogger().debug(af.toString());
		}
		catch (InterruptedException ie){
			Configuration.getLogger().debug(ie.toString());
		}
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}

		return null;
    }
	
	// cancel and close connection
	protected void purgeAllJobs() {
		if (srvProxy != null) {
			this.cancel(true);
			srvProxy = null;
			Configuration.getInstance().setServiceRunning(false);
		}
	}
	
	
	/**
	 * SwingWorker method which is triggered when the annotation process 
	 * ended.
	 */
	 @Override
     protected void done() {
		 Configuration.getInstance().setServiceRunning(false);
		if(!isCancelled()) {
			try {
				spawner.processResults(this, get());
			}
			catch (InterruptedException e) {
				Configuration.getLogger().debug(e.toString());
			}
			catch (ExecutionException e) {
				Configuration.getLogger().debug(e.toString());
			}
		}
     }
	

	 /**
	  * Return the query result for the input sequence.
	  *  
	  * @return
	  * 	annotation result
	  */
    public String getResult() {
    	return result;
    }
    
    
    /**
     * Ensure that there is a connection to the service proxy
     * @throws ServiceException
     */
	protected void srvProxyConnect() throws ServiceException {
		if (this.srvProxy == null) {
			JDispatcherService_Service service = new JDispatcherService_ServiceLocator();
			this.srvProxy = service.getJDispatcherServiceHttpPort();
		}
	}
    
}