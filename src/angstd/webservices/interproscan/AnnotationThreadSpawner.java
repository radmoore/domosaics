package angstd.webservices.interproscan;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import angstd.model.arrangement.ArrangementManager;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.sequence.SequenceI;

/**
 * AnnotationThreadSpawner spawns for a set of query sequences 
 * {@link AnnotationThread}s to annotate the sequences against InterPro using
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
public class AnnotationThreadSpawner implements AnnotatorProcessWriter{

	/** query sequences */
	protected SequenceI[] seqs;
	
	/** Chosen signature method for annotation, e.g. hmmpfam */
	protected String method;
	
	/** valid email address used for WSInterproScan */
	protected String email;
	
	/** object which prints the process information */
	protected AnnotatorProcessWriter out;
	
	/** main thread which spawns annotation threads for all query sequences */
	protected SwingWorker<Boolean, Void> annotationThread;
	
	/** list of all annotation threads */
	protected ArrayList<AnnotationThread> activeQuerys;
	
	/** number of active annotation threads */
	protected int activeThreads = 0;
	
	/** number of finished sequences */
	protected int seqsFinished;
	
	/** manager holding the resulting annotated arrangements */
	protected ArrangementManager daSet = new ArrangementManager();
	
	/**
	 * Constructor for a new AnnotationThreadSpawner. The standard
	 * output is used for messages about the annotation process.
	 */
	public AnnotationThreadSpawner() {
		this(null);
	}
	
	/**
	 * Constructor for a new AnnotationThreadSpawner
	 * 
	 * @param out
	 * 		the object which shall be used for printing messages about the 
	 * 		annotation process
	 */
	public AnnotationThreadSpawner(AnnotatorProcessWriter out) {
		if (out == null)
			out = this;
		else
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
		if(	annotationThread != null && annotationThread.getState().equals(SwingWorker.StateValue.STARTED)) 
			return true;
		return false;
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
	 * Spawns a new annotation thread with the parameters set.
	 * 
	 * @param seq
	 * 		the query sequence to be annotated
	 */
	protected void spawnAnnotation(SequenceI seq) {
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
	 * Cancels all active annotation threads against Interpro.
	 */
	public void cancel() {
		annotationThread.cancel(true);
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
	public void processResults(AnnotationThread annotator, String res) {
		if (annotator.isCancelled()) {
			out.print("annotation cancelled. \n");
			return;
		}
		
		DomainArrangement da = new InterProScanResultParser().parseResult(res);
		SequenceI seq = annotator.getQuerySequence();
		da.setSequence(seq);
		da.setName(seq.getName());
		da.setSeqLen(seq.getLen(true));
		
		daSet.add(da);
		out.print(da.getName() + " annotated. \n");
		activeThreads--;
		activeQuerys.remove(annotator);
		
		// update progressbar
		seqsFinished++;
		int progress = (int) Math.round( (100/(double)getSeqs().length)*seqsFinished);
			out.updateProgress(Math.min(5+progress, 105));
	}
	
	/**
	 * Spawns for a number of sequence annotation threads up to 25 threads 
	 * simultaniesly.
	 * <p>
	 * Time estimation for 21 sequences and hmmpfam: 2.38 minutes
	 */
	public void startMultiThreadSpawn() {
		activeQuerys = new ArrayList<AnnotationThread>();
		System.out.println("Executing multi thread annotation!");
		
		annotationThread = new SwingWorker<Boolean, Void>() {	  
			public Boolean doInBackground() {
				int seqsFinished = 0;
				System.out.println("Executing multi thread annotation!");
				
				// number of times where up to 25 sequences are queried simoultaniesly
				int neededRuns = (int) Math.ceil(getSeqs().length / (double) 25);

				out.updateProgress(5);
			
				for (int r = 0; r < neededRuns; r++) {
					
					out.print("I am up to something here");
					// spawn up to 25 annotation threads for the next round
					if (r == neededRuns - 1) { 		// lastrun
						out.print("Annotate next "+(getSeqs().length-seqsFinished)+" proteins... \n");
						
						// spawn x annotation threads
						while (seqsFinished < getSeqs().length) {
							out.print("Start annotation for "+getSeqs()[seqsFinished].getName()+" ("+(seqsFinished+1)+"/"+getSeqs().length+")\n");
							spawnAnnotation(getSeqs()[seqsFinished]);
							seqsFinished++;
							activeThreads++;
						}

					} else {
						out.print("Annotate next 25 proteins... \n");
						
						// spawn 25 annotation threads
						for (int s = 0; s < 25; s++) {
							out.print("Start Annotation for  protein "+getSeqs()[seqsFinished].getName()+" ("+(seqsFinished+1)+"/"+getSeqs().length+")\n");
							spawnAnnotation(getSeqs()[seqsFinished]);
							seqsFinished++;
							activeThreads++;
						}
					}
					
					// wait for the results until spawning the next threads
					while (activeThreads != 0) {
						out.print("please wait for annotations to finish...\n");
						sleep(4000);	// wait for threads to finish
					}
				}
				return true;
			}
			
			public void done() {
				if (isCancelled()) {
					out.print("Annotation process aborted, aborting all InterProScans..");
					for (int i = 0; i < activeQuerys.size(); i++) 
						activeQuerys.get(i).cancel(true);
					return;
				}
				out.print("All sequences annotated, click apply to create resulting view.");
			}
		};
		annotationThread.execute();
	}
	
	/**
	 * Spawns sequence annotation threads one-by-one
	 * After each thread is spawned, the spawner waits until the annotation is complete.
	 * <p>
	 * Time estimation for 21 sequences and hmmpfam: 8.56 minutes 
	 * (that is, this is considerably slower)
	 */
	public void startSingleThreadSpawn() {
		activeQuerys = new ArrayList<AnnotationThread>();
		annotationThread = new SwingWorker<Boolean, Void>() {	  
			
			public Boolean doInBackground() {
				seqsFinished = 0;
				int i = 0;
				activeThreads = 0;
				out.updateProgress(5);
				
				while (i < seqs.length) {
					
					if (activeThreads == 0) {
						out.print("Start Annotation for  protein "+seqs[i].getName()+" ("+(i+1)+"/"+seqs.length+")\n");
						spawnAnnotation(seqs[i]);
						i++;
						activeThreads++;
					} else {
						sleep(2000);	// wait for threads to finish
					}
				}
				
				// wait for last thread to finish
				while (activeThreads != 0) {
					sleep(2000);
				}
				
				
				return true;
			}
			
			// TODO: check here to solve 'cancel' problem (ADM)
			public void done() {
				if (isCancelled()) {
					out.print("Annotation process aborted, aborting all InterProScans..");
					for (int i = 0; i < activeQuerys.size(); i++) 
						activeQuerys.get(i).cancel(true);
					return;
				}
				out.print("All sequences annotated, click apply to create resulting view.");
			}
		};
		
		annotationThread.execute();
	}

	/**
	 * Prints textual messages about the annotation progress to the standard 
	 * output.
	 * 
	 * @param msg
	 * 		the message to be printed
	 */
	public void print(String msg) {
		System.out.println(msg);
	}

	public void updateProgress(int val) {}
	
}
