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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
 * Panel which is displayed in the configuration frame to change 
 * for instance lookup addresses. The changed data is stored
 * within the backend data type {@link Configuration}
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ConfigurationPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/** embedding parent frame */
	protected ConfigurationFrame parentFrame;
	protected JPanel configPanel;
	
	/** textfields for lookup addresses */
	protected JTextField googleField, ncbiField, pfamField, uniprotField, emailField, hmmerScanTF, hmmerPressTF, hmmer3dbTF;
	
	protected JButton loadHmmScanBin, loadHmmPressBin, loadHmmDB;
	
	/** buttons for apply and cancel */
	protected JButton apply, cancel, restore;
	
	/** checkbox indicating whether or not advises should be shown */
	protected JCheckBox showAdvices, saveOnExit, overwriteProject;
	
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
	
	/**
	 * Stores the changed values in the backend data structure
	 */
	private void apply() {
		Configuration config = Configuration.getInstance();
		config.setGoogleUrl(googleField.getText());
		config.setNcbiUrl(ncbiField.getText());
		config.setPfamUrl(pfamField.getText());
		config.setUniprotUrl(uniprotField.getText());
		config.setEmailAddr(emailField.getText());
		config.setHmmScanBin(hmmerScanTF.getText());
		config.setHmmPressBin(hmmerPressTF.getText());
		config.setHmmerDB(hmmer3dbTF.getText());
		//config.setShowAdvices(showAdvices.isSelected());
		config.setSaveOnExit(saveOnExit.isSelected());
		config.setOverwriteProjects(overwriteProject.isSelected());

		ConfigurationWriter.write(config.getConfigFile());
		
		dispose();
	}
	
	/**
	 * Restores the default settings of the configuration file.
	 */
	private void restore() {
		Configuration config = Configuration.getInstance();
		config.restoreDefaults();
		hmmerScanTF.setText(config.getHmmScanBin());
		hmmerPressTF.setText(config.getHmmPressBin());
		hmmer3dbTF.setText(config.getHmmerDB());
		googleField.setText(config.getGoogleUrl());
		ncbiField.setText(config.getNcbiUrl());
		pfamField.setText(config.getPfamUrl());
		uniprotField.setText(config.getUniprotUrl());
		emailField.setText(config.getEmailAddr());
		//showAdvices.setSelected(config.isShowAdvices());
		saveOnExit.setSelected(config.saveOnExit());
		overwriteProject.setSelected(config.getOverwriteProjects());
	}
	
	/**
	 * disposes the frame 
	 */
	private void dispose() {
		parentFrame.dispose();
	}
	
	/**
	 * initializes the components
	 */
	private void initComponents() {
		Configuration config = Configuration.getInstance();
		hmmerScanTF = new JTextField(config.getHmmScanBin());
		hmmerScanTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
				hmmerScanTF.setBackground(Color.WHITE);
			}
		});
		hmmerPressTF = new JTextField(config.getHmmPressBin());
		hmmerPressTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
				hmmerPressTF.setBackground(Color.WHITE);
			}
		});
		hmmer3dbTF = new JTextField(config.getHmmerDB());
		hmmer3dbTF.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {}
			public void focusGained(FocusEvent e) {
				hmmer3dbTF.setBackground(Color.WHITE);
			}
		});
		googleField = new JTextField(config.getGoogleUrl(), 50);
		ncbiField = new JTextField(config.getNcbiUrl(), 50);
		pfamField = new JTextField(config.getPfamUrl(), 50);
		uniprotField = new JTextField(config.getUniprotUrl(), 50);
		//emailField = new JTextField(config.getEmailAddr(), 50);
		emailField = UiUtil.createEmailField(config.getEmailAddr());
		
		loadHmmScanBin = new JButton("hmmscan bin");
		loadHmmScanBin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File scanBin = FileDialogs.showOpenDialog(parentFrame);
				if (scanBin == null || !scanBin.canExecute())
					return;
				if (checkHmmBin(scanBin))
					hmmerScanTF.setText(scanBin.getAbsolutePath());
					else return;
			}
		});
		
		loadHmmPressBin = new JButton("hmmpress bin");
		loadHmmPressBin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File pressBin = FileDialogs.showOpenDialog(parentFrame);
				if (pressBin == null || !pressBin.canExecute())
					return;
				if (checkHmmBin(pressBin))
					hmmerPressTF.setText(pressBin.getAbsolutePath());
					else return;
			}
		});
		
		
		loadHmmDB = new JButton("Load profiles");
		loadHmmDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = FileDialogs.showOpenDialog(parentFrame);
				if (file == null || !file.canRead())
					return;

				if (checkHmmDBDir(file, true))
					hmmer3dbTF.setText(file.getAbsolutePath());
					else return;
			}
		});
		
		
