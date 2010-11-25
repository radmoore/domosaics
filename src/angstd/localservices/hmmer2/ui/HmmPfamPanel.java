package angstd.localservices.hmmer2.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

//import angstd.localservices.hmmer2.Hmmer2Engine;
//import angstd.localservices.hmmer2.Hmmer2ProgramType;
import angstd.ui.util.FileDialogs;

/**
public class HmmPfamPanel extends HmmerServicePanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	protected JTextField hmmTF, fastaTF, evalueTF, bitThresTF;
	protected JButton loadHmmDB, loadFastaFile, run, cancel;
	protected JLabel evalLabel, bitThresLabel;
//	protected JComboBox selectView;
	protected JTextArea console;
	
	protected File hmmDBFile, fastaFile;
	
	

	 * Constructor for a new HmmPfamPanel
	 
	public HmmPfamPanel() {
		setLayout(new MigLayout("", "[left]"));
		
		// set border
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder title = BorderFactory.createTitledBorder(loweredetched, "HMMPFAM");
		setBorder(title);
		
		// init components and layout the panel
		initComponents();
		initPanel();
	}
	
	/**
	 * Helper method to create the components
	 
	private void initComponents() {
		// init text fields
		hmmTF = new JTextField(25);
		fastaTF = new JTextField(25);
		evalueTF = new JTextField(5);
		bitThresTF = new JTextField(5);
		evalLabel = new JLabel("Evalue");
		bitThresLabel = new JLabel("Bitthreshold");
		progressBar = new JProgressBar();
//		evalueTF.setEnabled(false);
//		bitThresTF.setEnabled(false);
		evalueTF.setText("10");
		bitThresTF.setText("");
		
		// load data base  
		// TODO add hmmDB file format checker
		loadHmmDB = new JButton("Load Database");
		loadHmmDB.setActionCommand("LoadDB");
		loadHmmDB.addActionListener(this);
		
		// load fasta file
		/*
		 * TODO fasta file format checker
		 * TODO count fasta entries
	
		loadFastaFile = new JButton("Load Sequences");
		loadFastaFile.setActionCommand("LoadFasta");
		loadFastaFile.addActionListener(this);
		
		// init console
		console = new JTextArea ();
		console.setFont(new Font ("Courier", 0, 12));	// style plain, size 14
		console.setColumns(55);
		console.setLineWrap(true);
		console.setRows(8);
		console.setWrapStyleWord(false);				// wrap on chars
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
	 * Helper method to layout the panel
	
	private void initPanel() {
		add(loadHmmDB,     						"gap 10");
		add(hmmTF, "gap 10, span2, growX, wrap");
		
		add(new JXTitledSeparator("Sequences"), "growx, span, wrap, gaptop 10");
		add(loadFastaFile,     						"gap 10");
		add(fastaTF,							"gap 10, span 2, growX, wrap");
//		add(new JLabel("Or Select Loaded View:"),"gap 10");
//		add(selectView,     					"gap 10, span 2, growX, wrap");
		
		add(loadFastaFile,     						"gap 10");
		add(fastaTF, "gap 10, span2, growX, wrap");
		add(evalLabel,     							"gap 10");
		add(evalueTF, "gap 10, span2, wrap");
		add(bitThresLabel,     						"gap 10");
		add(bitThresTF, "gap 10, span2, wrap");
		add(new JXTitledSeparator("Progress"), "growX, span, wrap, gaptop 10");
		add(progressBar, "gap 10, growX, span2, wrap");
		add(new JXTitledSeparator("Console"),	"growx, span, wrap, gaptop 10");
		add(new JScrollPane(console),			"gap 10, span, wrap");	
		add(run,     						"gap unrelated");
		add(cancel);
	}
	
	/**
	 * Actions triggered on specific ui components.
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("LoadDB")) 
			loadDBAction();
		else if(e.getActionCommand().equals("LoadFasta")) 
			loadFastaAction();
		else if(e.getActionCommand().equals("Run")) 
			runAction();
		else if(e.getActionCommand().equals("Cancel")) 
			cancelAction();
		else 
			System.out.println("Unknown component tried to trigger an action @ HmmPfamPanel");
	}
	
	/**
	 * Helper method triggered when the load DB button is pressed.
	
	private void loadDBAction() {
		File file = FileDialogs.showOpenDialog(this);
		if (file == null || !file.canRead())
			return;
			
		hmmDBFile = file;
		hmmTF.setText(hmmDBFile.getAbsolutePath());
	}
	
	/**
	 * Helper method triggered when the load Fasta button is pressed.
	
	private void loadFastaAction() {
		File file = FileDialogs.showOpenDialog(this);
		if(file == null || !file.canRead())
			return;
		
		fastaTF.setText(file.getAbsolutePath());
	}
	
	/**
	 * Helper method triggered when the run button is pressed.
	
	private void runAction() {
		Hmmer2Engine.getInstance().launch(
				Hmmer2ProgramType.HMMPFAM, 
				new File(fastaTF.getText()),
				prepareArgs());
		
		run.setText("Running");
		run.setEnabled(false);
		progressBar.setStringPainted(true);
	}
	
	/**
	 * Helper method triggered when the cancel button is pressed.
	
	private void cancelAction() {
		Hmmer2Engine.getInstance().stop();
		run.setText("  Run  ");
		progressBar.setValue(0);
		run.setEnabled(true);
		progressBar.setIndeterminate(false);
	}
	
	/**
	 * Prints to console
	 * 
	 * @param msg
	 * 		the message to be printed
	
	public void writeToConsole(String msg) {
		console.append(msg+"\n");
	}
	
	
	
	// TODO add setSequences to hmmerservice
//	private void initSelectViewBox() {
//		// get loaded sequence views
//		List<WorkspaceElement> viewList = WorkspaceManager.getInstance().getSequenceViews();
//		WorkspaceElement[] seqViews = viewList.toArray(new ViewElement[viewList.size()]);
//		
//		selectView = new JComboBox(seqViews);
//		selectView.setSelectedItem(null);
//		selectView.setRenderer(new WizardListCellRenderer());
//		selectView.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent evt) {
//				if (fastaTF.getText().length() > 0) {
//					writeToConsole("Already loaded a sequence file");
//					selectView.setSelectedItem(null);
//					return;
//				}
//				JComboBox cb = (JComboBox)evt.getSource();
//				ViewElement selected = (ViewElement)cb.getSelectedItem();
//				SequenceView view = ViewHandler.getInstance().getView(selected.getViewInfo());
//				view.getSeqs();
//			}
//		});
//	}
	
	// TODO validate
	private String[] prepareArgs() {
		String[] args = new String[4];
		args[0] = "-E";
		args[1] = (evalueTF.getText() == "") ? null : evalueTF.getText();
//		args[2] = "-T";
//		args[3] = (bitThresTF.getText() == "") ? null : bitThresTF.getText();
		args[2] = hmmTF.getText();
		args[3] = fastaTF.getText(); 
		return args;
	}
	

	
}
	 */