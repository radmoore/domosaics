package domosaics.webservices.interproscan.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.wizards.WizardListCellRenderer;
import domosaics.ui.wizards.WizardManager;
import domosaics.ui.wizards.pages.SelectNamePage;
import domosaics.util.CheckConnectivity;
import domosaics.util.StringUtils;
import domosaics.util.UiUtil;
import domosaics.webservices.interproscan.AnnotationThreadSpawner;
import domosaics.webservices.interproscan.AnnotatorProcessWriter;
import domosaics.webservices.interproscan.Method;




/**
 * Class AnnotatorPanel displays the annotation process and allows
 * setting all needed parameters, which are: <br>
 * 1. A sequence file or a sequence view for query sequences <br>
 * 2. a valid email address <br>
 * 3. a signature method for annotation, e.g. hmmpfam. <br>
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class AnnotatorPanel extends JPanel implements AnnotatorProcessWriter{
	private static final long serialVersionUID = 1L;
	
	/** default email address */
	private static String DEFAULT_EMAIL = "enter your email here";
	
	/** The AnnotatorFrame, where the panel is embedded in */
	private JFrame parent;
	
	/** Spawner for annotation threads */
	private AnnotationThreadSpawner annotationSpawner;

	/** TextFields for sequence file path, email and e-value */
	private JTextField seqPath, email, evalue;
	
	/** view selection box */
	private JComboBox<String> selectView, selectMethod;
	
	/** Buttons for load sequence file, submit job, apply results and cancel */
	private JButton loadSeqs, submit, apply, cancel; //close;
	
	/** console to update user about the annotation status */
	private JTextArea console;
	
	/** progressBar for the total process update */
	private JProgressBar progressBar;
	
	/** instance of the panel */
	private AnnotatorPanel instance;
	
	/** instance of configurator (for email) **/
	private Configuration config;

	/** Selected view **/
	private String defaultName;
	
	private SequenceView selectedSequenceView;
	
	/**
	 * Constructor for a new Annotator panel
	 * 
	 * @param parent 
	 * 		the frame where the panel is embedded in
	 */
	public AnnotatorPanel(JFrame parent) {
		super(new MigLayout("", "[left]"));
		
		this.parent = parent;
		annotationSpawner = new AnnotationThreadSpawner(this);
		instance = this;
		initComponents();
		initPanel();	
	}
	
	/**
	 * initialize all panel components with default values.
	 */
	private void initComponents() {
		config = Configuration.getInstance();
		seqPath = new JTextField("");
		progressBar = new JProgressBar(0, 105);
		progressBar.setValue(0);
		initSelectViewBox();
		initLoadSeqBtn();
		initMethodSelection();
		initEmailText();
		initEvalText();
		initFinalButtons();
		initConsole();
	}

	/**
	 * Prints messages to the console.
	 * 
	 * @param text
	 * 		the message to be printed
	 */
	@Override
	public void print(String text) {
		if ( annotationSpawner.isRunning() ) {
			console.append(text);
			console.setCaretPosition(console.getText().length());
		}
	}
	
	/**
	 * Updates the progress bar.
	 * 
	 * @param val
	 * 		the new value of the progress bar in percent
	 */
	@Override
	public void updateProgress(int val) {
		progressBar.setValue(val);
	}
	
	/* **************************************************************** *
	 * 					METHODS TRIGGERED BY BUTTONS					*
	 * **************************************************************** */
	
	/**
	 * Creates a new domain view out of the annotated domain arrangements
	 * produced by the annotation spawner. <br>
	 * If the sequences were loaded from a file, a new sequence view is created
	 * as well. <br>
	 * After the views were created, the annotation frame disposes.
	 * 
	 */
	public void apply() {
		
		DomainArrangement[] domArrs = annotationSpawner.getResult().get();
		
		if ( (domArrs == null) || (domArrs.length == 0) ) {
			progressBar.setValue(100);
			parent.setAlwaysOnTop(false);
			MessageUtil.showInformation(parent, "No siginificant hits found.");
			parent.setAlwaysOnTop(true);
			return;
		}
		
		// the name is equal to the file + seqs
		if(defaultName == null) {
			File dummy = new File(seqPath.getText());
			defaultName = dummy.getName().split("\\.")[0];
		}
		else
			defaultName = defaultName+"-interproscan-"+selectMethod.getSelectedItem().toString()+"-results";
		
		
		String viewName = null;
		String projectName = null;
		ProjectElement project = null;
		
		if (selectedSequenceView != null) {
			ViewElement elem = WorkspaceManager.getInstance().getViewElement(selectedSequenceView.getViewInfo());
			project = elem.getProject();
		}

		while (viewName == null) {

			parent.setAlwaysOnTop(false);
			Map m = WizardManager.getInstance().selectNameWizard(defaultName, "annotation", project, true);
			parent.setAlwaysOnTop(true);
			if(m!=null) {
				viewName = (String) m.get(SelectNamePage.VIEWNAME_KEY);
				projectName = (String) m.get(SelectNamePage.PROJECTNAME_KEY);
			} else {
				return;
			}
		}
		project = WorkspaceManager.getInstance().getProject(projectName);
				
		
		DomainViewI domResultView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
		domResultView.setDaSet(domArrs);
		domResultView.loadSequencesIntoDas(annotationSpawner.getSeqs(), domArrs, false);

		// create sequence view if it comes from a file
		if (seqPath.getText().length() > 0) {
			SequenceView view = ViewHandler.getInstance().createView(ViewType.SEQUENCE, viewName+"_seqs");
			view.setSeqs(domResultView.getSequences());
			ViewHandler.getInstance().addView(view, project);
		}

		ViewHandler.getInstance().addView(domResultView, project);
		
		cancel();
		parent.dispose();
	}
	
	/**
	 * cancels the annotation spawner.
	 */
	public void cancel() {
		if ( annotationSpawner.isRunning() ) {
			boolean cancel = MessageUtil.showDialog(parent,"This will cancel your job. Are you sure?");
			if (cancel) {
				annotationSpawner.cancel();
				submit.setEnabled(true);
				loadSeqs.setEnabled(true);
				seqPath.setEnabled(true);
				selectView.setEnabled(true);
				selectMethod.setEnabled(true);
				email.setEnabled(true);
				apply.setEnabled(false);
			}
			else
				return;
		}
		else {
			parent.dispose();
		}
	}
	
