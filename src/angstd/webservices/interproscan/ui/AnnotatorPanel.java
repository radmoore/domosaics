package angstd.webservices.interproscan.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.configuration.Configuration;
import angstd.model.sequence.SequenceI;
import angstd.model.sequence.io.FastaReader;
import angstd.model.workspace.ViewElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.ViewHandler;
import angstd.ui.WorkspaceManager;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.ViewType;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.wizards.WizardListCellRenderer;
import angstd.ui.wizards.WizardManager;
import angstd.webservices.interproscan.AnnotationThreadSpawner;
import angstd.webservices.interproscan.AnnotatorProcessWriter;
import angstd.webservices.interproscan.Method;

/**
 * Class AnnotatorPanel displays the annotation process and allows
 * setting all needed parameters, which are: <br>
 * 1. A sequence file or a sequence view for query sequences <br>
 * 2. a valid email address <br>
 * 3. a signature method for annotation, e.g. hmmpfam. <br>
 * 
 * @author Andreas Held, Andrew Moore
 *
 */
public class AnnotatorPanel extends JPanel implements AnnotatorProcessWriter{
	private static final long serialVersionUID = 1L;
	
	/** default email address */
	private static String DEFAULT_EMAIL = "enter your email here";
	
	/** The Annotator frame, where the panel is embedded in */
	private AnnotatorFrame parent;
	
	/** Spawner for annotation threads */
	private AnnotationThreadSpawner annotationSpawner;

	/** TextFields for sequence file path, email and e-value */
	private JTextField seqPath, email, evalue;
	
	/** view selection box */
	private JComboBox selectView;
	
	/** ButtonGroup for methods **/
	private ButtonGroup methodsGroup;
	
	/** Method radio buttons */
	private JRadioButton[] methods;

	/** Buttons for load sequence file, submit job, apply results and cancel */
	private JButton loadSeqs, submit, apply, cancel;
	
	/** console to update user about the annotation status */
	private JTextArea console;
	
	/** progressBar for the total process update */
	private JProgressBar progressBar;
	
	/** instance of the panel */
	private AnnotatorPanel instance;
	
	/** instance of configurator (for email) **/
	private Configuration config;
	
	
	/**
	 * Constructor for a new Annotator panel
	 * 
	 * @param parent 
	 * 		the frame where the panel is embedded in
	 */
	public AnnotatorPanel(AnnotatorFrame parent) {
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
		initMethodCheckBoxes();
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
	public void print(String text) {
		console.append(text);
		console.setCaretPosition(console.getText().length());
	}
	
	/**
	 * Updates the progress bar.
	 * 
	 * @param val
	 * 		the new value of the progress bar in percent
	 */
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
		// the name is equal to the file + seqs
		File dummy = new File(seqPath.getText());
		String defaultName = dummy.getName().split("\\.")[0];
		
		// create sequence view if it comes from a file
		if (seqPath.getText().length() > 0) {
			SequenceView view = ViewHandler.getInstance().createView(ViewType.SEQUENCE, defaultName+"_seqs");
			view.setSeqs(annotationSpawner.getSeqs());
			ViewHandler.getInstance().addView(view, null);
		}
		
		// add annotated domain view
		if (annotationSpawner.getResult().get().length != 0) {
			// force the user to enter a valid name for the view
			String viewName = null;
			while (viewName == null) {
				viewName = WizardManager.getInstance().selectNameWizard(defaultName, "view");
				if (viewName == null) 
					MessageUtil.showWarning("A valid view name is needed to complete this action");
			}
			
			DomainViewI domResultView = ViewHandler.getInstance().createView(ViewType.DOMAINS, viewName);
			domResultView.setDaSet(annotationSpawner.getResult().get());
			domResultView.loadSequencesIntoDas(annotationSpawner.getSeqs(), annotationSpawner.getResult().get());
			ViewHandler.getInstance().addView(domResultView, null);
		}
		
		dispose();
		parent.dispose();
	}
	
	/**
	 * cancels the annotation spawner and disposes the GUI.
	 * TODO: ensure that the swing worker disposes of the
	 * AnnotationThreas that are running (ADM)
	 */
	public void cancel() {
		dispose();
		parent.dispose();
	}
	
	/** 
	 * stops all active annotation threads
	 */
	public void dispose() {
		// aborts the thread if it is currently running
		if (annotationSpawner.isRunning())
			annotationSpawner.cancel();
	}
	
	/**
	 * Submits a new annotation job for all sequences
	 */
	public void submitJob() {
		console.setText("");
	//	System.out.println("Want to submit!");
		if(annotationSpawner.isRunning()) {
			print("Annotator is currently running please wait! \n");
			return;
		}
		
		if (annotationSpawner.getSeqs() == null) {
			print("Please load sequences first! \n");
			return;
		}
		
		if (!isValidEmail(email.getText())) {
			print("Please enter a correct email address! \n");
			return;
		}
		
		if (!isNumber(evalue.getText())) {
			print("Please enter an E value! \n");
			return;
		}
		
		// gather selected methods
		StringBuffer methodStr = new StringBuffer();
		for (Method m : Method.values()) 
			if (methods[m.ordinal()].isSelected())
				methodStr.append(""+m.getTitle()+" ");
		if (methodStr.length() == 0) {
			print("Please select a method! \n");
			return;
		}
			
		//System.out.println("... and am about to do that!");
		annotationSpawner.setEmail(email.getText());
		annotationSpawner.setMethod(methodStr.toString());
		
		//System.out.println("shoot!");
		//annotationSpawner.startMultiThreadSpawn();
		annotationSpawner.startSingleThreadSpawn();
	}
		

