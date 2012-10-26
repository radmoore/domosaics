package angstd.webservices.RADS.ui;

import info.radm.radscan.RADSQueryBuilder;
import info.radm.radscan.RADSResults;
import info.radm.radscan.ds.RADSProtein;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.arrangement.ArrangementManager;
import angstd.model.arrangement.DomainArrangement;
import angstd.model.arrangement.util.XdomUtil;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.util.SeqUtil;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.tools.Tool;
import angstd.ui.tools.RADSTool.RADSScanToolFrame;
import angstd.ui.tools.RADSTool.RADSScanView;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainView;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.views.view.View;
import angstd.ui.wizards.WizardListCellRenderer;
import angstd.ui.wizards.WizardManager;
import angstd.ui.wizards.pages.SelectNamePage;
import angstd.util.BrowserLauncher;
import angstd.webservices.RADS.RADSPanelI;
import angstd.webservices.RADS.RADSParms;
import angstd.webservices.RADS.RADSResultsProcessor;
import angstd.webservices.RADS.RADSResultsTableModel;
import angstd.webservices.RADS.RADSService;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;

/**
 * This class describes the RADS scan panel that can be used to conduct
 * a scan against RADS and RAMPAGE. Besides implementing the ActionListner
 * interface, it implements the RADSPanel interface (see {@link RADSPanelI})
 * 
 * The RADSScanPanel can be called via context menu of a domain arrangement
 * in which case it classifies as a tool (see {@link RADSScanToolFrame}. In tool
 * mode, the panel displays the arrangement which is used as query.
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSScanPanel extends JPanel implements ActionListener, RADSPanelI {

	public static RADSScanPanel instance;
	private static JFrame parent;
	
	private static final long serialVersionUID = 1L;
	private JPanel radsOptionPanel, rampageOptionPanel;
	private JButton loadSeq, loadArr, submit, reset,
	apply, cancel, browse, showReport;
	private JTextField loadSeqTF, loadArrTF, radsMatch, radsMismatch, radsIntGapOpen, radsIntGapExt, 
	radsTerGapOpen, radsTerGapExt, rampageIntGapOpen, rampageIntGapExt, rampageTerGapOpen,
	rampageTerGapExt;
	private JProgressBar progressBar;
	private JComboBox<WorkspaceElement> selectSeqView, selectArrView;
	private JComboBox<String> selectAlgo;
//	private JScrollPane pasteBoxSP;
//	private JTextArea pasteBox;
	

	private ArrangementManager arrSet;
	private DomainArrangement queryProtein;
	private TreeSet<RADSProtein> proteins;

	private RADSService radsService = null;
	private RADSResults results;
	private RADSResultsProcessor resultProcessor;
	private RADSQueryBuilder qBuilder;
	private RADSResultDetailsPanel logPanel = null;
	private RADSResultsTablePanel resultTable = null;
	private RADSResultsTableModel resultsTableModel;
	
	private ArrayList<String> xdomEntries;
	private ArrayList<String> fastaEntries;

	private View selectedView = null;
	private View currentView = null;
	
	
	/**
	 * This method provides access to the JFrame currently hosting 
	 * the RADSScanPanel. This is needed as this panel can be embedded in
	 * a tool frame called from an arrangement view, or a stand-alone frame
	 * called from the main menu. This method can be used to ensure that only
	 * one instance of this Panel is currently active.
	 *  
	 * @return - the current parent frame
	 */
	public static JFrame getCurrentRADSFrame() {
		return parent;
	}
	
	/**
	 * Used to construct a new instance of RADSScanPanel when used in
	 * tool mode. The RADSScanTool implements {@link Tool},
	 * making it necessary to set the parent frame after an instance has been
	 * created (the parent frame is set via {@link RADSScanPanel#setParentFrame(JFrame)}).
	 **/
	public RADSScanPanel() {
		super(new MigLayout("", "[left]"));
		initComponents();
		instance = this;
		queryProtein = new DomainArrangement();
	}
	
	
	/**
	 * Used to construct a new instance of the RADSScanPanel.
	 *  
	 * @param parent - the parent frame holding this panel
	 */
	public RADSScanPanel(JFrame parent) {
		super(new MigLayout("", "[left]"));
		initComponents();
		RADSScanPanel.parent = parent;
		queryProtein = new DomainArrangement();
		instance = this;
	}
	
	/**
	 * Sets the parent frame of this panel. This is only to
	 * be used when instance creation is invoked via 
	 * {@link RADSScanView}. 
	 * 
	 * @param toolFrame - the parent (tool) frame
	 */
	public void setParentFrame(JFrame toolFrame) {
		RADSScanPanel.parent = toolFrame;
	}
	
	/**
	 * This method is used to set the query arrangement when 
	 * the RADSScanPanel is used in tool mode. The query arrangement
	 * for the scan is set to the clicked arrangement component. 
	 * @param query
	 */
	public void setQueryArrangement(DomainArrangement query) {
		this.queryProtein = query;
	}
	
	/**
	 * When the RADSScanPanel is called in tool mode, 
	 * GUI elements used to set the query arrangement are disabled
	 * as the query arrangement is set to the clicked arrangement
	 * component (via {@link RADSScanPanel#setQueryArrangement(DomainArrangement)})
	 */
	public void setRADSScanToolMode() {
//		pasteBox.setEnabled(false);
		selectSeqView.setEnabled(false);
		selectArrView.setEnabled(false);
		loadSeq.setEnabled(false);
		loadArr.setEnabled(false);
		loadSeqTF.setEnabled(false);
		loadArrTF.setEnabled(false);
		submit.setEnabled(true);
	}

	/**
	 * see {@link RADSPanelI}
	 */
	public View getView() {
		if (selectedView == null)
			currentView = ViewHandler.getInstance().getActiveView();
		else
			currentView = selectedView;
		return currentView;
	}
	
	/**
	 * see {@link RADSPanelI}
	 */
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	
	/**
	 * see {@link RADSPanelI}
	 */
	public JFrame getParentFrame() {
		return parent;
	}
	
	/**
	 * see {@link RADSPanelI}
	 */
	public RADSResults getResults() {
		return results;
	}
	
	/**
	 * see {@link RADSPanelI}
	 */
	public RADSService getRadsService() {
		return radsService;
	}

	/**
	 * Methods called on RADSScanPanel ActionEvents 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("loadSeqFromFile"))
			loadSeqFromFile();
		if (e.getActionCommand().equals("loadArrFromFile"))
			loadArrFromFile();
		if (e.getActionCommand().equals("setDefaultValues"))
			setDefaultValues();
		if (e.getActionCommand().equals("close"))
			close(true);
		if (e.getActionCommand().equals("submitScan"))
			submitScan();
		if (e.getActionCommand().equals("createView"))
			createResultView();
		if (e.getActionCommand().equals("openLogWindow")) {
			openLogWindow();
		}
		if (e.getActionCommand().equals("openResultsTable")) {
			openResultTable();
		}
		if (e.getActionCommand().equals("openBrowseWindow"))
			BrowserLauncher.openURL(results.getJobUrl());	
	}

	/**
	 * This method is used to close the panel. If checkScanState is true,
	 * the method will check whether there is currently a scan in progress
	 * and will warn.
	 * 
	 * @param checkScanState - indicate whether scan state should be checked before closing
	 */
	public void close(boolean checkScanState) {
		if (radsService == null) {
			parent.dispose();
			if (logPanel != null)
				logPanel.destroy();
		}
		else if (checkScanState) {
			boolean choice = true;
			if ( RADSService.isRunning() || (radsService.isDone() && radsService.hasResults()) )
				choice = MessageUtil.showDialog(parent, "If you close this window you will loose your scan results. Are you sure?");
			if (choice) {
				radsService.cancelScan();
				parent.dispose();
				if (logPanel != null)
					logPanel.destroy();
			}
		}
		else {
			parent.dispose();
			if (logPanel != null)
				logPanel.destroy();
		}
	}
	
	/*
	 * Init the GUI components
	 */
	private void initComponents() {
		initButtons();
		initRadsPanel();
		initRampagePanel();
		loadSeqTF = new JTextField();
		loadArrTF = new JTextField();
		setDefaultValues();
		initSelectSeqView();
		initSelectArrView();
//		initPasteBox();
		progressBar = new JProgressBar();
		String[] algos = {"RADS", "RADS/RAMPAGE"};
		selectAlgo = new JComboBox<String>(algos);
		selectAlgo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectAlgo.getSelectedItem().equals("RADS")) {
					toggleComponents(radsOptionPanel, true);
					toggleComponents(rampageOptionPanel, false);
				}
				else
					toggleComponents(rampageOptionPanel, true);
			}
		});
		buildPanel();
	}

	
	/*
	 * Init the pasteBox (for pasting sequences or xdoms) 
	 */