//	/**
//	 * cancels the annotation spawner and disposes the GUI.
//	 */
//	public void close() {
//		dispose();
//		parent.dispose();
//	}
//	
//	/** 
//	 * stops all active annotation threads
//	 */
//	public void dispose() {
//		// aborts the thread if it is currently running
//		if (annotationSpawner.isRunning())
//			annotationSpawner.cancel();
//	}
	
	/**
	 * Submits a new annotation job for all sequences
	 */
	public void submitJob() {
			
		console.setText("");
		
		if (annotationSpawner.getSeqs() == null) {
			MessageUtil.showWarning(parent,"Please load sequences first!");
			//print("Please load sequences first! \n");
			return;
		}
		
		if (!UiUtil.isValidEmail(email.getText())) {
			MessageUtil.showWarning(parent,"Please enter a valid email!");
			//print("Please enter a correct email address! \n");
			return;
		} 
		else {
			if(Configuration.getInstance().getEmailAddr().equals("")) {
				Configuration.getInstance().setEmailAddr(email.getText());
				if(Configuration.getInstance().getFrame()!=null)
					Configuration.getInstance().getFrame().getConfigPanel().getEmailTF().setText(email.getText());
			} 
			else {
				if ( !email.getText().equals(Configuration.getInstance().getEmailAddr()) ) {
					if ( MessageUtil.showDialog(parent,"A distinct email is saved in settings. Overwrite?") ) {
						Configuration.getInstance().setEmailAddr(email.getText());
						if(Configuration.getInstance().getFrame()!=null)
							Configuration.getInstance().getFrame().getConfigPanel().getEmailTF().setText(email.getText());
					}
			}
		}
		
		if (!StringUtils.isNumber(evalue.getText())) {
			MessageUtil.showWarning(parent,"Please enter a numeric E-value!");

			return;
		}
		
		annotationSpawner.setEmail(email.getText());
		annotationSpawner.setMethod(selectMethod.getSelectedItem().toString());
		
		// check inet connectivity
		if (!CheckConnectivity.checkInternetConnectivity()) {
			MessageUtil.showWarning(parent,"Please check your intenet connection (connection failed)");
			return;
		}
		if (!CheckConnectivity.addressAvailable("http://www.ebi.ac.uk/Tools/services/soap/iprscan?wsdl")) {
			MessageUtil.showWarning(parent,"Cannot connect to EBI webservices. Please try again later.");
			return;
		}
		
		annotationSpawner.startMultipleThreadSpawn();
		
		submit.setEnabled(false);
		loadSeqs.setEnabled(false);
		seqPath.setEnabled(false);
		selectView.setEnabled(false);
		selectMethod.setEnabled(false);
		email.setEnabled(false);
		apply.setEnabled(true);
	}
	

	/* ************************************************************* *
	 * 						CORECTNESS CHECKING						 *
	 * ************************************************************* */

		private boolean isNumber(String word) {
		try {
			Double.parseDouble(word);
			return true;
		} 
		catch (Exception e){
			return false;
		}
	}
	
	/* ************************************************************* *
	 * 						LAYOUTING COMPONENTS					 *
	 * ************************************************************* */
	
	/**
	 * Layouts the panels components using MigLayout.
	 */
	private void initPanel() {
		// sequences
		add(new JXTitledSeparator("Sequences"), "growx, span, wrap, gaptop 10");
		
		add(loadSeqs, "w 165!, gap 5");
		add(seqPath, "h 25!, span, growX, gapright 5, wrap");
		
		add(new JLabel("Or select view:"), "gap 5");
		add(selectView, "h 25!, span, growX, gapright 5, wrap");
		
		// parameter
		add(new JXTitledSeparator("Parameters"),"growX, span, wrap, gaptop 10");
		add(new JLabel("Your e-mail:"), "gap 5");
		add(email, "h 25!, span, growX, gapright 5, wrap");
	 
		add(new JXTitledSeparator("Submit job to Interpro / EBI"),  "growx, span, wrap, gaptop 10");
		
		add(new JLabel("Select method:"), "gap 5");
		add(selectMethod, "h 25!, span, wrap");

		//add(methodPane, "span");
		add(submit, "w 165!, wrap");

		// console
		add(new JXTitledSeparator("Console"),	"growx, span, wrap, gaptop 10");
		add(new JScrollPane(console),			"span, align center, wrap");	
		 
		// progressbar
		add(new JXTitledSeparator("Progress"), "growx, span, wrap, gaptop 10");
		add(progressBar,						"h 25!, gap 10, gapright 10, span, growX, wrap");
		
		// apply
		add(new JXTitledSeparator("Apply Results"), "growx, span, wrap, gaptop 10");
		add(apply, "w 80!, gap 5, split 2");
		add(cancel, "gap 5, split 2");
//		add(close, "wrap");	
	}
	
	/* ************************************************************* *
	 * 					COMPONENTS INITIALIZATION					 *
	 * ************************************************************* */

	private void initFinalButtons() {
		submit = new JButton("Submit Job");
		submit.setEnabled(false);
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				submitJob();
			}
		});
		
		apply = new JButton ("Keep");
		apply.setEnabled(false);
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				apply();
			}
		});

		cancel = new JButton ("Dismiss");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cancel();
			}
		});
	}
	
	private void initSelectViewBox() {
		// get loaded sequence views
		List<WorkspaceElement> viewList = WorkspaceManager.getInstance().getSequenceViews();
		WorkspaceElement[] seqViews = viewList.toArray(new ViewElement[viewList.size()]);
		
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
			@Override
			public void actionPerformed(ActionEvent evt) {
				seqPath.setText("");
				JComboBox cb = (JComboBox)evt.getSource();
				ViewElement selected = (ViewElement)cb.getSelectedItem();
				if (selected == null) {
					return;
				}
				else {
					defaultName = selected.getTitle();
					//System.out.println(selected.getTitle());
				}
				selectedSequenceView = ViewHandler.getInstance().getView(selected.getViewInfo());
				annotationSpawner.setSeqs(selectedSequenceView.getSeqs());
				submit.setEnabled(true);
			}
		});
	}
	
	private void initLoadSeqBtn() {
		loadSeqs = new JButton("Load Sequences");
		loadSeqs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (!(selectView.getSelectedItem() == null)) {
					//MessageUtil.showWarning("You have already loaded sequences, deselecting.");
					defaultName=null;
					selectView.setSelectedItem(null);
					submit.setEnabled(false);
				}
				File file = FileDialogs.showOpenDialog(parent);
				if(file != null && file.canRead()) {
					parent.setAlwaysOnTop(false);
					Object sequences = new FastaReader().getDataFromFile(file);
					parent.setAlwaysOnTop(true);
					if (sequences != null) {
						annotationSpawner.setSeqs((SequenceI[]) sequences);
						seqPath.setText(file.getAbsolutePath());
						submit.setEnabled(true);
					}
				}
			}
		});
	}
	
	private void initMethodSelection() {
		// preselect hmmpfam
		selectMethod = new JComboBox(Method.getMethodNames());
		selectMethod.setSelectedIndex(4);
	}
	
	
	private void initEmailText() {
		String email_text = (config.getEmailAddr().isEmpty()) ? DEFAULT_EMAIL : config.getEmailAddr() ;
		email = UiUtil.createEmailField(email_text);
		/*if (UiUtil.isValidEmail(email_text)) {
			// colorize green, if email is valid
			email.setForeground(new Color(60, 120, 30));
		} else	{			
			email.setForeground(new Color(210, 60, 60));
		}
		email.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}

			public void insertUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}

			public void removeUpdate(DocumentEvent arg0) {
				email.setForeground(getCorrectColor(email.getText()));
			}
			
			private Color getCorrectColor(String adress) {
				if (isValidEmail(adress))
					return new Color(60, 120, 30);
				return new Color(210, 60, 60);
			}
		})*/; 
	}
	
	private void initEvalText() {
		evalue = new JTextField("2.04E-4", 25);
//		evalue.setForeground(new Color(60, 120, 30));
		evalue.setEditable(false);
//		evalue.getDocument().addDocumentListener(new DocumentListener() {
//			// colorize green, if evalue holds a valid number
//			public void changedUpdate(DocumentEvent arg0) {
//				evalue.setForeground(getCorrectColor(evalue.getText()));
//			}
//
//			public void insertUpdate(DocumentEvent arg0) {
//				evalue.setForeground(getCorrectColor(evalue.getText()));
//			}
//
//			public void removeUpdate(DocumentEvent arg0) {
//				evalue.setForeground(getCorrectColor(evalue.getText()));
//			}
//			
//			private Color getCorrectColor(String eval) {
//				if (isNumber(eval))
//					return new Color(60, 120, 30);
//				return new Color(210, 60, 60);
//			}
//		}); 
	}
	
	private void initConsole() {
		console = new JTextArea();
		console.setFont(new Font ("Courier", 0, 12));
		console.setColumns(70);
		console.setLineWrap(true);
		console.setRows(8);
		console.setWrapStyleWord(false);				// wrap on chars
		console.setEditable(false);
	}


}
