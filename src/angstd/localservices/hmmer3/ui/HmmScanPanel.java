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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
import angstd.localservices.hmmer3.programs.Hmmer3Program;
import angstd.model.configuration.Configuration;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaWriter;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.wizards.WizardListCellRenderer;
import angstd.util.StringUtils;

/**
 * HmmScanPanel holds the GUI components necessary to start local
 * hmmscan jobs. It is of type {@link HmmerServicePanel}.
 * The general setup is closely related to other panels used in
 * AnGSTD.
 * 
 * TODO:
 * - progressbar issue (inkl. proper calc)
 * - unhandled exceptions
 * - consider enclosing hmmscan and hmmpress, or,
 * - ensure that hmmscan and hmmpress are available (not
 *   only any executable)
 * - add hmmdb format check
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class HmmScanPanel extends HmmerServicePanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private Hmmer3Frame parent;
	private HashMap<String, File> hmmer3bins;
	private HmmScan hmmScan;
	private JTextField hmmScanTF, hmmPressTF, hmmTF, fastaTF, evalueTF;
	private JCheckBox gaCkb, biasCkb, coddCkb; 
	private ButtonGroup groupRadio;
	private JRadioButton overlapRadioNone, overlapRadioEvalue, overlapRadioCoverage;
	private JButton loadScanBin, loadPressBin, loadHmmDB, loadFastaFile, run, cancel;
	private JComboBox cpuCB, selectView;
	private JLabel thresholdLabel, evalLabel, cpuLabel, biasFilterLabel;
	private JTextArea console;
	private JPanel ePane, radioPane;
	private File hmmDBFile, fastaFile;
	private HashMap<Integer, File> view2file; 
	
	private ViewElement seqView;

	 
	public HmmScanPanel(Hmmer3Frame parent) {
		super();
		this.parent = parent;
		setLayout(new MigLayout("", "[left]"));
		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "Run hmmscan");
		setBorder(title);
		
		hmmer3bins = new HashMap<String, File>();
		
		initComponents();
		initPanel();
	}
	 
	/**
	 * This actually initiates all graphical 
	 * components
	 */
	private void initComponents() {
		
		Configuration config = Configuration.getInstance();
		initSelectViewBox();
		
		hmmScanTF = new JTextField(25);
		hmmScanTF.setText(config.getHmmScanBin());
		hmmPressTF = new JTextField(25);
		hmmPressTF.setText(config.getHmmPressBin());
		
		hmmTF = new JTextField(25);
		hmmTF.setText(config.getHmmerDB());
		fastaTF = new JTextField(25);
		evalueTF = new JTextField(5);
		evalueTF.setText("0.1"); // default evalue
		evalueTF.setToolTipText("Max: 10");
		evalLabel = new JLabel("E-value:");
		biasFilterLabel = new JLabel("Filter");
		
		// the evalue label and field
		// are conditionally shown if ga is not selected
		// easier for layout when they are in a seperate Panel
		ePane = new JPanel();
		ePane.add(evalLabel);
		ePane.add(evalueTF);
		ePane.setVisible(false); // only visible if ga is deselected
		thresholdLabel = new JLabel("Hit Threshold");
		
		cpuLabel = new JLabel("Number of CPUs");
		
		progressBar.setBorderPainted(true);
		progressBar.setPreferredSize(new Dimension(300,20));

		// gathering threshold checkbox. If disabled,
		// the panel for the evalue is set to visible
	    gaCkb = new JCheckBox("Confidence cutoff", true);
	    gaCkb.addItemListener(new ItemListener() {	
			public void itemStateChanged(ItemEvent e) {
				ePane.setVisible(!(gaCkb.isSelected()));
			}
		});
	
	    biasCkb = new JCheckBox("Bias filter", true);
	    
	    // RadioButton to choose (or not) a post processing method
	    // to resolve overlaps
	    radioPane = new JPanel();
		groupRadio = new ButtonGroup();
	    overlapRadioNone = new JRadioButton("None      ", true);
	    overlapRadioEvalue = new JRadioButton("E-value based        ");
	    overlapRadioCoverage = new JRadioButton("Max. coverage");
	    radioPane.add(overlapRadioNone);
	    radioPane.add(overlapRadioEvalue);
	    radioPane.add(overlapRadioCoverage);
	    groupRadio.add(overlapRadioNone);
	    groupRadio.add(overlapRadioEvalue);
	    groupRadio.add(overlapRadioCoverage);
	    overlapRadioNone.setActionCommand("None");
	    overlapRadioEvalue.setActionCommand("OverlapFilterEvalue");
	    overlapRadioCoverage.setActionCommand("OverlapFilterCoverage");
	    
	    // gathering threshold checkbox. If disabled,
		// the panel for the evalue is set to visible
	    coddCkb = new JCheckBox("", false);
	    coddCkb.setToolTipText("Context dependant annotation, see Terrapon et al., Bioinformatics, 2009");
	    coddCkb.addItemListener(new ItemListener() {	
			public void itemStateChanged(ItemEvent e) {
				gaCkb.setSelected(!coddCkb.isSelected());
				gaCkb.setEnabled(!coddCkb.isSelected());
				overlapRadioEvalue.setSelected(coddCkb.isSelected());
				overlapRadioNone.setEnabled(!coddCkb.isSelected());
				overlapRadioCoverage.setEnabled(!coddCkb.isSelected());
				if (coddCkb.isSelected())
					evalueTF.setText("10");
					else 
						evalueTF.setText("0.1");
				//groupRadio.setSelected(overlapRadioEvalue,coddCkb.isSelected());
				//ePane.setVisible(coddCkb.isSelected());
			}
		});
	    
	    // available processors to run the job on
		int availProc = Runtime.getRuntime().availableProcessors();
		String[] cpuNo = new String[availProc];
		for (int i = 0; i < availProc; i++) {
			int cno = i+1;
			cpuNo[i] = " "+cno;
		}
		cpuCB = new JComboBox(cpuNo);
		cpuCB.setSelectedIndex(0); //default to one processor
		
		loadScanBin = new JButton("HMMER3 scan bin");
		loadScanBin.setName("scan");
		loadScanBin.setActionCommand("LoadBins");
		loadScanBin.addActionListener(this);
		
		loadPressBin = new JButton("HMMER3 press bin");
		loadPressBin.setName("press");
		loadPressBin.setActionCommand("LoadBins");
		loadPressBin.addActionListener(this);
		
		loadHmmDB = new JButton("Load profiles");
		loadHmmDB.setActionCommand("LoadDB");
		loadHmmDB.addActionListener(this);
		
		loadFastaFile = new JButton("Load Sequences");
		loadFastaFile.setActionCommand("LoadFasta");
		loadFastaFile.addActionListener(this);
		
		// init console
		console = new JTextArea ();
		console.setFont(new Font ("Courier", 0, 12));
		console.setColumns(85);
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
		
		add(loadScanBin, "gap 5, w 165!");
		add(hmmScanTF, "h 25!, gap 5, span2, growX, wrap");
		add(loadPressBin, "gap 5, w 165!");
		add(hmmPressTF, "h 25!, gap 5, span2, growX, wrap");
		
		add(loadHmmDB, "gap 5, w 165!");
		add(hmmTF, "h 25!, gap 5, span2, growX, wrap");
		
		add(new JXTitledSeparator("Sequences"), "growx, span, wrap, gaptop 10");
	
		add(loadFastaFile, "gap 5, w 165!");
		add(fastaTF, "h 25!, gap 5, span2, growX, wrap");
		
		add(new JLabel("Or Select Loaded View:"),"gap 5");
		add(selectView, "h 25!, gap 5, span2, growX, wrap");
		
		add(new JXTitledSeparator("Options"), "growX, span, wrap, gaptop 10");
		
		add(thresholdLabel,	"gap 5");
		add(gaCkb, "gap 5, split 2");
		add(ePane, "growX, wrap");
		
		add(biasFilterLabel, "gap 5");
		add(biasCkb, "gap 5, span2, growX, wrap");
		
		add(cpuLabel, "gap 5");
		add(cpuCB, "gap 5, span2, wrap");
		
		add(new JXTitledSeparator("Post processing"), "growX, span, wrap, gaptop 10");
		add(new JLabel("Resolve overlaps by:"), "gap 5");
		add(radioPane, "gap 2, growX, wrap");

		add(new JLabel("Co-Occurring Domain Filter:"), "gap 5");
		add(coddCkb, "gap 5, span 2, growX, wrap");

		add(new JXTitledSeparator("Progress"), "growX, span, wrap, gaptop 10");
		add(progressBar, "h 25!, gap 5, span3, growX, wrap");
		
		add(new JXTitledSeparator("Console"), "growX, span, wrap, gaptop 10");
		add(new JScrollPane(console), "align center, span, wrap");	
		add(run, "gap 5, split 2");
		add(cancel);
	}
	
	/**
	 * Actions triggered on specific ui components.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("LoadBins"))
			loadBinAction(e);
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
	private void loadBinAction(ActionEvent e) {
		JButton src = (JButton)e.getSource();
		
		File bin = FileDialogs.showOpenDialog(this);
		if (bin == null || !bin.canRead())
			return;
		
		if (src.getName().equals("scan")) {
			hmmScanTF.setText(bin.getAbsolutePath());
		}
		else {
			hmmPressTF.setText(bin.getAbsolutePath());
		}	
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
		if (selectView.getSelectedItem() != null)
			selectView.setSelectedItem(null);
		
		File file = FileDialogs.showOpenDialog(this);
		if(file == null || !file.canRead())
			return;
		
		seqView = null; // sequences come from fasta file
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
		if (!StringUtils.isNumber(evalueTF.getText())){
			MessageUtil.showWarning(this, "The E-value field does not contain a valid number.");
			return;
		}
		
		if(hmmScanTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a hmmscan binary.");
			return;	
		}
		
		if(hmmPressTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a hmmpress binary.");
			return;	
		}

		else if (hmmTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose HMMER3 profiles");
			return;
		}
		else if (fastaTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a fasta file.");
			return;
		}
		
		// if the user has set these fields globally, we
		// still have to check them again and setup the 
		// hmmer3engine instance with the progs
		if (!checkBins(new File(hmmScanTF.getText())))
			return;
		if (!checkBins(new File(hmmPressTF.getText())))
			return;
		if (!checkDbDir(new File(hmmTF.getText())))
			return;
		
		// if we have passed all tests, then we are
		// ready to run hmmer
		hmmScan = new HmmScan(
				Hmmer3Engine.getInstance().getAvailableServicePath("hmmscan"), 
				fastaFile, hmmDBFile, this);
		hmmScan.setCpu((String)cpuCB.getSelectedItem());
		//console.setText("");
		
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
		hmmScan.setBiasFilter(biasCkb.isSelected());
		hmmScan.setOverlapMethod(groupRadio.getSelection().getActionCommand());
		hmmScan.setCoddFilter(coddCkb.isSelected());
		hmmScan.setSeqView(seqView);
		
		// Launches the hmmscan job
		Hmmer3Engine.getInstance().launchInBackground(hmmScan);

		run.setText("Running");
		run.setEnabled(false);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString("Waiting for hmmscan job to complete.");
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
			progressBar.setStringPainted(false);
			writeToConsole("*** I: hmmscan run canceled by user.\n");
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
		console.append(msg);
	}
	
	
	/**
	 * resets the components of this panel
	 * is case of an error during a run
	 * (ie something that is not triggered at start
	 * or end of a job - non zero exit code)
	 */
	public void resetPanel() {
		run.setText("  Run  ");
		progressBar.setValue(0);
		progressBar.setIndeterminate(false);
		run.setEnabled(true);
	}
	
	
	
	
	/**
	 * Check that the selected bin dir contains an executable HMMER program
	 * (at least one - it should actually check for hmmscan and hmmpress)
	 * @param bin
	 */
	private boolean checkBins(File bin) {
		
		if ( Hmmer3Engine.isSupportedService(bin.getName()) ) {
			if (!(bin.canExecute())) {
				MessageUtil.showWarning(this, bin.getAbsoluteFile()+" is not executable. Exiting.");
				return false;
			}
		}
		else {
			MessageUtil.showWarning(this, bin.getAbsoluteFile()+" is not supported");
			return false;
		}
			
		hmmer3bins.put(bin.getName(), bin);	
		Hmmer3Engine.getInstance().setAvailableServices(hmmer3bins);
		return true;

	}
	
	
	/**
	 * checks if the selected DB dir contains a pressed
	 * version of the model database, and allows the user
	 * to press it if not. NOTE: this does no
	 * format checking
	 * 
	 * @param dbFile
	 * @return true, if the the DB dir contains necessary files
	 */
	private boolean checkDbDir(File dbFile) {
		// check if pressed files are available
		if (!HmmPress.hmmFilePressed(dbFile)) {
			if (MessageUtil.showDialog("The HMMERDBFILE is not pressed. Do you want AnGSTD to press it now?")) {
				
				if (!HmmPress.isValidProfileFile(dbFile)) {
					MessageUtil.showWarning("The HMMERDBFILE does not appear to be valid. Please check.");
					return false;
				}
					
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				// TODO: I would like to disable GUI components here
				// and enable them _after_ the run is complete.
				// See also angstd.localservices.hmmer3.programs.HmmPress
				HmmPress hmmPress = new HmmPress(Hmmer3Engine.getInstance().getAvailableServicePath("hmmpress"), dbFile, this);
				Hmmer3Engine.getInstance().launchInBackground(hmmPress);
				progressBar.setIndeterminate(true);
				// we must return false, even if the press was successful to ensure that the engine instance is free
				// before we init the actual scan
				return false;
			}
			else {
				return false;
			}	
		}
		hmmDBFile = dbFile;
		return true;
	}
	
	
	/**
	 * Initiates the sequence views to select from. Creates a
	 * a temporary fasta file if a sequence view is selected
	 */
	private void initSelectViewBox() {

		// get loaded sequence views
		List<WorkspaceElement> viewList = WorkspaceManager.getInstance().getSequenceViews();
		WorkspaceElement[] seqViews = viewList.toArray(new ViewElement[viewList.size()]);
		
		// initiate the map between seqviews and tmpfiles
		view2file = new HashMap<Integer, File>();
		
		if (seqViews.length == 0) {
			selectView = new JComboBox(seqViews);
			
			selectView.setSelectedItem(null);
			selectView.setEnabled(false);
			return;
		}
		
		selectView = new JComboBox(seqViews);
		selectView.setSelectedItem(null);
		selectView.setRenderer(new WizardListCellRenderer());
		selectView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
			
				if (fastaTF.getText().length() > 0) {
					fastaTF.setText("");
				}
				JComboBox cb = (JComboBox)evt.getSource();
				
				ViewElement selected = (ViewElement)cb.getSelectedItem();
				if (selected == null)
					return;
				
				SequenceView view = ViewHandler.getInstance().getView(selected.getViewInfo());
				SequenceI[] seqs = view.getSeqs();
				
//				for (int i = 0; i < seqs.length; i++) {
//	    			
//	    			System.out.println("At position: "+i);
//	    			System.out.println("Sequence name: "+seqs[i].getName());
//	    			System.out.println("Sequence: "+seqs[i].getSeq(false));
//				}
				
				// check if we already created the tmp fasta for the selected view
				if ( view2file.containsKey(selected.getViewID()) ) {
					fastaFile = view2file.get( selected.getViewID() );
					if (fastaFile.exists() && fastaFile.canRead()) {
						fastaTF.setText(fastaFile.getAbsolutePath());
						return;
					}
				}
					
				// write sequences to tmp file and internally set the sequences file
				try {
					console.setText("");
					fastaFile = File.createTempFile(selected.getTitle()+"_", ".fasta");
					writeToConsole("*** I: Writing sequence view to tmpfile... ");
					new FastaWriter().wrappedWrite(fastaFile, seqs, 60);
					writeToConsole("done.\n");
				}
				catch (Exception e) {
					writeToConsole("*** E: Something went wrong while creating the tmp file.\n");
					writeToConsole("*** I: Please ensure sufficient space and permissions on the system temp dir\n");
					Configuration.getLogger().debug(e.toString());
				}
				fastaTF.setText(fastaFile.getAbsolutePath());
				view2file.put(selected.getViewID(), fastaFile);
				seqView = selected; // so we can access the view info later
				
			}
		});
	}
	
	
}
