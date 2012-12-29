package domosaics.localservices.hmmer3.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
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

import domosaics.localservices.hmmer3.Hmmer3Engine;
import domosaics.localservices.hmmer3.programs.HmmPress;
import domosaics.localservices.hmmer3.programs.HmmScan;
import domosaics.localservices.hmmer3.programs.Hmmer3Program;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.sequence.io.FastaWriter;
import domosaics.model.sequence.util.SeqUtil;
import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.wizards.WizardListCellRenderer;
import domosaics.util.StringUtils;


/**
 * HmmScanPanel holds the GUI components necessary to start local
 * hmmscan jobs. It is of type {@link HmmerServicePanel}.
 * The general setup is closely related to other panels used in
 * DoMosaicS.
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
	private JCheckBox gaCkb, biasCkb, maxCkb, coddCkb; 
	private ButtonGroup groupRadio;
	private JRadioButton overlapRadioNone, overlapRadioEvalue, overlapRadioCoverage;
	private JButton loadScanBin, loadPressBin, loadHmmDB, loadFastaFile, run, cancel;
	private JComboBox cpuCB, selectView;
	private JLabel thresholdLabel, evalLabel, cpuLabel, filterLabel;
	private JTextArea console;
	private JPanel ePane, radioPane;
	private File hmmDBFile, fastaFile;
	private HashMap<Integer, File> view2file; 
	public static Color highlightColor = new Color(244,215,215);
	
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
		hmmScanTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
				hmmScanTF.setBackground(Color.WHITE);
			}
		});
		
		hmmPressTF = new JTextField(25);
		hmmPressTF.setText(config.getHmmPressBin());
		hmmPressTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
				hmmPressTF.setBackground(Color.WHITE);
			}
		});
		
		hmmTF = new JTextField(25);
		hmmTF.setText(config.getHmmerDB());
		hmmTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
				hmmTF.setBackground(Color.WHITE);
			}
		});
		fastaTF = new JTextField(25);
		fastaTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
				fastaTF.setBackground(Color.WHITE);
			}
		});
		evalueTF = new JTextField(5);
		evalueTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
				evalueTF.setBackground(Color.WHITE);
			}
		});
		evalueTF.setText("0.1"); // default evalue
		evalueTF.setToolTipText("Max: 10");
		evalLabel = new JLabel("E-value:");
		filterLabel = new JLabel("Filter");
		
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
	    maxCkb = new JCheckBox("Max filter", true);
	    maxCkb.addItemListener(new ItemListener() {	
			public void itemStateChanged(ItemEvent e) {
				if(!maxCkb.isSelected())
					biasCkb.setSelected(false);
				biasCkb.setEnabled(maxCkb.isSelected());
			}
		});
		
	    // RadioButton to choose (or not) a post processing method
	    // to resolve overlaps
	    radioPane = new JPanel();
		groupRadio = new ButtonGroup();
	    overlapRadioNone = new JRadioButton("None      ", true);
	    overlapRadioEvalue = new JRadioButton("E-value based        ");
	    overlapRadioCoverage = new JRadioButton("Max. coverage");
	    overlapRadioNone.setActionCommand("None");
	    overlapRadioEvalue.setActionCommand("Evalue");
	    overlapRadioCoverage.setActionCommand("Coverage");
	    groupRadio.add(overlapRadioNone);
	    groupRadio.add(overlapRadioEvalue);
	    groupRadio.add(overlapRadioCoverage);
	    radioPane.add(overlapRadioNone);
	    radioPane.add(overlapRadioEvalue);
	    radioPane.add(overlapRadioCoverage);
	    
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
		add(hmmScanTF, "h 25!, gap 5, gapright 5, span 2, growX, wrap");
		add(loadPressBin, "gap 5, w 165!");
		add(hmmPressTF, "h 25!, gap 5, gapright 5, span 2, growX, wrap");
		
		add(loadHmmDB, "gap 5, w 165!");
		add(hmmTF, "h 25!, gap 5, gapright 5, span 2, growX, wrap");
		
		add(new JXTitledSeparator("Sequences"), "growx, span, wrap, gaptop 10");
	
		add(loadFastaFile, "gap 5, w 165!");
		add(fastaTF, "h 25!, gap 5, gapright 5, span 2, growX, wrap");
		
		add(new JLabel("Or Select Loaded View:"),"gap 5");
		add(selectView, "h 25!, gap 5, gapright 5, span 2, growX, wrap");
		
		add(new JXTitledSeparator("Options"), "growX, span, wrap, gaptop 10");
		
		add(thresholdLabel,	"gap 5");
		add(gaCkb, "gap 5, split 2");
		add(ePane, "growX, wrap");
		
		add(filterLabel, "gap 5");
		add(biasCkb, "gap 5, split 2");
		add(maxCkb, "gap 80, wrap");
		
		add(cpuLabel, "gap 5");
		add(cpuCB, "gap 5, span 2, wrap");
		
		add(new JXTitledSeparator("Post processing"), "growX, span, wrap, gaptop 10");
		add(new JLabel("Resolve overlaps by:"), "gap 5");
		add(radioPane, "gap 2, growX, wrap");

		add(new JLabel("Co-Occurring Domain Filter:"), "gap 5");
		add(coddCkb, "gap 5, span 2, growX, wrap");

		add(new JXTitledSeparator("Progress"), "growX, span, wrap, gaptop 10");
		add(progressBar, "h 25!, gap 5, gapright 5, span 3, growX, wrap");
		
		add(new JXTitledSeparator("Console"), "growX, span, wrap, gaptop 10");
		add(new JScrollPane(console), "align center, span, wrap");	
		add(run, "w 80!, gap 5, split 2");
		add(cancel, "gap 5, split 2");
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
			evalueTF.setBackground(highlightColor);
			return;
		}
		
		if(hmmScanTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a hmmscan binary.");
			hmmScanTF.setBackground(highlightColor);
			return;	
		}
		
		/*if(hmmPressTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a hmmpress binary.");
			hmmPressTF.setBackground(highlightColor);
			return;	
		}*/

		else if (hmmTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose HMMER3 profiles");
			hmmTF.setBackground(highlightColor);
			return;
		}
		else if (fastaTF.getText().equals("")) {
			MessageUtil.showWarning("Please choose a sequence file or view");
			fastaTF.setBackground(highlightColor);
			return;
		}
		
		// if the user has set these fields globally, we
		// still have to check them again and setup the 
		// hmmer3engine instance with the progs
		if (!checkBins(new File(hmmScanTF.getText()))) {
			hmmScanTF.setBackground(highlightColor);
			return;
		}
		/*if (!checkBins(new File(hmmPressTF.getText()))) {
			hmmPressTF.setBackground(highlightColor);
			return;
		}*/
		if (!checkDB(new File(hmmTF.getText()))) {
			hmmTF.setBackground(highlightColor);
			return;
		} else {
			hmmDBFile = new File(hmmTF.getText());
		}
		if (!checkDBpressed(new File(hmmTF.getText()))) {
			hmmPressTF.setBackground(highlightColor);
			return;
		}
		if ( !FastaReader.isValidFasta(new File(fastaTF.getText())) ) {
			MessageUtil.showWarning("Malformated fasta file or unknown sequence format");
			fastaTF.setBackground(highlightColor);
			return;
		}
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
				evalueTF.setBackground(highlightColor);
				return;
			}
			else {
				hmmScan.setEvalue(evalueTF.getText());
			}
		}
		hmmScan.setBiasFilter(biasCkb.isSelected());
		hmmScan.setMaxFilter(maxCkb.isSelected());
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
		Configuration.getInstance().setServiceRunning(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		run.setText("  Run  ");
		progressBar.setValue(0);
		progressBar.setIndeterminate(false);
		progressBar.setString("");
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

		if(bin.getName().indexOf(".")!=-1)
			hmmer3bins.put(bin.getName().substring(0, bin.getName().indexOf(".")), bin);
		else
			hmmer3bins.put(bin.getName(), bin);	
		Hmmer3Engine.getInstance().setAvailableServices(hmmer3bins);
		return true;

	}
	
	
	/**
	 * checks if the selected DB dir contains a pressed
	 * version of the model database, and allows the user
	 * to press it if not.
	 * 
	 * @param dbFile
	 * @return true, if the the DB dir contains necessary files
	 */
	private boolean checkDB(File dbFile) {

		// check if file is still there
		if (!dbFile.exists()) {
			MessageUtil.showWarning(this.parent, "Warning: could not find specified HMMER db file "+dbFile.getName());
			return false;
		}
		
		// format check hmme3 profiles
		if (!HmmPress.isValidProfileFile(dbFile)) {
			MessageUtil.showWarning(dbFile.getName()+ " does not appear to be a valid hmmer3 profile");
			return false;
		} else {
			return true;
		}
	}
		
		
	private boolean checkDBpressed(File dbFile) {
		boolean pressAvail = false;
				
		// check if pressed files are available
		if (!HmmPress.hmmFilePressed(dbFile)) {
			// if not, check whether press bin in the selected textfield is valid
			if (!hmmPressTF.getText().equals(""))
				pressAvail = checkBins(new File(hmmPressTF.getText()));
			
			// Check if want to/can press
			if (MessageUtil.showDialog(this, "The HMMERDBFILE is not pressed. Do you want DoMosaicS to press it now?")) {					
				if (pressAvail) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					run.setEnabled(false);
				
					// TODO: I would like to disable GUI components here
					// and enable them _after_ the run is complete.
					// See also domosaics.localservices.hmmer3.programs.HmmPress
					HmmPress hmmPress = new HmmPress(Hmmer3Engine.getInstance().getAvailableServicePath("hmmpress"), dbFile, this);
					Hmmer3Engine.getInstance().launchInBackground(hmmPress);
					progressBar.setIndeterminate(true);
					// ATTENTION: we must return false, even if the press was successful to ensure that the engine instance is free
					// before we init the actual scan
					return false;
				}
				else {
					if (hmmPressTF.getText().equals("")) {
						MessageUtil.showInformation(this.parent, "Please first provide hmmpress binary");
					} else {
						MessageUtil.showInformation(this.parent, "Please first provide correct hmmpress binary");
					}
					hmmPressTF.setBackground(highlightColor);
					return false; 
				}	
			}
			else {
				return false;
			}	
		}
		run.setEnabled(true);
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
					writeToConsole("I: Writing sequence view to tmpfile... ");
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
	
	public boolean usingCODD() {
		return coddCkb.isSelected();
	}
}