	/* ************************************************************* *
	 * 						CORECTNESS CHECKING						 *
	 * ************************************************************* */

	/**
	 * Checks whether or not the email address is valid.
	 * 
	 * @param
	 * 		the address entered in the email text field
	 * @return
	 * 		whether or not the address is valid
	 */
	private boolean isValidEmail (String adress) {
		if (adress.contains(" "))			// white spaces
			return false;

		String[] nameDomain = adress.split("@");
		if (nameDomain.length != 2 		||	// no or more than one @
			nameDomain[0].length() == 0	||	// empty name
			!nameDomain[1].contains(".")||	// no .
			nameDomain[1].substring(0, nameDomain[1].lastIndexOf(".")).length() < 1 ||
			nameDomain[1].substring(nameDomain[1].lastIndexOf(".")).length()-1 < 2
		   )								// x.xx
			return false;
		
		// x@x.xx
		return true;
	}
	
	private boolean isNumber(String word) {
		try {
			Double.parseDouble(word);
			return true;
		} catch (Exception e){
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
		add(loadSeqs,     						"gap 10");
		add(seqPath,							"gap 10, span 2, growX, wrap");
		add(new JLabel("Or Select Loaded View:"),"gap 10");
		add(selectView,     					"gap 10, span 2, growX, wrap");
		
		// parameter
		add(new JXTitledSeparator("Parameters"),"growx, span, wrap, gaptop 10");
		add(new JLabel("Your e-mail:"),  		"gap 10");
		add(email,     							"span, growx, wrap");
		add(new JLabel("Evalue Threshold:"),	"gap 10");
		add(evalue,     						"growx, wrap");
	 
		// methods 
		add(new JXTitledSeparator("Methods"),	"growx, span, wrap, gaptop 10");
		for (Method m : Method.values()) {
			if ((m.ordinal()+1) % 4 != 0)
				add(methods[m.ordinal()],		"gap 10");
			else
				add(methods[m.ordinal()],		"gap 10, wrap");
		}
		add(new JLabel(""),						"wrap");					
		 
		// buttons
		add(new JXTitledSeparator("Submit job to Interpro / EBI"),  "growx, span, wrap, gaptop 10");
		add(new JLabel(""));
		add(submit,  							"gaptop 10, span 2, center, wrap");

		// console
		add(new JXTitledSeparator("Console"),	"growx, span, wrap, gaptop 10");
		add(new JScrollPane(console),			"gap 10, span, wrap");	
		 
		// progressbar
		add(new JXTitledSeparator("Progress"), "growx, span, wrap, gaptop 10");
		add(progressBar,						"gap 10, span, growx, wrap");
		
		// apply
		add(new JXTitledSeparator("Apply Results"), "growx, span, wrap, gaptop 10");
		add(apply,								"gap 10");
		add(cancel,								"gap 10, wrap");	
	}
	
	/* ************************************************************* *
	 * 					COMPONENTS INITIALIZATION					 *
	 * ************************************************************* */

	private void initFinalButtons() {
		submit = new JButton("Submit Job");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				submitJob();
			}
		});
		
		apply = new JButton ("Apply");
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				apply();
			}
		});
		
		cancel = new JButton ("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancel();
			}
		});
	}
	
	private void initSelectViewBox() {
		// get loaded sequence views
		List<WorkspaceElement> viewList = WorkspaceManager.getInstance().getSequenceViews();
		WorkspaceElement[] seqViews = viewList.toArray(new ViewElement[viewList.size()]);
		
		selectView = new JComboBox(seqViews);
		selectView.setSelectedItem(null);
		selectView.setRenderer(new WizardListCellRenderer());
		selectView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				if (seqPath.getText().length() > 0) {
					print("Already loaded a sequence file");
					selectView.setSelectedItem(null);
					return;
				}
				JComboBox cb = (JComboBox)evt.getSource();
				ViewElement selected = (ViewElement)cb.getSelectedItem();
				SequenceView view = ViewHandler.getInstance().getView(selected.getViewInfo());
				annotationSpawner.setSeqs(view.getSeqs());
			}
		});
	}
	
	private void initLoadSeqBtn() {
		loadSeqs = new JButton("Load Sequences");
		loadSeqs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				File file = FileDialogs.showOpenDialog(instance);
				if(file != null) 
					seqPath.setText(file.getAbsolutePath());
				if(file != null) { 
					// load seqs
					annotationSpawner.setSeqs((SequenceI[]) new FastaReader().getDataFromFile(file));
				}
			}
		});
	}
	
	private void initMethodCheckBoxes() {
		methodsGroup = new ButtonGroup();
		methods = new JRadioButton[Method.values().length];
		for (Method m : Method.values()) {
			methods[m.ordinal()] = new JRadioButton(m.getTitle(), m.getInitialState());
			methodsGroup.add(methods[m.ordinal()]);
		}
	}
	
	private void initEmailText() {
		String email_text = (config.getEmailAddr().isEmpty()) ? DEFAULT_EMAIL : config.getEmailAddr() ;
		email = new JTextField(email_text);
		if (isValidEmail(email_text)) {
			// colorize green, if email is valid
			email.setForeground(new Color(60, 120, 30));
		} else				
			email.setForeground(new Color(210, 60, 60));
		
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
		}); 
	}
	
	private void initEvalText() {
		evalue = new JTextField("2.04E-4");
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
		console = new JTextArea ();
		console.setFont(new Font ("Courier", 0, 14));	// style plain, size 14
		console.setColumns(50);
		console.setLineWrap(true);
		console.setRows(8);
		console.setWrapStyleWord(false);				// wrap on chars
		console.setEditable(false);
	}



}
