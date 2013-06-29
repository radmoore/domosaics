package domosaics.ui.tools.configuration;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import domosaics.localservices.hmmer3.Hmmer3Engine;
import domosaics.localservices.hmmer3.programs.HmmPress;
import domosaics.model.configuration.Configuration;
import domosaics.model.configuration.ConfigurationWriter;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.util.UiUtil;

/**
 * Panel displayed in the configuration frame.
 * Allows management of program-wide configuration.
 * All changes made here are adjusted accordingly
 * within the backend data type {@link Configuration}
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * @author Andreas Held
 *
 */
public class ConfigurationPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/** embedding parent frame */
	protected ConfigurationFrame parentFrame;
	protected JPanel configPanel;
	
	/** textfields for lookup addresses */
	protected JTextField googleField, ncbiField, pfamField, uniprotField, 
	emailField, hmmerScanTF, hmmerPressTF, hmmer3dbTF, workspace, documentationTF;
	
	protected JButton loadHmmScanBin, loadHmmPressBin, loadHmmDB, loadWorkspace,
	loadDocumentation;
	
	/** buttons for apply and cancel */
	protected JButton apply, cancel, restore;
	
	/** checkbox indicating whether or not advises should be shown */
	protected JCheckBox showAdvices, saveOnExit, overwriteProject, helpImprove;
	
	protected Hmmer3Engine hmmer3Engine;
	private HashMap<String, File> hmmer3bins;
	
	public static Color highlightColor = new Color(244,215,215);
	
	
	/**
	 * Constructor for a new ConfigurationPanel
	 * 
	 * @param parent
	 * 		the embedding frame
	 */
	public ConfigurationPanel(ConfigurationFrame parent) {
		super(new MigLayout());
		
		this.parentFrame = parent;
		initComponents();
		layoutComponents();
		this.configPanel = this;
		hmmer3Engine = Hmmer3Engine.getInstance();
		hmmer3bins = new HashMap<String, File>();
		
	}
	
	public void setHelpImprove(boolean improve) {
		helpImprove.setSelected(improve);
	}
	
	/**
	 * Stores the changed values in the backend data structure
	 */
	private void apply() {
		Configuration config = Configuration.getInstance();
		config.setGoogleUrl(googleField.getText());
		//config.setNcbiUrl(ncbiField.getText());
		config.setPfamUrl(pfamField.getText());
		config.setUniprotUrl(uniprotField.getText());
		config.setEmailAddr(emailField.getText());
		config.setHmmScanBin(hmmerScanTF.getText());
		config.setHmmPressBin(hmmerPressTF.getText());
		config.setHmmerDB(hmmer3dbTF.getText());
		//config.setShowAdvices(showAdvices.isSelected());
		config.setWorkspaceDir(workspace.getText());
		config.setSaveOnExit(saveOnExit.isSelected());
		config.setOverwriteProjects(overwriteProject.isSelected());
		config.setHelpImprove(helpImprove.isSelected());
		config.setDocuPath(documentationTF.getText());
		ConfigurationWriter.write();
		
		//parentFrame.tryStop();
		dispose();
	}
	
	/**
	 * Restores the default settings of the configuration file.
	 */
	public void restore() {
		Configuration config = Configuration.getInstance();
		config.restoreDefaults();
		hmmerScanTF.setText(config.getHmmScanBin());
		hmmerPressTF.setText(config.getHmmPressBin());
		hmmer3dbTF.setText(config.getHmmerDB());
		googleField.setText(config.getGoogleUrl());
		//ncbiField.setText(config.getNcbiUrl());
		pfamField.setText(config.getPfamUrl());
		uniprotField.setText(config.getUniprotUrl());
		emailField.setText(config.getEmailAddr());
		//showAdvices.setSelected(config.isShowAdvices());
		workspace.setText(config.getWorkspaceDir());
		saveOnExit.setSelected(config.saveOnExit());
		overwriteProject.setSelected(config.getOverwriteProjects());
		helpImprove.setSelected(config.getHelpImprove());
	}
	
	public JTextField getHmmScanTF() {
		return hmmerScanTF;
	}
	
	public JTextField getHmmDBTF() {
		return hmmer3dbTF;
	}
	
	public JTextField getHmmPressTF() {
		return hmmerPressTF;
	}
	
	public JTextField getEmailTF() {
		return emailField;
	}
	
	/**
	 * disposes the frame 
	 */
	private void dispose() {
		Configuration.getInstance().closeFrame();
	}
	
	/**
	 * initializes the components
	 */
	private void initComponents() {
		Configuration config = Configuration.getInstance();
		hmmerScanTF = new JTextField(config.getHmmScanBin());
		hmmerScanTF.setEditable(false);
		hmmerScanTF.setBackground(Color.WHITE);
		/*hmmerScanTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
			}
		});*/
		hmmerPressTF = new JTextField(config.getHmmPressBin());
		hmmerPressTF.setEditable(false);
		hmmerPressTF.setBackground(Color.WHITE);
		/*hmmerPressTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
			}
		});*/
		hmmer3dbTF = new JTextField(config.getHmmerDB());
		hmmer3dbTF.setEditable(false);
		hmmer3dbTF.setBackground(Color.WHITE);
		/*hmmer3dbTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
			}
		});*/
		workspace = new JTextField(config.getWorkspaceDir());
		workspace.setEditable(false);
		workspace.setBackground(Color.WHITE);
		/*workspace.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
			}
		});*/
		googleField = new JTextField(config.getGoogleUrl(), 25);
		//ncbiField = new JTextField(config.getNcbiUrl(), 25);
		pfamField = new JTextField(config.getPfamUrl(), 25);
		uniprotField = new JTextField(config.getUniprotUrl(), 25);
		//emailField = new JTextField(config.getEmailAddr(), 25);

		emailField = UiUtil.createEmailField(config.getEmailAddr());
		documentationTF = new JTextField(config.getDocuPath(false), 25);
		
		loadHmmScanBin = new JButton("hmmscan");
		loadHmmScanBin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File scanBin = FileDialogs.showOpenDialog(parentFrame);
				if (scanBin == null || !scanBin.canExecute())
					return;
				hmmerScanTF.setText(scanBin.getAbsolutePath());
			}
		});
		
		loadHmmPressBin = new JButton("hmmpress");
		loadHmmPressBin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File pressBin = FileDialogs.showOpenDialog(parentFrame);
				if (pressBin == null || !pressBin.canExecute())
					return;
				hmmerPressTF.setText(pressBin.getAbsolutePath());
			}
		});
		
		
		loadHmmDB = new JButton("load profiles");
		loadHmmDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = FileDialogs.showOpenDialog(parentFrame);
				if (file == null || !file.canRead())
					return;
				hmmer3dbTF.setText(file.getAbsolutePath());
			}
		});

		loadWorkspace = new JButton("Browse");
		loadWorkspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File workspaceFile = FileDialogs.openChooseDirectoryDialog(parentFrame);
				if (workspaceFile == null)
					return;
				workspace.setText(workspaceFile.getAbsolutePath()+File.separator+"domosaics-workspace");
			}
		});
		
		loadDocumentation = new JButton("Help");
		loadDocumentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("HTML file", "html", "htm");
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(parentFrame);
				File documentationIndex = null;
				if (returnVal == JFileChooser.APPROVE_OPTION)
					documentationIndex = chooser.getSelectedFile();
				if (documentationIndex == null)
					return;
				documentationTF.setText(documentationIndex.getAbsolutePath());
			}
		});
		
		
