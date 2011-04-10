package angstd.webservices.interproscan;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import angstd.model.arrangement.ArrangementManager;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.sequence.SequenceI;

/**
 * AnnotationThreadSpawner spawns for a set of query sequences 
 * {@link AnnotationThread_old}s to annotate the sequences against InterPro using
 * a signature method, such as hmmpfam.
 * <p>
 * The thread spawner can be used multithreaded, creating up to 25 threads at a time
 * or single threaded.
 * <p>
 * A valid email address must be set to run WSInterproScan successfully.
 * 
 * @author Andreas Held
 *
 */
public class AnnotationThreadSpawner {

	/** query sequences */
	protected SequenceI[] seqs;
	
	/** Chosen signature method for annotation, e.g. hmmpfam */
	protected String method;
	
	/** valid email address used for WSInterproScan */
	protected String email;
	
	/** object which prints the process information */
	protected AnnotatorProcessWriter out;
	
	/** list of all annotation threads */
	//protected ArrayList<AnnotationThread> activeQuerys;
	protected ArrayList<AnnotationThread> activeQuerys;
	
	/** list of all annotation threads */
	protected SwingWorker<String, Void> jobLauncher;
	
	/** number of finished sequences */
	protected int seqsWaitingForAnnotation;
	
	/** manager holding the resulting annotated arrangements */
	protected ArrangementManager daSet = new ArrangementManager();
	
	
	/**
	 * Constructor for a new AnnotationThreadSpawner
	 * 
	 * @param out
	 * 		the object which shall be used for printing messages about the 
	 * 		annotation process
	 */
	public AnnotationThreadSpawner(AnnotatorProcessWriter out) {
		//activeQuerys = new ArrayList<AnnotationThread>();
		activeQuerys = new ArrayList<AnnotationThread>();
		this.out = out;
	}
	
	/**
	 * All annotated domain arrangement objects associated 
	 * with their query sequence.
	 * 
	 * @return
	 * 		manager holding all annotated arrangement objects
	 */
	public ArrangementManager getResult() {
		return daSet;
	}
	
	/**
	 * Indicates whether or not annotation threads are still active and running.
	 * 
	 * @return
	 * 		whether or not annotation threads are still running.
	 */
	public boolean isRunning() {
			return activeQuerys.size()!=0;
	}
	
	/**
	 * Sets the email address for the annotation process
	 * 
	 * @param email
	 * 		a valid email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Sets the signature method which shall be used for annotation.
	 * 
	 * @param method
	 * 		signature method for annotation, e.g. hmmpfam
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * Sets the query sequences. For each sequence an annotation thread will be spawned.
	 * 
	 * @param seqs
	 * 		all query sequences
	 */
	public void setSeqs(SequenceI[] seqs) {
		this.seqs = seqs;
	}
	
	/**
	 * Returns all query sequences
	 * 
	 * @return
	 * 		all query sequences
	 */
	public SequenceI[] getSeqs() {
		return seqs;
	}
	
	/**
	 * Cancel the jobLauncher
	 * 
	 */
	public void cancel() {
		jobLauncher.cancel(true);
	}
	
	/**
	 * Spawns a new annotation thread with the parameters set.
	 * 
	 * @param seq
	 * 		the query sequence to be annotated
	 */
	protected void spawnAnnotation(SequenceI seq) {
		//AnnotationThread annotator = new AnnotationThreadII(this);
		AnnotationThread annotator = new AnnotationThread(this);
		annotator.setParams(email, method);
		annotator.setQuerySequence(seq);
		activeQuerys.add(annotator);
		annotator.execute();
	}
	
	/**
	 * Until the spawner thread waits for the annotations to finish, this method
	 * is used to set the spawner thread to sleep for a specified duration.
	 * 
	 * @param ms
	 * 		duration of thread sleeping
	 */
	protected void sleep (long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			System.out.println("->cancelled<-");
		}
	}

	/**
	 * Processes the results of an annotation thread.
	 * Therefore the WSInterproResult is parsed into an domain arrangement object
	 * and associated with the query sequence.
	 * <br>
	 * The number of active threads is decreased.
	 * 
	 * @param annotator 
	 * 		the annotation thread which finished
	 * @param res
	 * 		the result of the annotation thread
	 */
	//public void processResults(AnnotationThread annotator, String res) {
	public void processResults(AnnotationThread annotator, String res) {

		if (!(res == null) ) {	
			DomainArrangement da = new InterProScanResultParser().parseResult(res);
			SequenceI seq = annotator.getQuerySequence();
			da.setSequence(seq);
			da.setName(seq.getName());
			da.setSeqLen(seq.getLen(true));
			daSet.add(da);
			out.print(da.getName() + " annotated. \n");
		}
		else {
			//System.out.println("*** I: No hits found.");
			out.print(annotator.getQuerySequence().getName()+": no hits found.\n");
		}
		activeQuerys.remove(annotator);
		
		// update progressbar
		int progress = (int) Math.round( (100/(double)getSeqs().length)*(seqsWaitingForAnnotation-activeQuerys.size()));
		out.updateProgress(Math.min(5+progress, 105));
		
		if(jobLauncher.isDone()) {
			if(activeQuerys.size()==0)
				out.print("---------------------------------\nAll sequences annotated, click apply to create view of results.");
			else {
				out.print("Wait for results (last " + activeQuerys.size() + " job");
				if (seqsWaitingForAnnotation != getSeqs().length)
					out.print("s");
				out.print(" running)... \n");
			}
		}
	}
	

	public void startMultipleThreadSpawn(){
		
		jobLauncher = new SwingWorker<String, Void>() {
			public String doInBackground() {
				try {
					seqsWaitingForAnnotation = 0;
					out.updateProgress(5);
			
					while (seqsWaitingForAnnotation < getSeqs().length && !isCancelled()) {
						for (int s = activeQuerys.size(); s < 25 && s < getSeqs().length; s++) {
							out.print("Preparing annotation for "+getSeqs()[seqsWaitingForAnnotation].getName()+" ("+(seqsWaitingForAnnotation+1)+"/"+getSeqs().length+")\n");
							spawnAnnotation(getSeqs()[seqsWaitingForAnnotation]);
							seqsWaitingForAnnotation++;
						}
						sleep(10000);	// wait before respawn
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				return null;
			}
		
			public void done() {
				if (isCancelled()) {
					out.updateProgress(0);
					out.print("Annotation process aborted, aborting all scans...\n");
					for (int i = 0; i < activeQuerys.size(); i++) 
					{
						activeQuerys.get(i).cancel(true);
						out.print(activeQuerys.get(i).getQuerySequence().getName()+" aborted! \n");
					}
					activeQuerys.clear();
					out.print("------------------------------\nReady to submit a new job");
				}
			}
		};
		
		jobLauncher.execute();
	}
	
}
