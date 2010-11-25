package angstd.webservices.interproscan;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import uk.ac.ebi.webservices.wsinterproscan.Data;
import uk.ac.ebi.webservices.wsinterproscan.InputParams;
import uk.ac.ebi.webservices.wsinterproscan.WSInterProScan;
import uk.ac.ebi.webservices.wsinterproscan.WSInterProScanService;
import uk.ac.ebi.webservices.wsinterproscan.WSInterProScanServiceLocator;
import angstd.model.sequence.SequenceI;

/**
 * AnnotationThread is the interface between the WSInterproScan library 
 * and AnGSTD. 
 * <p>
 * Annotation threads are created by a {@link AnnotationThreadSpawner}. <br>
 * A valid email address and the signature method must be set to run the 
 * annotation thread successfully.
 * <p>
 * The result is the output of WSInterproScan in raw format.
 * 
 * @author Andreas Held, Andrew Moore
 *
 */
public class AnnotationThread extends SwingWorker<String, Void> {

	/** input parameters needed for a query against InterPro, such as email address */
	protected InputParams params;
	
	/**	query sequence */
	protected Data[] content;
	
	/**	output of WSInterproScan for the query sequence */
	protected String result;
	
	/** the query sequence */
	protected SequenceI seq;		
	
	/** spawner object which created the annotation thread */
	protected AnnotationThreadSpawner spawner;
	
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
		
		// The input data
        content = new Data[1];
        content[0] = new Data();
        content[0].setType("sequence");
        content[0].setContent(">"+seq.getName()+"\n"+seq.getSeq(false));
	}
	
	/**
	 * Sets the input parameters, which are needed for a query against InterPro.
	 * 
	 * @param email
	 * 		a valid email address
	 * @param methods
	 * 		an annotation method, e.g. hmmpfam
	 */
	public void setParams(String email, String methods) {
		params = new InputParams();
		params.setEmail(email);                 // User e-mail address
		params.setAsync(new Boolean(true));     // Async submission
		params.setSeqtype("P");                 // Protein input sequence
		params.setCrc(new Boolean(true));       // Use IprMatches lookup
		params.setGoterms(false);
		params.setOutformat("toolraw");
		
        /* A space separated list of InterPro signature methods to run.
         * Valid method names are:
         * blastprodom, gene3d, hmmpanther, hmmpir, hmmpfam, hmmsmart,
         * signalp, tmhmm, hmmtigr, fprintscan, scanregexp, profilescan,
         * superfamily
         */
		params.setApp(methods);
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
			WSInterProScanService service = new WSInterProScanServiceLocator();
			WSInterProScan interProScan = service.getWSInterProScan();
	        String jobId = interProScan.runInterProScan(params, content);
	        
	        String status = "PENDING";
	            
	        // Check status and wait if not finished
	        while(status.equals("RUNNING") || status.equals("PENDING")) {
	        	status = interProScan.checkStatus(jobId);

	        	if(status.equals("RUNNING") || status.equals("PENDING")) {
	        		Thread.sleep(2000); // Wait before polling again.
	        	}
	        }
	       
	        // Get results
	        byte[] res;
	        res = interProScan.poll(jobId, "toolraw");
	        
	        if (res == null)
	        	return null;
	        
	       result = new String(res);
	       return result;
	       
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
    }
	
	/**
	 * SwingWorker method which is triggered when the annotation process 
	 * ended.
	 */
	 @Override
	 // Check here for cancelation error (ADM)
     protected void done() {
     	if (isCancelled()) {
     		return;
     	}
		try {
			spawner.processResults(this, get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
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
    
}