//		showAdvices = new JCheckBox("Show Advices", config.isShowAdvices());
		saveOnExit = new JCheckBox(" Save Workspace on Exit", config.saveOnExit());
		overwriteProject = new JCheckBox(" Overwrite existing projects", config.getOverwriteProjects());
		
		apply = new JButton ("Apply");
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				// user can only save a valid config
				
				if (!emailField.getText().equals(""))
					if (!UiUtil.isValidEmail(emailField.getText())) {
						MessageUtil.showWarning("Please enter a valid email (or non at all)");
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
					if (!checkHmmDBDir(new File(hmmer3dbTF.getText()), false)) {
						hmmer3dbTF.setBackground(highlightColor);
						return;
					}
				
				}	
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
		
		add(new JXTitledSeparator("Local HMMER3 setup"),"growx, span, wrap, gaptop 10");
		add(loadHmmScanBin, "h 25!, w 165!, gap 10");
		add(hmmerScanTF, "h 25!, gap 10, span, growX, gapright 10, wrap");
		add(loadHmmPressBin, "h 25!, w 165!, gap 10");
		add(hmmerPressTF, "h 25!, gap 10, span, growX, gapright 10, wrap");
		
		add(loadHmmDB, "h 25!, w 165!, gap 10");
		add(hmmer3dbTF, "h 25!, gap 10, span, growX, gapright 10, wrap");
				
		add(new JXTitledSeparator("URLs"),"growx, span, wrap, gaptop 10");
		add(new JLabel("Google Url: "), "h 25!, gap 10");
		add(googleField, "h 25!, gap 10, span, growx, gapright 10, wrap");
		add(new JLabel("NCBI Url: "), "h 25!, gap 10");
		add(ncbiField, "h 25!, gap 10, span, growx, gapright 10, wrap");
		/*add(new JLabel("Pfam Url: "), "h 25!, gap 10");
		add(pfamField, "h 25!, gap 10, span, growx, gapright 10, wrap");*/
		add(new JLabel("Uniprot Url: "), "h 25!, gap 10");
		add(uniprotField, "h 25!, gap 10, span, growx, gapright 10, wrap");
		
//		add(new JXTitledSeparator("Advice"),"growx, span, wrap, gaptop 10");
//		add(showAdvices, 	"h 25!, gap 10, wrap");
		add(new JXTitledSeparator("Workspace"),"growx, span, wrap, gaptop 10");
		add(saveOnExit, 	"h 25!, gap 10, span 2, wrap");
		add(overwriteProject, 	"h 25!, gap 10, span 2, wrap");
		
		add(new JXTitledSeparator("Apply"),"growx, span, wrap, gaptop 10");
		add(apply, "h 25!, w 80!, split 2, gap 10");
		add(cancel, "h 25!, gap 15");
		add(restore, "h 25!, w 80!, gap 10");
		
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
	 * Check whether the current HMMER3 database dir
	 * contains the pressed files
	 *
	 * @param file
	 * @return
	 */
	private boolean checkHmmDBDir(File file, boolean initPress) {
		
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
		
		boolean pressAvail = true;
		
		// check if hmmpress service is available
		if (!hmmer3Engine.isAvailableService("hmmpress")) {
			// if not, check whether press bin in the selected textfield is valid
			if (!hmmerPressTF.getText().equals("")) {
				pressAvail = checkHmmBin(new File(hmmerPressTF.getText()));
			}
		}
		
		// check if pressed files are available
		if (!HmmPress.hmmFilePressed(file) && initPress) {
			if (MessageUtil.showDialog(parentFrame, file.getName()+" is not pressed. Do you want DoMosaicS to press it now?")) {
				if (!pressAvail || (hmmerPressTF.getText().isEmpty())) {
					MessageUtil.showInformation(parentFrame, "Please first provide hmmpress binary");
					hmmer3dbTF.setText("");
					return false; 
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				HmmPress hmmPress = new HmmPress(hmmer3Engine.getAvailableServicePath("hmmpress"), file, configPanel);
				//TODO  we should get some type of return here.
				hmmer3Engine.launch(hmmPress);
			}
			else {
				return false;
				// do something else here
			}	
		}
		return true;
	}
	


}
