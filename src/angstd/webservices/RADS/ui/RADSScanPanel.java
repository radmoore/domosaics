package angstd.webservices.RADS.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.DomainView;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.wizards.WizardListCellRenderer;
import angstd.webservices.RADS.RADSParms;
import angstd.webservices.RADS.RADSService;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RADSScanPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// components
	private JPanel radsOptionPanel, rampageOptionPanel;
	private JButton loadSeq, loadArr, submit, reset,
	apply, cancel, browse, showReport;
	private JTextField loadSeqTF, loadArrTF, radsMatch, radsMismatch, radsIntGapOpen, radsIntGapExt, 
	radsTerGapOpen, radsTerGapExt, rampageIntGapOpen, rampageIntGapExt, rampageTerGapOpen,
	rampageTerGapExt;
	private JProgressBar progressBar;
	private JComboBox<WorkspaceElement> selectSeqView, selectArrView;
	private JComboBox<String> selectAlgo;
	private JScrollPane pasteBoxSP;
	private String selectedViewName;
		
	private RADSService radsService;
	
	private SequenceView selectedSeqView;
	private DomainView selectedArrView;
	
	private JTextArea pasteBox;
	private JFrame parent;
	
	private RADSScanPanel instance;
	
	public RADSScanPanel(JFrame parent) {
		super(new MigLayout("", "[left]"));
		initComponents();
		this.parent = parent;
		instance = this;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("loadSeqFromFile"))
			loadSeqFromFile();
		if (e.getActionCommand().equals("loadArrFromFile"))
			loadArrFromFile();
		if (e.getActionCommand().equals("setDefaultValues"))
			setDefaultValues();
		if (e.getActionCommand().equals("close"))
			close();
		
	}
	
	private void initComponents() {
		initButtons();
		initRadsPanel();
		initRampagePanel();
		loadSeqTF = new JTextField();
		loadArrTF = new JTextField();
		setDefaultValues();
		initSelectSeqView();
		initSelectArrView();
		pasteBox = new JTextArea(10, 50);
		pasteBox.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {	}
			public void insertUpdate(DocumentEvent e) {	
				loadSeqTF.setText("");
				loadArrTF.setText("");
				selectSeqView.setSelectedItem(null);
				selectArrView.setSelectedItem(null);
			}
			public void changedUpdate(DocumentEvent e) { 
				loadSeqTF.setText("");
				loadArrTF.setText("");
				selectSeqView.setSelectedItem(null);
				selectArrView.setSelectedItem(null);
			}
		});
		pasteBoxSP = new JScrollPane(pasteBox);
		pasteBoxSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		
		progressBar = new JProgressBar();
		String[] algos = {"RADS", "RAMPAGE"};
		selectAlgo = new JComboBox<String>(algos);
		selectAlgo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectAlgo.getSelectedItem().equals("RADS")) {
					toggleComponents(radsOptionPanel, true);
					toggleComponents(rampageOptionPanel, false);
				}
				else {
					toggleComponents(rampageOptionPanel, true);
				//	toggleComponents(radsOptionPanel, false);
				}
			}
		});
		buildPanel();
	}

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
	
	private void toggleComponents(JPanel panel, boolean enabled) {
		Component[] comp = panel.getComponents();
		for (Component c: comp)
			c.setEnabled(enabled);
	}
	
	private void loadSeqFromFile() {
		File file = FileDialogs.showOpenDialog(instance);
		if (!(selectSeqView.getSelectedItem() == null)) {
			selectedViewName = null;
			selectSeqView.setSelectedItem(null);
		}
		if(file != null && file.canRead()) {
			loadSeqTF.setText(file.getAbsolutePath());
			//annotationSpawner.setSeqs((SequenceI[]) new FastaReader().getDataFromFile(file));
			loadArrTF.setText("");
			pasteBox.setText("");
			selectArrView.setSelectedItem(null);
			submit.setEnabled(true);
		}
	}
	
	private void loadArrFromFile() {
		File file = FileDialogs.showOpenDialog(instance);
		if (!(selectArrView.getSelectedItem() == null)) {
			selectedViewName = null;
			selectArrView.setSelectedItem(null);
		}
		if(file != null && file.canRead()) {
			loadArrTF.setText(file.getAbsolutePath());
			//annotationSpawner.setSeqs((SequenceI[]) new FastaReader().getDataFromFile(file));
			loadSeqTF.setText("");
			pasteBox.setText(""); //TODO does not work
			selectSeqView.setSelectedItem(null);
			submit.setEnabled(true);
		}
	}
	
	private void close() {
		parent.dispose();
	}
	
	private void buildPanel() {
		// sequences
		add(new JXTitledSeparator("Paste protein (xdom / fasta / UniProt ID)"), "growx, span, wrap");
		add(pasteBoxSP, "gap 5, wrap, span, growX");
		add(new JXTitledSeparator("Sequences"), "growx, span, wrap");
		add(loadSeq, "w 150!, gap 5");
		add(loadSeqTF, "h 25!, w 350!, span, gapright 5, wrap");
		add(new JLabel("Or select view:"), "gap 5");
		add(selectSeqView, "h 25!, w 350!, span, gapright 5, wrap");
		
		// arrangements
		add(new JXTitledSeparator("Arrangements"), "growx, span, wrap");
		add(loadArr, "w 150!, gap 5");
		add(loadArrTF, "h 25!, w 350!, span, gapright 5, wrap");
		add(new JLabel("Or select view:"), "gap 5");
		add(selectArrView, "h 25!, w 350!, span, gapright 5, wrap");
		
		// parameter
		add(new JXTitledSeparator("Options"),"growX, span, wrap, gaptop 5");
		add(new JLabel("Algorithm"), "gap 17");
		add(selectAlgo, "wrap");
		add(new JXTitledSeparator("RADS scoring"),"gap 10, growX, span, wrap, gaptop 10");
		add(radsOptionPanel, "span, wrap");
		add(new JXTitledSeparator("RAMPAGE scoring"),"gap 10, growX, span, wrap");
		add(rampageOptionPanel, "span, wrap");
		
		add(submit, "split 2");
		add(reset, "wrap");
		add(progressBar, "h 25!, gaptop 10, span, growX, wrap");
		
		// apply
		add(new JXTitledSeparator("Apply Results"), "growx, span, wrap, gaptop 10");
		add(apply, "split 2");
		add(cancel, "");
		add(browse, "");
		add(showReport, "");
	}
	
	private void setDefaultValues() {
		radsMatch.setText(""+RADSParms.DEFAULT_MATCHSCORE.getDeafultValue());
		radsMismatch.setText(""+RADSParms.DEFAULT_MISMATCH_PEN.getDeafultValue());
		radsIntGapOpen.setText(""+RADSParms.DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		radsIntGapExt.setText(""+RADSParms.DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		radsTerGapOpen.setText(""+RADSParms.DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		radsIntGapExt.setText(""+RADSParms.DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
		rampageIntGapOpen.setText(""+RADSParms.RAM_DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		rampageIntGapExt.setText(""+RADSParms.RAM_DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		rampageTerGapOpen.setText(""+RADSParms.RAM_DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		rampageTerGapExt.setText(""+RADSParms.RAM_DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
	}
	
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
		submit.setActionCommand("startScan");
		submit.addActionListener(this);
		submit.setEnabled(false);
		
		reset = new JButton("Set defaults");
		reset.setToolTipText("Set default values");
		reset.setActionCommand("setDeaultValues");
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
		
		showReport = new JButton("Show scan log");
		showReport.setToolTipText("Show RADS scan log");
		showReport.setActionCommand("openLogWindow");
		showReport.addActionListener(this);
		showReport.setEnabled(false);
		
	}
	
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
				@SuppressWarnings({ "rawtypes", "unchecked" })
				JComboBox<WorkspaceElement> cb = (JComboBox<WorkspaceElement>)evt.getSource();
				ViewElement selected = (ViewElement)cb.getSelectedItem();
				if (selected == null) {
					return;
				}
				else {
					selectedViewName = selected.getTitle();
					//System.out.println(selected.getTitle());
				}
				selectedSeqView = ViewHandler.getInstance().getView(selected.getViewInfo());
				if (selectedSeqView.getSequences().length > 1)
					MessageUtil.showInformation("The selected view has multiple sequences. Only the first will be considered");
				selectArrView.setSelectedItem(null);
				loadArrTF.setText("");
				submit.setEnabled(true);
				//annotationSpawner.setSeqs(selectedSequenceView.getSeqs());
				
			}
		});
	}
	
	private void initSelectArrView() {
		List<WorkspaceElement> viewList = WorkspaceManager.getInstance().getDomainViews();
		WorkspaceElement[] arrViews = viewList.toArray(new ViewElement[viewList.size()]);
		
		if (arrViews.length == 0) {
			selectSeqView = new JComboBox<WorkspaceElement>(arrViews);
			selectSeqView.setSelectedItem(null);
			selectSeqView.setEnabled(false);
			return;
		}
		
		selectArrView = new JComboBox<WorkspaceElement>(arrViews);
		selectArrView.setSelectedItem(null);
		selectArrView.setRenderer(new WizardListCellRenderer());
		selectArrView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				loadArrTF.setText("");
				@SuppressWarnings({ "rawtypes", "unchecked" })
				JComboBox<WorkspaceElement> cb = (JComboBox<WorkspaceElement>)evt.getSource();
				ViewElement selected = (ViewElement)cb.getSelectedItem();
				if (selected == null) {
					return;
				}
				else {
					selectedViewName = selected.getTitle();
					//System.out.println(selected.getTitle());
				}
				selectedArrView = ViewHandler.getInstance().getView(selected.getViewInfo());
				if (selectedArrView.getDaSet().length > 1)
					MessageUtil.showInformation("The selected view has multiple arrangements. Only the first will be considered");
				selectSeqView.setSelectedItem(null);
				loadSeqTF.setText("");
				//annotationSpawner.setSeqs(selectedSequenceView.getSeqs());
				submit.setEnabled(true);
				
				
			}
		});
	}
	
	
	
}
