package domosaics.localservices.hmmer3.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import domosaics.algos.overlaps.OverlapResolver;
import domosaics.localservices.codd.ConditionallyDependentDomainPairMap;
import domosaics.localservices.executor.Executor;
import domosaics.localservices.hmmer3.Hmmer3Engine;
import domosaics.localservices.hmmer3.ui.HmmerServicePanel;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.io.ArrangementImporterUtil;
import domosaics.model.arrangement.io.HmmOutReader;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.pages.SelectNamePage;





/**
 * Class to run a local version of hmmscan (Hmmer3).
 * Implements the {@link Hmmer3Program} interface. Is not
 * executed directly; this is is handled by {@link Hmmer3Engine}
 * which expects a program of type {@link Hmmer3Program}.
 * 
 * TODO:
 * - include static final version number of supported program
 *   (perhaps better in Hmmer3Program)
 * - argument validation before prepare()
 * - perhaps prepare() should be forced to return String[]?
 * - progress management
 * - ensure that is this is called on a sequence view,
 *   that the results are added to corresponding arrangement view
 *   (that is, of the current project)
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class HmmScan implements Hmmer3Program {

	
	private File hmmScanBin, fasta, hmmDB, outfile;
	private boolean ga, biasFilter, maxFilter, coddFilter;
	
	private int totalFastaEntries, completedScans;
	private String name, evalue, cpu, overlapResolvMethod;
	private String[] args;
	private HmmerServicePanel parent;
	
	private DomainArrangement[] arrangementSet;
	private ViewElement seqView;
//	private HmmOutReader parser;
	
	/**
	 * Default constructor
	 * @param bin
	 * @param fasta
	 * @param hmmDB
	 * @param parent
	 */
	public HmmScan(File bin, File fasta, File hmmDB, HmmerServicePanel parent) {
		this.overlapResolvMethod=new String("None");
		this.hmmScanBin = bin;
		this.fasta = fasta;
		this.hmmDB = hmmDB;
		this.parent = parent;
		this.name = "HMMSCAN";
		this.seqView = null;
	}
	
	/**
	 * Secondary constructor, allows instantiation with CPU variable
	 * @param bin
	 * @param fasta
	 * @param hmmDB
	 * @param cpu
	 * @param parent
	 */
	public HmmScan(File bin, File fasta, File hmmDB, String cpu, HmmerServicePanel parent) {
		this.hmmScanBin = bin;
		this.fasta = fasta;
		this.hmmDB = hmmDB;
		this.parent = parent;
		this.cpu = cpu;
		this.name = "HMMSCAN";
		this.seqView = null;
	}
	
	/**
	 * Prepares all necessary arguments as a String[]
	 * which is required for execution via {@link Executor}. Does
	 * not validate arguments. 
	 */
	public void prepare() {

		// crate the temporary output which is later parsed to
		// create a DomainArrangement[]
		try {
			outfile = File.createTempFile("hmmerJob_", ".hmmout");
		}
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
		
		if (!maxFilter) {
			if (ga) { // gathering threshold is on (no evalue needed)
				args = new String[10];
				args[0] = hmmScanBin.getAbsolutePath();
				args[1] = "--domtblout";
				args[2] = outfile.getAbsolutePath();
				args[3] = "--noali";
				args[4] = "--cut_ga";
				args[5] = "--max";
				args[6] = "--cpu";
				args[7] = cpu; 
				args[8] = hmmDB.getAbsolutePath();
				args[9] = fasta.getAbsolutePath();
			}
			else { // gathering threshold is off (evalue needed)
				args = new String[11];
				args[0] = hmmScanBin.getAbsolutePath();
				args[1] = "--domtblout";
				args[2] = outfile.getAbsolutePath();
				args[3] = "--noali";
				args[4] = "--domE";
				args[5] = evalue;
				args[6] = "--max";
				args[7] = "--cpu";
				args[8] = cpu;
				args[9] = hmmDB.getAbsolutePath();
				args[10] = fasta.getAbsolutePath();
			}	
		}
		else { 
			if(!biasFilter) {
				if (ga) { // gathering threshold (no evalue)
						args = new String[10];
						args[0] = hmmScanBin.getAbsolutePath();
						args[1] = "--domtblout";
						args[2] = outfile.getAbsolutePath();
						args[3] = "--noali";
						args[4] = "--cut_ga";
						args[5] = "--nobias";
						args[6] = "--cpu";
						args[7] = cpu; 
						args[8] = hmmDB.getAbsolutePath();
						args[9] = fasta.getAbsolutePath();
				}
				else { // gathering threshold is off (evalue needed)
					args = new String[11];
					args[0] = hmmScanBin.getAbsolutePath();
					args[1] = "--domtblout";
					args[2] = outfile.getAbsolutePath();
					args[3] = "--noali";
					args[4] = "--domE";
					args[5] = evalue;
					args[6] = "--nobias";
					args[7] = "--cpu";
					args[8] = cpu;
					args[9] = hmmDB.getAbsolutePath();
					args[10] = fasta.getAbsolutePath();
				}
			} else {
				if (ga) { // gathering threshold (no evalue)
					args = new String[9];
					args[0] = hmmScanBin.getAbsolutePath();
					args[1] = "--domtblout";
					args[2] = outfile.getAbsolutePath();
					args[3] = "--noali";
					args[4] = "--cut_ga";
					args[5] = "--cpu";
					args[6] = cpu;
					args[7] = hmmDB.getAbsolutePath();
					args[8] = fasta.getAbsolutePath();
				}
				else { // no gatherin threshold (evalue needed)
					args = new String[10];
					args[0] = hmmScanBin.getAbsolutePath();
					args[1] = "--domtblout";
					args[2] = outfile.getAbsolutePath();
					args[3] = "--noali";
					args[4] = "--domE";
					args[5] = evalue;
					args[6] = "--cpu";
					args[7] = cpu;
					args[8] = hmmDB.getAbsolutePath();
					args[9] = fasta.getAbsolutePath();
				}
			}
		}

		// Ensure the correct args are present
		for (int i=0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		
		// completed scans
		completedScans = 0;
		// complete no. of entries to be scanned
		totalFastaEntries = countFastaSequences(fasta);
	}
	
	/**
	 * Set the panel that triggers this service
	 * @param parentPanel
	 */
	public void setParentPanel(HmmerServicePanel parentPanel) {
		this.parent = parentPanel;
	}
	

	/**
	 * set the sequence view from which the sequences were extracted
	 * (if from seq not from view, create an according sequence view)
	 * @param fromView
	 */
	public void setSeqView(ViewElement seqView) {
		this.seqView = seqView;
	}
	
	
	/**
	 * Set the executable of this service
	 * @param hmmScanBin
	 */
	public void setHmmScanBin(File hmmScanBin) {
		this.hmmScanBin = hmmScanBin;
	}
	
	/**
	 * Set the fasta file
	 * @param fasta
	 */
	public void setFasta(File fasta) {
		this.fasta = fasta;
	}
	
	/**
	 * Set the hmmer database file
	 * @param hmmDB
	 */
	public void setHmmDB(File hmmDB) {
		this.hmmDB = hmmDB;
	}
	
	/**
	 * Toggle the gathering threshold
	 * @param ga
	 */
	public void setGA(boolean ga) {
		this.ga = ga;
	}
	
	/**
	 * Set the number of CPUs to run on
	 * @param cpuNo
	 */
	public void setCpu(String cpuNo) {
		this.cpu = cpuNo;
	}
	
	/**
	 * Set the evalue (as string, as the command array
	 * required by {@link Executor} is String[]
	 * @param evalue
	 */
	public void setEvalue(String evalue) {
		this.evalue = evalue;
	}
	
	/**
	 * Toggle bias filter
	 * @param biasFilter
	 */
	public void setBiasFilter(boolean biasFilter) {
		this.biasFilter = biasFilter;
	}


	/**
	 * Toggle bias filter
	 * @param biasFilter
	 */
	public void setMaxFilter(boolean maxFilter) {
		this.maxFilter = maxFilter;
	}

	/**
	 * Toggle CODD procedure filter
	 * @param coddFilter
	 */
	public void setCoddFilter(boolean coddFilter) {
		this.coddFilter = coddFilter;
	}
	
	/**
	 * Toggle CODD procedure filter
	 * @param coddFilter
	 */
	public void setOverlapMethod(String method) {
		this.overlapResolvMethod = method;
	}
	
	
	/**
	 * Get HmmerDB file
	 * @return
	 */
	public File getHmmDB() {
		return this.hmmDB;
	}
	
	/**
	 * Get fasta file
	 * @return
	 */
	public File getFasta() {
		return this.fasta;
	}
	
	/**
	 * Get number of CPUs
	 * @return
	 */
	public String getCpu() {
		return this.cpu;
	}
	
	/**
	 * Get current hmmscan executable
	 * @return
	 */
	public File getHmmScanBin() {
		return this.hmmScanBin;
	}

	/**
	 * Get the current bias filter setting
	 * @return
	 */
	public boolean getBiasFilter() {
		return this.biasFilter;
	}
	
	/**
	 * Get the panel that hosts / triggers this
	 * program
	 */
	public HmmerServicePanel getParentServicePanel() {
		return this.parent;
	}
	
	/**
	 * Method of {@link Hmmer3Program} interface.
	 * Returns a list of arguments
	 */
	public String[] getArgs() {
		return this.args;
	}
	
	/**
	 * Method of {@link Hmmer3Program} interface.
	 * Returns the name of this program
	 */
	public String getName() {
		return this.name;
	}
	
	public String getCommandCall() {
		StringBuffer commandString = new StringBuffer();
		for (String arg: args)
			commandString.append(arg+" ");
		
		return commandString.toString();
	}
	
	
	/**
	 * Method of {@link Hmmer3Program} interface.
	 * Parses the results of a hmmscan run. If hits were
	 * found, results are parsed into a set {@link DomainArrangement}.
	 * The {@link WizardManager} is used to create (and name)
	 * a new view, and the sequences corresponding to the arrangements
	 * with domains are attached to the DomainView.
	 */
	public void parseResults() {
		
		if (HmmOutReader.checkFileFormat(outfile)) {
			
			if (!ga)
				HmmOutReader.setThreshold(Double.parseDouble(evalue));
			
			arrangementSet = ArrangementImporterUtil.importData(outfile);
			parent.close();
			
			// If the CODD procedure was requested, launch
			if(coddFilter) {
				arrangementSet = ConditionallyDependentDomainPairMap.coddProcedure(arrangementSet);
			}
			else {
				// test for another post-processing filter
				if(overlapResolvMethod.equals("Evalue") || overlapResolvMethod.equals("Coverage") ) {
					arrangementSet = OverlapResolver.resolveOverlaps(arrangementSet, overlapResolvMethod);
				}
			}

			int importedProts = arrangementSet.length;
			
			if (importedProts == 0) {
				String name = (seqView == null) ? fasta.getName() : seqView.getTitle();
				MessageUtil.showInformation(null, "No significant hits found in "+ name);
			}

			int withoutDomProts = 0;
			// read the sequences in the source fasta file
			SequenceI[] seqs = new FastaReader().getDataFromFile(fasta);
			
			List<DomainArrangement> arrList=new ArrayList<DomainArrangement>();
			boolean withoutDom;
			for(int i=0; i<seqs.length; i++) {
				withoutDom=true;
				int j=0;
				for(; j<arrangementSet.length && withoutDom; j++) {
					if(arrangementSet[j].getName().equals(seqs[i].getName())) {
						withoutDom=false;						
					}
				}
				if(withoutDom) {
					DomainArrangement da = new DomainArrangement();
					da.setName(seqs[i].getName());
					da.setSeqLen(seqs[i].getLen(true));
					da.setSequence(seqs[i]);
					arrList.add(da);
					withoutDomProts++;
				} else {
					arrList.add(arrangementSet[j-1]);
				}
			}
			arrangementSet=arrList.toArray(new DomainArrangement[arrList.size()]);
			
			String defaultName;
			ProjectElement project = null;

			// external fasta was used
			if (seqView == null) {
				defaultName = fasta.getName() + "-hmmscan-results";
			}
			else {
				ViewElement elem = WorkspaceManager.getInstance().getViewElement(seqView.getViewInfo());
				project = elem.getProject();
				defaultName = seqView.getTitle()+"-hmmscan-results";
			}					

			String viewName = null;
			String projectName = null;
			while (viewName == null) {
				Map m = WizardManager.getInstance().selectNameWizard(defaultName, "annotation", project, true);
				viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
				projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);

				if (viewName == null) 
					if (MessageUtil.showDialog("You will loose the hmmscan results. Are you sure?"))
						// will not delete tmp files, just in case
						return;
			}

			// get chosen project
			project = WorkspaceManager.getInstance().getProject(projectName);

			DomainViewI resultDAView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
			resultDAView.setDaSet(arrangementSet);
			resultDAView.loadSequencesIntoDas(seqs, resultDAView.getDaSet());


			// only create a sequence view if the sequences came from a file
			if (seqView == null) {
				SequenceView resultSeqView = ViewHandler.getInstance().createView(ViewType.SEQUENCE, viewName+"_seqs");
				resultSeqView.setSeqs(resultDAView.getSequences());
				ViewHandler.getInstance().addView(resultSeqView, project);
			}
			// add domain view now, so that it is active
			ViewHandler.getInstance().addView(resultDAView, project);
			String noDomMessage="";
			if(withoutDomProts!=0 && importedProts!=0)
				noDomMessage+="\nWarning: "+withoutDomProts+" do not have any domain!";
			MessageUtil.showInformation(null, importedProts+withoutDomProts+" proteins successfully imported."+noDomMessage);

		}
		else {
			String name = (seqView == null) ? fasta.getName() : seqView.getTitle();
			parent.close();
			MessageUtil.showInformation(null, "No hits found in "+ name);
			
		}
		
		outfile.delete();
		// if seqs came from a view, delete the tmp fasta file
		if (!(seqView == null))
			fasta.delete();
	}
	
	public int returnValue(int result) {
		return result;
	}
	
	
	/**
	 * Counts the number of fasta entries that will
	 * be scanned. This can be seen as part of the 
	 * progress management logic.
	 * @param fasta
	 * @return
	 */
	private int countFastaSequences(File fasta) {
		String line = new String();
		int entries = 0;
			
		try {
			BufferedReader br = new BufferedReader(new FileReader(fasta));
			while((line = br.readLine())!= null) 
				if (line.substring(0, 1).equals(">")) 
					entries++;
				
				br.close();
		} 
		catch (Exception e) {
			Configuration.getLogger().debug(e.toString());
		}
		return entries;
	}
	
}