//	private void initPasteBox() {
//		pasteBox = new JTextArea(10, 50);
//		pasteBox.getDocument().addDocumentListener(new DocumentListener() {
//			public void removeUpdate(DocumentEvent e) {	}
//			public void insertUpdate(DocumentEvent e) {	
//				loadSeqTF.setText("");
//				loadArrTF.setText("");
//				selectSeqView.setSelectedItem(null);
//				selectArrView.setSelectedItem(null);
//				selectedView = null;
//				submit.setEnabled(true);
//			}
//			public void changedUpdate(DocumentEvent e) { 
//				loadSeqTF.setText("");
//				loadArrTF.setText("");
//				selectSeqView.setSelectedItem(null);
//				selectArrView.setSelectedItem(null);
//				selectedView = null;
//				submit.setEnabled(true);
//			}
//		});
//		pasteBoxSP = new JScrollPane(pasteBox);
//		pasteBoxSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//	}

	/*
	 * Init the RADS panel with fields for RADS parameters 
	 */
	private void initRadsPanel() {
		radsMatch = new JTextField(5);
		radsMismatch = new JTextField(5);
		radsIntGapOpen = new JTextField(5);
		radsIntGapExt = new JTextField(5);
		radsTerGapOpen = new JTextField(5);
		radsTerGapExt = new JTextField(5);
		radsOptionPanel = new JPanel(new MigLayout());
		radsOptionPanel.add(new JLabel("Match"), "gap 10");
		radsOptionPanel.add(radsMatch, "");
		radsOptionPanel.add(new JLabel("Mismatch"), "");
		radsOptionPanel.add(radsMismatch, "wrap");
		radsOptionPanel.add(new JLabel("Int. gap opening"), "gap 10");
		radsOptionPanel.add(radsIntGapOpen, "");
		radsOptionPanel.add(new JLabel("Int. gap extend"), "");
		radsOptionPanel.add(radsIntGapExt, "wrap");
		radsOptionPanel.add(new JLabel("Ter. gap open"), "gap 10");
		radsOptionPanel.add(radsTerGapOpen, "");
		radsOptionPanel.add(new JLabel("Ter. gap extend"), "");
		radsOptionPanel.add(radsTerGapExt, "wrap");
	}
	
	/*
	 * Init the RAMPAGE panel with fields for RAMPAGE parameters
	 */
	private void initRampagePanel() {
		rampageIntGapOpen = new JTextField(5);
		rampageIntGapExt = new JTextField(5);
		rampageTerGapOpen = new JTextField(5);
		rampageTerGapExt = new JTextField(5);
		rampageOptionPanel = new JPanel(new MigLayout());
		rampageOptionPanel.add(new JLabel("Int. gap opening"), "gap 10");
		rampageOptionPanel.add(rampageIntGapOpen, "");
		rampageOptionPanel.add(new JLabel("Int. gap extend"), "");
		rampageOptionPanel.add(rampageIntGapExt, "wrap");
		rampageOptionPanel.add(new JLabel("Ter. gap open"), "gap 10");
		rampageOptionPanel.add(rampageTerGapOpen, "");
		rampageOptionPanel.add(new JLabel("Ter. gap extend"), "");
		rampageOptionPanel.add(rampageTerGapExt, "wrap");
		toggleComponents(rampageOptionPanel, false);
	}

	/*
	 * Init all buttons used in the panel 
	 */
	private void initButtons() {
		loadSeq = new JButton("Load fasta");
		loadSeq.setToolTipText("Load sequence from Fasta file");
		loadSeq.setActionCommand("loadSeqFromFile");
		loadSeq.addActionListener(this);
		
		loadArr = new JButton("Load xdom");
		loadArr.setToolTipText("Load arrangements from xdom file");
		loadArr.setActionCommand("loadArrFromFile");
		loadArr.addActionListener(this);
		
		submit = new JButton("Submit Job");
		submit.setToolTipText("Submit RADS/RAMPAGE job");
		submit.setActionCommand("submitScan");
		submit.addActionListener(this);
		submit.setEnabled(false);
		
		reset = new JButton("Set defaults");
		reset.setToolTipText("Set default values");
		reset.setActionCommand("setDefaultValues");
		reset.addActionListener(this);
		
		apply = new JButton("Apply");
		apply.setToolTipText("Create new view from results");
		apply.setActionCommand("createView");
		apply.addActionListener(this);
		apply.setEnabled(false);
		
		cancel = new JButton("Cancel");
		cancel.setToolTipText("Close window");
		cancel.setActionCommand("close");
		cancel.addActionListener(this);
		
		browse = new JButton("Browse results online");
		browse.setToolTipText("Open results at RADS online (requires internet)");
		browse.setActionCommand("openBrowseWindow");
		browse.addActionListener(this);
		browse.setEnabled(false);
		
		//showReport = new JButton("Show scan log");
		showReport = new JButton("Show Results Table");
		showReport.setToolTipText("Show RADS scan log");
		//showReport.setActionCommand("openLogWindow");
		showReport.setActionCommand("openResultsTable");
		showReport.addActionListener(this);
		showReport.setEnabled(false);
	}
	
	/*
	 * Init the JComboBox used to select a sequence view from the
	 * workspace 
	 */
	private void initSelectSeqView() {
		List<WorkspaceElement> viewList = WorkspaceManager.getInstance().getSequenceViews();
		WorkspaceElement[] seqViews = viewList.toArray(new ViewElement[viewList.size()]);
		
		if (seqViews.length == 0) {
			selectSeqView = new JComboBox<WorkspaceElement>(seqViews);
			selectSeqView.setSelectedItem(null);
			selectSeqView.setEnabled(false);
			return;
		}
		
		selectSeqView = new JComboBox<WorkspaceElement>(seqViews);
		selectSeqView.setSelectedItem(null);
		selectSeqView.setRenderer(new WizardListCellRenderer());
		selectSeqView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				loadSeqTF.setText("");
//				clearPasteBox();
				@SuppressWarnings({ "unchecked" })
				JComboBox<WorkspaceElement> cb = (JComboBox<WorkspaceElement>)evt.getSource();
				ViewElement selected = (ViewElement)cb.getSelectedItem();
				if (selected == null)
					return;
				selectedView = ViewHandler.getInstance().getView(selected.getViewInfo());
				SequenceView seqView = (SequenceView) selectedView;
				if (seqView.getSequences().length > 1)
					MessageUtil.showInformation(parent, "The selected view has multiple sequences. Only one will be considered");
				selectArrView.setSelectedItem(null);
				loadArrTF.setText("");
				submit.setEnabled(true);
			}
		});
	}
	
	/*
	 * Init the JComboBox used to select an arrangement view from the
	 * workspace 
	 */
	private void initSelectArrView() {
		List<WorkspaceElement> viewList = WorkspaceManager.getInstance().getDomainViews();
		WorkspaceElement[] arrViews = viewList.toArray(new ViewElement[viewList.size()]);
		
		if (arrViews.length == 0) {
			selectArrView = new JComboBox<WorkspaceElement>(arrViews);
			selectArrView.setSelectedItem(null);
			selectArrView.setEnabled(false);
			return;
		}
		
		selectArrView = new JComboBox<WorkspaceElement>(arrViews);
		selectArrView.setSelectedItem(null);
		selectArrView.setRenderer(new WizardListCellRenderer());
		selectArrView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				loadArrTF.setText("");
//				clearPasteBox();
				@SuppressWarnings({ "unchecked" })
				JComboBox<WorkspaceElement> cb = (JComboBox<WorkspaceElement>)evt.getSource();
				ViewElement selected = (ViewElement)cb.getSelectedItem();
				if (selected == null)
					return;
				
				selectedView = ViewHandler.getInstance().getView(selected.getViewInfo());
				DomainView domView = (DomainView) selectedView;
				if (domView.getDaSet().length > 1)
					MessageUtil.showInformation(parent, "The selected view has multiple arrangements. Only one will be considered");
				selectSeqView.setSelectedItem(null);
				loadSeqTF.setText("");
				submit.setEnabled(true);
			}
		});
	}
	
	/*
	 * Put together the panel with all components 
	 */
	private void buildPanel() {

//		add(new JXTitledSeparator("Paste protein (xdom / fasta / UniProt ID)"), "growx, span, wrap");
//		add(pasteBoxSP, "gap 5, wrap, span, growX");
		
		// sequences
		add(new JXTitledSeparator("Sequences"), "growx, span, wrap");
		add(loadSeq, "w 150!, gap 5");
		add(loadSeqTF, "h 25!, w 350!, span, growx, gapright 5, wrap");
		add(new JLabel("Or select view:"), "gap 5");
		add(selectSeqView, "h 25!, w 350!, span, growx, gapright 5, wrap");
		
		// arrangements
		add(new JXTitledSeparator("Arrangements"), "growx, span, wrap");
		add(loadArr, "w 150!, gap 5");
		add(loadArrTF, "h 25!, w 350!, span, growx, gapright 5, wrap");
		add(new JLabel("Or select view:"), "gap 5");
		add(selectArrView, "h 25!, w 350!, span, growx, gapright 5, wrap");
		
		// parameter
		add(new JXTitledSeparator("Options"),"growX, span, wrap, gaptop 5");
		add(new JLabel("Algorithm"), "gap 17");
		add(selectAlgo, "wrap");
		add(new JXTitledSeparator("RADS scoring"),"gap 10, growX, span, wrap, gaptop 10");
		add(radsOptionPanel, "span, wrap");
		add(new JXTitledSeparator("RAMPAGE scoring"),"gap 10, growX, span, wrap");
		add(rampageOptionPanel, "span, wrap");
		
		add(submit, "w 150!");
		add(reset, "w 150!, wrap");
		add(progressBar, "h 25!, gaptop 10, span, growX, wrap");
		
		// apply
		add(new JXTitledSeparator("Apply Results"), "growx, span, wrap, gaptop 10");
		add(apply, "split 2");
		add(cancel, "");
		add(browse, "");
		add(showReport, "");
	}
	
	/*
	 * Toggle panel components. This is used for toggling
	 * the RAMPAGE panel when RAMPAGE is selected/deselected
	 * @param panel
	 * @param enabled
	 */
	private void toggleComponents(JPanel panel, boolean enabled) {
		Component[] comp = panel.getComponents();
		for (Component c: comp)
			c.setEnabled(enabled);
	}
	
	
	/*
	 * Used to clear the pasteBox 
	 */