//		showAdvices = new JCheckBox("Show Advices", config.isShowAdvices());
		saveOnExit = new JCheckBox(" Save Workspace on Exit", config.saveOnExit());
		overwriteProject = new JCheckBox(" Overwrite existing projects", config.getOverwriteProjects());
		helpImprove = new JCheckBox(" Help improve DoMosaics", Configuration.getReportExceptionsMode(false));
		
		
		apply = new JButton ("Apply");
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				// user can only save a valid config
				
				if (!emailField.getText().equals(""))
					if (!UiUtil.isValidEmail(emailField.getText())) {
						MessageUtil.showWarning(parentFrame, "Please enter a valid email!");
						return;
					}
				
				if (!hmmerScanTF.getText().equals("")) {
					if (!checkHmmBin(new File(hmmerScanTF.getText()))) {
						hmmerScanTF.setBackground(highlightColor);
						return;	
					}
				}
				
				if (!hmmerPressTF.getText().equals("")) {
					if (!checkHmmBin(new File(hmmerPressTF.getText()))){
						hmmerPressTF.setBackground(highlightColor);
						return;	
					}
				}

				if (!hmmer3dbTF.getText().equals("")) {
					if (!checkHmmDB(new File(hmmer3dbTF.getText()))) {
						hmmer3dbTF.setBackground(highlightColor);
						return;
					}
				
				}
				
				if (!hmmer3dbTF.getText().equals("")) {
					if (!checkHmmDBpressed(new File(hmmer3dbTF.getText()))) {
						hmmerPressTF.setBackground(highlightColor);
						return;
					}
				
				}
				new File(workspace.getText()).mkdir();
				apply();
			}
		});
		
		cancel = new JButton ("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}
		});
		
		restore = new JButton ("Reset");
		restore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				restore();
			}
		});
	}
	
	/**
	 * layouts the components
	 */
	private void layoutComponents() {
		
		add(new JXTitledSeparator("General Settings"),"growx, span, wrap, gaptop 10");
		add(new JLabel("Email: "), "h 25!, gap 10");
		add(emailField, "h 25!, gap 10, span, growx, gapright 10, wrap");
		add(loadDocumentation, "h 25!, w 135!, gap 10");
		add(documentationTF, "h 25!, gap 10, span, growx, gapright 10, wrap");
		
		add(new JXTitledSeparator("Local HMMER3 setup"),"growx, span, wrap, gaptop 10");
		add(loadHmmScanBin, "h 25!, w 135!, gap 10");
		add(hmmerScanTF, "h 25!, gap 10, span, growX, gapright 10, wrap");
		add(loadHmmPressBin, "h 25!, w 135!, gap 10");
		add(hmmerPressTF, "h 25!, gap 10, span, growX, gapright 10, wrap");
		
		add(loadHmmDB, "h 25!, w 135!, gap 10");
		add(hmmer3dbTF, "h 25!, gap 10, span, growX, gapright 10, wrap");
				
		add(new JXTitledSeparator("URLs"),"growx, span, wrap, gaptop 10");
		add(new JLabel("Google Url: "), "h 25!, gap 10");
		add(googleField, "h 25!, gap 10, span, growx, gapright 10, wrap");
		/*add(new JLabel("NCBI Url: "), "h 25!, gap 10");
		add(ncbiField, "h 25!, gap 10, span, growx, gapright 10, wrap");*/
		/*add(new JLabel("Pfam Url: "), "h 25!, gap 10");
		add(pfamField, "h 25!, gap 10, span, growx, gapright 10, wrap");*/
		add(new JLabel("Uniprot Url: "), "h 25!, gap 10");
		add(uniprotField, "h 25!, gap 10, span, growx, gapright 10, wrap");
		
//		add(new JXTitledSeparator("Advice"),"growx, span, wrap, gaptop 10");
//		add(showAdvices, 	"h 25!, gap 10, wrap");
		add(new JXTitledSeparator("Workspace"),"growx, span, wrap, gaptop 10");
		add(loadWorkspace, "h 25!, w 135!, gap 10");
		add(workspace, "h 25!, gap 10, span, growX, gapright 10, wrap");
		add(saveOnExit, 	"h 25!, gap 10, span 2, wrap");
		add(overwriteProject, 	"h 25!, gap 10, span 2, wrap");
		
		add(new JXTitledSeparator("Help improve"),"growx, span, wrap, gaptop 10");
		add(helpImprove, 	"h 25!, gap 10, span 2, wrap");
		
		
		add(new JXTitledSeparator("Apply"),"growx, span, wrap, gaptop 10");
		add(apply, "h 25!, w 80!, split 2, gap 10");
		add(cancel, "h 25!, gap 15");
		add(restore, "h 25!, w 80!, gap 10, gapbottom 5, wrap");
		
	}
	/**
	 * Check whether the current HMMER3 binary
	 * is a supported HMMER3 program
	 * 
	 * @param hmmer binary
	 * @return
	 */
	private boolean checkHmmBin(File bin) {

		if ( Hmmer3Engine.isSupportedService(bin.getName()) ) {
			
			if (!(bin.exists())) {
				MessageUtil.showWarning(parentFrame, "Cannot find or read "+bin.getAbsoluteFile());
				return false;
			}
			
			if (!(bin.canExecute())) {
				MessageUtil.showWarning(parentFrame, bin.getAbsoluteFile()+" is not executable. Exiting.");
				return false;
			}
		}
		else {
			MessageUtil.showWarning(parentFrame, bin.getAbsoluteFile()+" is not supported");
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
	 * Check whether the current HMMER3 database 
	 * file exists and is valid
	 *
	 * @param file
	 * @return
	 */
	private boolean checkHmmDB(File file) {
		
		// check if file is still there
		if (!file.exists()) {
			MessageUtil.showWarning(parentFrame, "Warning: could not find specified HMMER db file "+file.getName());
			return false;
		}
		
		// check if contains valid hmmer3 profiles
		if (!HmmPress.isValidProfileFile(file)) {
			MessageUtil.showWarning(parentFrame, file.getName()+ " does not appear to be a valid hmmer3 profile");
			return false;
		}
		return true;
	}
		
	/**
	 * Check whether the current HMMER3 database dir
	 * contains the pressed files
	 *
	 * @param file
	 * @return
	 */
	private boolean checkHmmDBpressed(File file) {
		
		boolean pressAvail = false;
		// if not, check whether press bin in the selected textfield is valid
		if (!hmmerPressTF.getText().equals(""))
			pressAvail = checkHmmBin(new File(hmmerPressTF.getText()));
		
		// check if pressed files are available
		/*if (!HmmPress.hmmFilePressed(file)) {
			if (MessageUtil.showDialog(parentFrame, file.getName()+" is not pressed. Do you want AnGSTD to press it now?")) {
				if (!pressAvail) {
					MessageUtil.showInformation(parentFrame, "Please first provide hmmpress binary");
					return false; 
				}
				configPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				HmmPress hmmPress = new HmmPress(hmmer3Engine.getAvailableServicePath("hmmpress"), file, configPanel);
				//TODO  we should get some type of return here.
				hmmer3Engine.launch(hmmPress);
			}
		}*/
		return true;
	}
	
	/*public boolean isRunningPress() {
		return hmmer3Engine.isRunning();
	}*/

}
