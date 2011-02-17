package angstd.localservices.hmmer3.programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import angstd.localservices.executor.Executor;
import angstd.localservices.hmmer3.ui.HmmerServicePanel;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.io.ArrangementImporterUtil;
import angstd.model.arrangement.io.HmmOutReader;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaReader;
import angstd.ui.ViewHandler;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.wizards.WizardManager;

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

	
	protected File hmmScanBin, fasta, hmmDB, outfile;
	protected boolean ga, biasFilter;
	
	protected int totalFastaEntries, completedScans;
	protected String name, evalue, cpu;
	protected String[] args;
	protected HmmerServicePanel parent;
	
	protected DomainArrangement[] arrangementSet;
	protected HmmOutReader parser;
	
	/**
	 * Default constructor
	 * @param bin
	 * @param fasta
	 * @param hmmDB
	 * @param parent
	 */
	public HmmScan(File bin, File fasta, File hmmDB, HmmerServicePanel parent) {
		this.hmmScanBin = bin;
		this.fasta = fasta;
		this.hmmDB = hmmDB;
		this.parent = parent;
		this.name = "HMMSCAN";
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
			System.out.println("Something went wrong when creating the tmp file.");
		}
		
		if (biasFilter) {
			if (ga) { // gathering threshold is on (no evalue needed)
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
		}
		else { // bias filter is off
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
			
			arrangementSet = ArrangementImporterUtil.importData(outfile);
			int importedProts = arrangementSet.length;
			parent.close();
			
			if (importedProts > 0) {
				
				String defaultName = fasta.getName()+"-hmmscan-results";	
				// read the sequences in the source fasta file
				SequenceI[] seqs = new FastaReader().getDataFromFile(fasta);
								
				String viewName = null;
				while (viewName == null) {
					viewName = WizardManager.getInstance().selectNameWizard(defaultName, "view");
					if (viewName == null) 
						MessageUtil.showWarning("A valid view name is needed to complete this action");
				}
				
				DomainViewI domResultView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
				domResultView.setDaSet(arrangementSet);
				// associate sequences with the found arrangements
				domResultView.loadSequencesIntoDas(seqs, domResultView.getDaSet());
				ViewHandler.getInstance().addView(domResultView, null);
				MessageUtil.showInformation(importedProts+" proteins successfully imported.");
			}
			else {		
				MessageUtil.showInformation("No hits found in "+fasta.getName());
			}
		}
		else {		
			MessageUtil.showInformation("No hits found in "+ fasta.getName());
			parent.close();
		}
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
			// TODO: handle exception
		}
		return entries;
	}

	
}
