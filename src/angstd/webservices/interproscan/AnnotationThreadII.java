package angstd.webservices.interproscan;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import uk.ac.ebi.webservices.axis1.*;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.InputParameters;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.JDispatcherService_PortType;
import uk.ac.ebi.webservices.axis1.stubs.iprscan.WsResultType;

import angstd.model.sequence.SequenceI;
import angstd.util.StringUtils;

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
public class AnnotationThreadII extends SwingWorker<String, Void> {

	/** input parameters needed for a query against InterPro, such as email address */
	protected InputParameters params;
	private JDispatcherService_PortType srvProxy = null;
	
	/**	query sequence */
	//protected Data[] content;
	
	/**	output of WSInterproScan for the query sequence */
	protected String result, email, fasta;
	
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
	public AnnotationThreadII(AnnotationThreadSpawner spawner) {
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
		this.fasta = ">"+seq.getName()+"\n"+seq.getSeq(false);
		
		// The input data
        //content = new Data[1];
//        content[0] = new Data();
//        content[0].setType("sequence");
//        content[0].setContent(">"+seq.getName()+"\n"+seq.getSeq(false));
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
		params = new InputParameters();
//		params.setEmail(email);                 // User e-mail address
//		params.setAsync(new Boolean(true));     // Async submission
//		params.setSeqtype("P");                 // Protein input sequence
//		params.setCrc(new Boolean(true));       // Use IprMatches lookup
//		params.setGoterms(true);
//		params.setOutformat("toolraw");
		
		params.setNocrc(true);
		params.setGoterms(true);
		String[] methods2 = new String[1];
		methods2[0] = "hmmpfam";
		
		params.setAppl(methods2);
		this.email = email;
		
        /* A space separated list of InterPro signature methods to run.
         * Valid method names are:
         * blastprodom, gene3d, hmmpanther, hmmpir, hmmpfam, hmmsmart,
         * signalp, tmhmm, hmmtigr, fprintscan, scanregexp, profilescan,
         * superfamily
         */
		//params.setApp(methods);
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
//			WSInterProScanService service = new WSInterProScanServiceLocator();
//			WSInterProScan interProScan = service.getWSInterProScan();
//	        String jobId = interProScan.runInterProScan(params, content);
			System.out.println("Calling method: ");
			params.setSequence(fasta);
			String jobId = srvProxy.run(email, "test run", params);
			String status = srvProxy.getStatus(jobId);
	            
	        // Check status and wait if not finished
	        while(status.equals("RUNNING")) {
	        	System.out.println("Waiting for result... ");
        		Thread.sleep(1000); // Wait before polling again.
	        	status = srvProxy.getStatus(jobId);
	        }
	       
	        if (status.equals("FINISHED")) {
	        	WsResultType[] resType = srvProxy.getResultTypes(jobId);
	        	System.out.print("This is the res type length: "+resType.length);
	        }
	        else if (status.equals("ERROR")) {
	        	System.out.println("*** E: an error occurred attempting to get the job status [jobid: "+ jobId +"]");
			}
	        else if (status.equals("FAILURE")) {
	        	System.out.println("*** E: The job failed [jobid: "+ jobId +"]");
			}
	        else if (status.equals("NOT_FOUND")) {
	        	System.out.println("*** E: job not found [jobid: "+ jobId +"]");
	        	
	        }
	        	
	        
	        
	        // Get results
//	        byte[] res;
//	        res = interProScan.poll(jobId, "toolraw");
//	        
//	        if (res == null)
//	        	return null;
	        
	       result = new String("some res");
	       return result;
	       
		} catch (Exception e) {
			//System.out.println("->Job "+seq.getName()+" cancelled<-");
		} 
		return null;
    }
	
	/**
	 * SwingWorker method which is triggered when the annotation process 
	 * ended.
	 */
	 @Override
     protected void done() {
		if(!isCancelled())
		{
			//System.out.println("->Job "+seq.getName()+" finished<-");
			try {
				spawner.processResults(this, get());
			}
			catch (InterruptedException e) {
				System.out.println("Interrupted.");
				e.printStackTrace();
			}
			catch (ExecutionException e) {
				System.out.println("Some other executaion exception.");
     		e.printStackTrace();
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
    
}
