package angstd.localservices.hmmer3.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.localservices.hmmer3.Hmmer3Engine;
import angstd.localservices.hmmer3.programs.HmmPress;
import angstd.localservices.hmmer3.programs.HmmScan;
import angstd.model.configuration.Configuration;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;

/**
 * HmmScanPanel holds the GUI components necessary to start local
 * hmmscan jobs. It is of type {@link HmmerServicePanel}.
 * The general setup is closely related to other panels used in
 * AnGSTD.
 * 
 * TODO:
 * - progressbar issue (inkl. proper calc)
 * - unhandled exceptions
 * - Runtime can only return number of CPUs available to JVM (not System)
 * - After the run, we should be able to attach sequences _directly_ to the arrangements
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class HmmScanPanel extends HmmerServicePanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	protected Hmmer3Frame parent;
	protected HashMap<String, File> hmmer3bins;
	protected HashMap<String, String> namedArgs;
	protected HmmScan hmmScan;
	protected JTextField binTF, hmmTF, fastaTF, evalueTF;
	protected JCheckBox gaCkb, biasCkb; 
	protected JButton loadBinDir, loadHmmDB, loadFastaFile, run, cancel;
	protected JComboBox cpuCB;
	protected JLabel thresholdLabel, evalLabel, cpuLabel, biasFilterLabel, gaLabel;
	protected JTextArea console;
	protected JProgressBar progressBar;
	protected JPanel ePane;
	protected File hmmBinDir, hmmDBFile, fastaFile;

	 
	public HmmScanPanel(Hmmer3Frame parent) {
		this.parent = parent;
		setLayout(new MigLayout("", "[left]"));
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "Run hmmscan");
		setBorder(title);
		
		initComponents();
		initPanel();
	}
	 
	/**
	 * This actually initiates all graphical 
	 * components
	 */
	private void initComponents() {
		
		Configuration config = Configuration.getInstance();
		
		binTF = new JTextField(35);
		binTF.setText(config.getHmmerBins());
		hmmTF = new JTextField(35);
		hmmTF.setText(config.getHmmerDB());
		fastaTF = new JTextField(35);
		evalueTF = new JTextField(5);
		evalueTF.setText("0.1"); // default evalue
		evalLabel = new JLabel("Static cutoff (E-value)");
		biasFilterLabel = new JLabel("Filter");
		// the evalue label and field
		// are conditionally shown if ga is not selected
		// easier for layout when they are in a seperate Panel
		ePane = new JPanel();
		ePane.add(evalLabel);
		ePane.add(evalueTF);
		ePane.setVisible(false); // only visible if ga is deselected
		thresholdLabel = new JLabel("Match threshold");
		cpuLabel = new JLabel("Number of CPUs");
		progressBar = new JProgressBar();
		progressBar.setBorderPainted(true);
		progressBar.setPreferredSize(new Dimension(500,20));

		// gathering threshold checkbox. If disabled,
		// the panel for the evalue is set to visible
	    gaCkb = new JCheckBox("Model defined", true);
	    gaCkb.addItemListener(new ItemListener() {	
			public void itemStateChanged(ItemEvent e) {
				if (gaCkb.isSelected())
					ePane.setVisible(false); 
					else
						ePane.setVisible(true);
			}
		});
	
	    biasCkb = new JCheckBox("Bias filter", true);
	    
		// TODO: this only returns the number of CPUs
		// available to the JVM.
		int availProc = Runtime.getRuntime().availableProcessors()-1;
		String[] cpuNo = new String[availProc];
		for (int i = 0; i < availProc; i++) {
			int cno = i+1;
			cpuNo[i] = " "+cno;
		}
		cpuCB = new JComboBox(cpuNo);
		cpuCB.setSelectedIndex(0); //default to one proc
		
		loadBinDir = new JButton("HMMER3 binaries");
		loadBinDir.setActionCommand("LoadBins");
		loadBinDir.addActionListener(this);
		
		loadHmmDB = new JButton("Load Database");
		loadHmmDB.setActionCommand("LoadDB");
		loadHmmDB.addActionListener(this);
		
		loadFastaFile = new JButton("Load Sequences");
		loadFastaFile.setActionCommand("LoadFasta");
		loadFastaFile.addActionListener(this);
		
		// init console
		console = new JTextArea ();
		console.setFont(new Font ("Courier", 0, 12));
		console.setColumns(75);
		console.setLineWrap(true);
		console.setRows(8);
		console.setWrapStyleWord(false);
		console.setEditable(false);
		
		// init buttons
		run = new JButton("Run");
		run.setActionCommand("Run");
		run.addActionListener(this);

		cancel = new JButton("Cancel");
		cancel.setActionCommand("Cancel");
		cancel.addActionListener(this);
		
	}
	

	/**
	 * Positions all the GUI components on the panel
	 */
	private void initPanel() {
		
		add(loadBinDir, "gap 10");
		add(binTF, "gap 10, span2, growX, wrap");
		
		add(loadHmmDB, "gap 10");
		add(hmmTF, "gap 10, span2, growX, wrap");
		
		add(new JXTitledSeparator("Sequences"), "growx, span, wrap, gaptop 10");
		
		add(loadFastaFile, "gap 10");
		add(fastaTF, "gap 10, span 2, growX, wrap");
//		add(new JLabel("Or Select Loaded View:"),"gap 10");
//		add(selectView, "gap 10, span 2, growX, wrap");
		add(loadFastaFile, "gap 10");
		add(fastaTF, "gap 10, span2, growX, wrap");
		
		add(new JXTitledSeparator("Options"), "growX, span, wrap, gaptop 10");
		
		add(thresholdLabel,	"gap 10");
		add(gaCkb, "gap 10");
		add(ePane, "gap 10, span 2, growX, wrap");
		
		add(biasFilterLabel, "gap 10");
		add(biasCkb, "gap 10, span2, growX, wrap");
		
		add(cpuLabel, "gap 10");
		add(cpuCB, "gap 10, span2, wrap");

		add(new JXTitledSeparator("Progress"), "growX, span, wrap, gaptop 10");
		add(progressBar, "gap 10, span3, growX, wrap");
		
		add(new JXTitledSeparator("Console"), "growX, span, wrap, gaptop 10");
		add(new JScrollPane(console), "gap 10, span, wrap");	
		add(run, "gap 10");
		add(cancel);
	}
	
	/**
	 * Actions triggered on specific ui components.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("LoadBins"))
			loadBinAction();
		else if (e.getActionCommand().equals("LoadDB")) 
			loadDBAction();
		else if(e.getActionCommand().equals("LoadFasta")) 
			loadFastaAction();
		else if(e.getActionCommand().equals("Run")) 
			runHmmScanAction();
		else if(e.getActionCommand().equals("Cancel")) 
			cancelAction();
		else 
			System.out.println("Unknown component tried to trigger an action @ HmmScanPanel");
	}
	
	/**
	 * Triggered when the load bin button is pressed
	 * The input fields are check for validity at a later point
	 * (before launching the job)
	 */
	private void loadBinAction() {

		File binDir = FileDialogs.openChooseDirectoryDialog(this);
		if (binDir == null || !binDir.canRead())
			return;
		
		hmmBinDir = binDir;
		binTF.setText(binDir.getAbsolutePath());	
	}
	
	
	/**
	 * Triggered when the load DB button is pressed.
	 */
	private void loadDBAction() {
		File file = FileDialogs.showOpenDialog(this);
		if (file == null || !file.canRead())
			return;
		
		hmmDBFile = file;
		hmmTF.setText(hmmDBFile.getAbsolutePath());
	}
	
	/**
	 * Triggered when the load Fasta button is pressed.
	 */
	private void loadFastaAction() {
		File file = FileDialogs.showOpenDialog(this);
		if(file == null || !file.canRead())
			return;
		
		fastaFile = file;
		fastaTF.setText(file.getAbsolutePath());
	}
	
	/**
	 * Triggered when the run button is pressed.
	 * Checks if all necessary inputs have been made, instantiates
	 * {@link HmmScan} (of type {@link Hmmer3Program}) 
	 * and launches it via the current instance of 
	 * {@link Hmmer3Engine}
	 */
	private void runHmmScanAction() {
	
		if (binTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a directory with hmmer3 binaries.");
			return;
		}
		else if (hmmTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a hmmer3 database you wish to scan against.");
			return;
		}
		else if (fastaTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a fasta file.");
			return;
		}
		
		// if the user has set these fields globally, we
		// still have to check them again and setup the 
		// hmmer3engine instance with the progs
		checkBins(new File(binTF.getText()));
		checkDbDir(new File(hmmTF.getText()));
		
		hmmScan = new HmmScan(
				Hmmer3Engine.getInstance().getAvailableServicePath("hmmscan"), 
				fastaFile, hmmDBFile, this);
		hmmScan.setCpu((String)cpuCB.getSelectedItem());
		// clear console
		console.setText("");
		
		if (gaCkb.isSelected()) 
			hmmScan.setGA(true); 
		else {
			hmmScan.setGA(false);
			if (evalueTF.getText().equals("")) {
				MessageUtil.showWarning("Please choose an evalue threshold (or use the gathering threshold).");
				return;
			}
			else {
				hmmScan.setEvalue(evalueTF.getText());
			}
		}
		
		if (biasCkb.isSelected())
			hmmScan.setBiasFilter(true);
			else
				hmmScan.setBiasFilter(false);
		
		// Launches the hmmscan job
		Hmmer3Engine.getInstance().launchInBackground(hmmScan);

		run.setText("Running");
		run.setEnabled(false);
		progressBar.setIndeterminate(true);
	}
	
	/**
	 * Triggered when the cancel button is pressed.
	 */
	private void cancelAction() {
		if (Hmmer3Engine.getInstance().isRunning()) { 
			Hmmer3Engine.getInstance().stop();
			run.setText("  Run  ");
			progressBar.setValue(0);
			run.setEnabled(true);
			progressBar.setIndeterminate(false);
			console.setText("");
			writeToConsole("hmmscan run canceled by user!");
		} 
		else {
			close();
		}
				
	}

	/**
	 * Closes the parent frame
	 */
	public void close() {
		parent.dispose();
	}
	
	/**
	 * Prints to console
	 * 
	 * @param msg
	 * 		the message to be printed
	 */
	public void writeToConsole(String msg) {
		console.append(msg+"\n");
	}
	
	
	/**
	 * TODO
	 * Crude: dunno how to get my hand on the ProgressBar
	 * to update the bastard, so I declared it as an
	 * abstract method in HmmerServicePanel. Sucks.
	 */
	public JProgressBar getProgressBar() {
		return this.progressBar;
	}
	
	/**
	 * Check that the selected bin dir contains a excutable HMMER program
	 * @param binDir
	 */
	private void checkBins(File binDir) {
		
		File[] files = binDir.listFiles();
		hmmer3bins = new HashMap<String, File>();
		
		if (files.length == 0) {
			MessageUtil.showWarning("Could not find any HMMER programs.");
			binTF.setText("");
			return;
		}
		for (int i=0; i < files.length; i++) {	
			
			if ( Hmmer3Engine.isSupportedService(files[i].getName()) ) {
				if (!files[i].canExecute()) {
					MessageUtil.showWarning(this, files[i].getAbsoluteFile()+" is not executable. Exiting.");
					binTF.setText("");
					return;
				}
				hmmer3bins.put(files[i].getName(), files[i]);
			}
		}	
		if (hmmer3bins.isEmpty()) {
			MessageUtil.showWarning(this, "Could not find all required HMMER programs (hmmscan, hmmpress).");
			binTF.setText("");
			return;
		}
		hmmBinDir = binDir;
		Hmmer3Engine.getInstance().setAvailableServices(hmmer3bins);

	}
	
	
	private void checkDbDir(File dbFile) {
		// check if pressed files are available
		if (!HmmPress.hmmFilePressed(dbFile)) {
			if (MessageUtil.showDialog("The HMMERDBFILE is not pressed. Do you want AnGSTD to press it now?")) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				// TODO: I would like to disable GUI components here
				// and enable them _after_ the run is complete.
				// See also angstd.localservices.hmmer3.programs.HmmPress
				HmmPress hmmPress = new HmmPress(Hmmer3Engine.getInstance().getAvailableServicePath("hmmpress"), dbFile, this);
				Hmmer3Engine.getInstance().launchInBackground(hmmPress);
				progressBar.setIndeterminate(true);
			}
			else {
				return;
			}	
		}
		hmmDBFile = dbFile;
	}
	
	
}