//	private void clearPasteBox() {
//		pasteBox.setText("");
//	}
	
	
	/*
	 * Action: used to load sequences from a file 
	 */
	private void loadSeqFromFile() {
		loadSeqTF.setText("");
		submit.setEnabled(false);
		File file = FileDialogs.showOpenDialog(instance);
		if (!(selectSeqView.getSelectedItem() == null)) {
			selectSeqView.setSelectedItem(null);
		}
		if(file != null && file.canRead()) {
			fastaEntries = SeqUtil.getFastaFromFile(file);
			if (fastaEntries.size() == 0) {
				MessageUtil.showWarning(parent, "File does not contain valid fasta entries");
				return;
			}
			else if (fastaEntries.size() > 1)
				MessageUtil.showInformation(parent, "The selected file has multiple arrangements. Only one will be considered");
			
			loadSeqTF.setText(file.getAbsolutePath());
			loadArrTF.setText("");
//			pasteBox.setText("");
			selectArrView.setSelectedItem(null);
			selectedView = null;
			submit.setEnabled(true);
			
		}
	}
	
	
	/*
	 * Action: used to load Arrangements from a file 
	 */
	private void loadArrFromFile() {
		loadArrTF.setText("");
		submit.setEnabled(false);
		File file = FileDialogs.showOpenDialog(instance);
		if (!(selectArrView.getSelectedItem() == null)) {
			selectArrView.setSelectedItem(null);
		}
		if(file != null && file.canRead()) {
			xdomEntries = XdomUtil.getXdomsFromFile(file);
			if (xdomEntries.size() == 0) {
				MessageUtil.showWarning(parent, "File does not contain valid xdom entries");
				return;
			}
			else if (xdomEntries.size() > 1)
				MessageUtil.showInformation(parent, "The selected file has multiple arrangements. Only one will be considered");
			
			loadArrTF.setText(file.getAbsolutePath());
			loadSeqTF.setText("");
//			pasteBox.setText("");
			selectSeqView.setSelectedItem(null);
			selectedView = null;
			submit.setEnabled(true);
		}
	}


	/*
	 * Action: used to reset all values of all panels
	 * (RADS/RAMPAGE panels) 
	 */
	private void setDefaultValues() {
		radsMatch.setText(""+RADSParms.DEFAULT_MATCHSCORE.getDeafultValue());
		radsMismatch.setText(""+RADSParms.DEFAULT_MISMATCH_PEN.getDeafultValue());
		radsIntGapOpen.setText(""+RADSParms.DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		radsIntGapExt.setText(""+RADSParms.DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		radsTerGapOpen.setText(""+RADSParms.DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		radsTerGapExt.setText(""+RADSParms.DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
		rampageIntGapOpen.setText(""+RADSParms.RAM_DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		rampageIntGapExt.setText(""+RADSParms.RAM_DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		rampageTerGapOpen.setText(""+RADSParms.RAM_DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		rampageTerGapExt.setText(""+RADSParms.RAM_DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
	}
	

	private void openLogWindow() {
		if (logPanel == null)
			logPanel = RADSResultDetailsPanel.createResultsFrame(queryProtein, results, proteins);
			
		logPanel.showFrame();
	}
	
	private void openResultTable() {
		if (resultTable == null)
			resultTable = RADSResultsTablePanel.createResultsTableFrame(queryProtein, results, resultsTableModel);
		
		resultTable.showFrame();
	}
	
	/*
	 * Builds the RADS/RAMPAGE query. Will return true
	 * if the query was build, false otherwise (e.g. if no
	 * appropriate query was provided)
	 *  
	 * @return - true if query was build, false otherwise
	 */
	private boolean buildQuery() {
		
		qBuilder = new RADSQueryBuilder();
		
		// check if view is selected
		if (!(selectedView == null)) {
			// sequence view
			if (selectedView.getViewInfo().getType() == ViewType.SEQUENCE) {
				SequenceView seqView = (SequenceView) selectedView;
				SequenceI[] seqsI = seqView.getSeqs();
				SequenceI query = seqsI[0];
				qBuilder.setQueryFastaString(query.toFasta(false));
				queryProtein.setName(query.getName());
				if (selectAlgo.getSelectedItem().equals("RADS/RAMPAGE"))
					qBuilder.setAlgorithm("rampage");
			}
			// domain view
			else if (selectedView.getViewInfo().getType() == ViewType.DOMAINS) {
				DomainView domView = (DomainView) selectedView;
				queryProtein = domView.getDaSet()[0];
				queryProtein.setName(queryProtein.getName());
				// check if RAMPAGE run is desired (and warn if no sequences are available)
				if (selectAlgo.getSelectedItem().equals("RADS/RAMPAGE")) {
					if (domView.getDaSet()[0].hasSeq()) {
						MessageUtil.showInformation(parent, "RAMPAGE will use the sequence of the selected Domain Arrangement");
						qBuilder.setQueryFastaString(queryProtein.getSequence().toFasta(false));
						qBuilder.setAlgorithm("rampage");
					}
					else {
						MessageUtil.showWarning(parent, "RAMPAGE requires a sequence to run");
						selectAlgo.setSelectedIndex(0);
						return false;
					}
				}
				else {
					if ( !queryProtein.hasPfamDomains() ) {
						MessageUtil.showWarning(parent, "RADS currently only supports Pfam - consider running RAMPAGE if sequences are available");
						return false;
					}
					// no RAMPAGE run requested, use xdom
					qBuilder.setQueryXdomString(queryProtein.toXdom());
				}
			}
		}
		// check if sequence file was selected
		else if (!(loadSeqTF.getText().equals(""))) {
			qBuilder.setQueryFastaString(fastaEntries.get(0));
			queryProtein.setName(SeqUtil.getIDFromFasta(fastaEntries.get(0)));
			if (selectAlgo.getSelectedItem().equals("RADS/RAMPAGE"))
				qBuilder.setAlgorithm("rampage");
		}
		// check if xdom file was selected
		else if (!(loadArrTF.getText().equals(""))) {
			qBuilder.setQueryXdomString(xdomEntries.get(0));
			queryProtein.setName(XdomUtil.getIDFromXdom(xdomEntries.get(0)));
			if (selectAlgo.getSelectedItem().equals("RADS/RAMPAGE")) {
				MessageUtil.showWarning(parent, "RAMPAGE requires a sequence to run");
				selectAlgo.setSelectedIndex(0);
				return false;
			}
		}
//		// check if the paste box is in use
//		else if (!(pasteBox.getText().equals(""))) {
//			if (SeqUtil.validFastaString(pasteBox.getText()) == SeqUtil.PROT) {
//				qBuilder.setQueryFastaString(pasteBox.getText());
//				queryProtein.setName(SeqUtil.getIDFromFasta(pasteBox.getText()));
//			}
//			else if (XdomUtil.validXdomString(pasteBox.getText())) {
//				if (selectAlgo.getSelectedItem().equals("RADS/RAMPAGE")) {
//					MessageUtil.showWarning(parent, "RAMPAGE requires a sequence to run");
//					selectAlgo.setSelectedIndex(0);
//					return false;
//				}
//				qBuilder.setQueryXdomString(pasteBox.getText());
//				queryProtein.setName(XdomUtil.getIDFromXdom(pasteBox.getText()));
//			}
//			else {
//				MessageUtil.showWarning(parent, "RADS/RAMPAGE requires a valid amino acid fasta or xdom entry to run");
//				return false;
//			}
//		}
		// else, check if the query protein was preselected (context dependant scan in tool frame)
		else if (!(queryProtein == null)) {
			// warn if RAMPAGE run selected (if we have no sequences)
			if (selectAlgo.getSelectedItem().equals("RADS/RAMPAGE")) {
				if (!queryProtein.hasSeq()) {
					MessageUtil.showWarning(parent, "RAMPAGE requires a sequence to run. Please select arrangement accordingly (or run RADS)");
					selectAlgo.setSelectedIndex(0);
					return false;
				}
				else {
					qBuilder.setQueryFastaString(queryProtein.getSequence().toFasta(false));
					//System.out.println(queryProtein.getSequence().toFasta(false));
					qBuilder.setAlgorithm("rampage");
				}
				
			}
			else {
				if ( !queryProtein.hasPfamDomains() ) {
					MessageUtil.showWarning(parent, "RADS currently only supports Pfam - consider running RAMPAGE if sequences are available");
					return false;
				}
				qBuilder.setQueryXdomString(queryProtein.toXdom());
			}
		}
		// we have no input, cannot build query
		else {
			submit.setEnabled(false);
			MessageUtil.showWarning("Please provide a XDOM / FASTA or select a view");
			return false;
		}
		// query was built
		return true;
	}
	
	
	/* 
	 * Checks whether the parameters provided for RADS/RAMPAGE were valid
	 * 
	 * @return - true if the RADS/RAMPAGE params were valid numbers, false otherwise
	 */
	private boolean validateParams() {
		try {				
			int matchScoreValue = Integer.valueOf(radsMatch.getText());
			qBuilder.setRads_M(matchScoreValue);
			
			int mismatchPenValue = Integer.valueOf(radsMismatch.getText());
			qBuilder.setRads_m(mismatchPenValue);
			
			int intOpenGapPenValue = Integer.valueOf(radsIntGapOpen.getText());
			qBuilder.setRads_G(intOpenGapPenValue);
			
			int intExtenGapPenValue = Integer.valueOf(radsIntGapExt.getText());
			qBuilder.setRads_g(intExtenGapPenValue);
			
			int terOpenGapPenValue = Integer.valueOf(radsTerGapOpen.getText());
			qBuilder.setRads_T(terOpenGapPenValue);
			
			int terExtenGapPenValue = Integer.valueOf(radsTerGapExt.getText());
			qBuilder.setRads_t(terExtenGapPenValue);
			
			int rampIntOpenGapPenValue = Integer.valueOf(rampageIntGapOpen.getText());
			qBuilder.setRads_G(rampIntOpenGapPenValue);
			
			int rampIntExtenGapPenValue = Integer.valueOf(rampageIntGapExt.getText());
			qBuilder.setRads_g(rampIntExtenGapPenValue);
			
			int rampTerOpenGapPenValue = Integer.valueOf(rampageTerGapOpen.getText());
			qBuilder.setRads_T(rampTerOpenGapPenValue);
			
			int rampTerExtenGapPenValue = Integer.valueOf(rampageTerGapExt.getText());
			qBuilder.setRads_t(rampTerExtenGapPenValue);
		}
		catch (NumberFormatException nfe) {
			MessageUtil.showWarning(parent, "Values for scores and penalties must be numbers");
			return false;
		}
		return true;
	}
	
	/*
	 * Submits the scan job via RADSService. First checks whether the 
	 * RADSQuery could be build, and whether the set parameters were valid.
	 * Using a propertyChangeListner, this method will invoke the parsing
	 * if scan results were found, and will inform if no results were found.  
	 */
	private void submitScan() {
		// ensure that we do not have any results lying around
		if ( radsService != null && radsService.isDone() && radsService.hasResults() ) {
			boolean choice = true;
			choice = MessageUtil.showDialog(parent, "Submitting this job will delete your current results. Are you sure?");
			if (!choice)
				return;
		}
		
		if (buildQuery()) {
			if (validateParams()) {
				submit.setText("Running scan");
				submit.setEnabled(false);
				reset.setEnabled(false);
				qBuilder.setQuietMode(true);
				radsService = new RADSService(qBuilder.build());
				progressBar.setIndeterminate(true);
				radsService.execute();
				radsService.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if ("state".equals(evt.getPropertyName())) {
							if ( "DONE".equals(evt.getNewValue().toString()) ) {
								if (radsService.isCancelled())
									return;
								proteins = radsService.getHits();
								submit.setText("Submit Job");
								submit.setEnabled(true);
								results = radsService.getScanResults();
								resultProcessor = new RADSResultsProcessor(instance);
								//TODO should retrun a table model
								arrSet = resultProcessor.process();
								resultsTableModel = resultProcessor.createResultTable();
								
								progressBar.setString("Scan complete");
								if (arrSet != null) {
									apply.setEnabled(true);
									browse.setEnabled(true);
									showReport.setEnabled(true);
								}
								submit.setEnabled(true);
								reset.setEnabled(true);
							}
						}
					}
				});
				
			}
		}
	}
	
	//TODO this will change now (when a JTable is used)
	/* 
	 * Constructs a new arrangement view of scan results
	 */
	private void createResultView() {
		DomainArrangement[] hits = arrSet.get();
		String defaultViewName = queryProtein.getName()+"-radscan";
		
		View currentView = getView();
		String viewName = null;
		String projectName = null;
		ProjectElement project = null;

		project = WorkspaceManager.getInstance().getViewElement(currentView.getViewInfo()).getProject();

		@SuppressWarnings("rawtypes")
		Map m = WizardManager.getInstance().selectNameWizard(defaultViewName, "RadScan", project, true);
		viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
		projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
		project = WorkspaceManager.getInstance().getProject(projectName);
			
		if (viewName == null) 
			MessageUtil.showWarning("A valid view name is needed to complete this action");
	
		DomainViewI domResultView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
		// TODO if we want domain colors, something like this
//		DomainColorManager domColorMan = domResultView.getDomainColorManager();
//		DomainViewI domView = ViewHandler.getInstance().getView(currentView.getViewInfo()) ;
//		Color color = domView.getDomainColorManager().getDomainColor(domFamily);
//		System.out.println("Color: "+color.toString());
		domResultView.setDaSet(hits);
		ViewHandler.getInstance().addView(domResultView, project);
		close(false);
	}
	
	
}

