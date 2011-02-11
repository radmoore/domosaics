package angstd.ui.tools.configuration;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import angstd.localservices.hmmer3.Hmmer3Engine;
import angstd.localservices.hmmer3.programs.HmmPress;
import angstd.model.configuration.Configuration;
import angstd.model.configuration.ConfigurationWriter;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;


/**
 * Panel which is displayed in the configuration frame to change 
 * for instance lookup addresses. The changed data is stored
 * within the backend data type {@link Configuration}
 * 
 * TODO
 * - This is currently a hack!
 * - ActionListner implemented as anonyomous inner class -> change?
 * - Process communiction with the Hmmer3Engine sucks.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ConfigurationPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/** embedding parent frame */
	protected ConfigurationFrame parentFrame;
	protected JPanel configPanel;
	
	/** textfields for lookup addresses */
	protected JTextField googleField, ncbiField, pfamField, uniprotField, emailField, hmmer3binTF, hmmer3dbTF;
	
	protected JButton loadHmmBins, loadHmmDB;
	
	/** buttons for apply and cancel */
	protected JButton apply, cancel, restore;
	
	/** checkbox indicating whether or not advises should be shown */
	protected JCheckBox showAdvices;
	
	protected Hmmer3Engine hmmer3Engine;
	
	
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
		config.setHmmerBins(hmmer3binTF.getText());
		config.setHmmerDB(hmmer3dbTF.getText());
		config.setShowAdvices(showAdvices.isSelected());

		ConfigurationWriter.write(config.getConfigFile());
		
		dispose();
	}
	
	/**
	 * Restores the default settings of the configuration file.
	 */
	private void restore() {
		Configuration config = Configuration.getInstance();
		config.restoreDefaults();
		hmmer3binTF.setText(config.getHmmerBins());
		hmmer3dbTF.setText(config.getHmmerDB());
		googleField.setText(config.getGoogleUrl());
		ncbiField.setText(config.getNcbiUrl());
		pfamField.setText(config.getPfamUrl());
		uniprotField.setText(config.getUniprotUrl());
		emailField.setText(config.getEmailAddr());
		showAdvices.setSelected(config.isShowAdvices());
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
		hmmer3binTF = new JTextField(config.getHmmerBins());
		hmmer3dbTF = new JTextField(config.getHmmerDB());
		googleField = new JTextField(config.getGoogleUrl(), 50);
		ncbiField = new JTextField(config.getNcbiUrl(), 50);
		pfamField = new JTextField(config.getPfamUrl(), 50);
		uniprotField = new JTextField(config.getUniprotUrl(), 50);
		emailField = new JTextField(config.getEmailAddr(), 50);
		
		loadHmmBins = new JButton("Load hmm binaries");
		loadHmmBins.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				File binDir = FileDialogs.openChooseDirectoryDialog(parentFrame);
				if (binDir == null || !binDir.canRead())
					return;
				if (checkHmmBinDir(binDir))
					hmmer3binTF.setText(binDir.getAbsolutePath());
					else return;
			}
		});
		
		loadHmmDB = new JButton("Load hmmer3 model DB");
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
		
		
		showAdvices = new JCheckBox("Show Advices", config.isShowAdvices());
		
		apply = new JButton ("Apply");
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				// user can only save a valid config
				if (!hmmer3binTF.getText().equals("")) {
					if (!checkHmmBinDir(new File(hmmer3binTF.getText())))
						return;
					
				}
				else if (!hmmer3dbTF.getText().equals("")) {
					if (!checkHmmDBDir(new File(hmmer3binTF.getText()), false))
						return;
				
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
		
		restore = new JButton ("Restore Defaults");
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
		add(new JLabel("Email: "), "gap 10");
		add(emailField, "gap 10, span, growx, wrap");
		
		add(new JXTitledSeparator("Local annotation"),"growx, span, wrap, gaptop 10");
		add(loadHmmBins, "gap 10");
		add(hmmer3binTF, "gap 10, span, growX, wrap");
		add(loadHmmDB, "gap 10");
		add(hmmer3dbTF, "gap 10, span, growX, wrap");
				
		add(new JXTitledSeparator("URLs"),"growx, span, wrap, gaptop 10");
		add(new JLabel("Google Url: "), "gap 10");
		add(googleField, "gap 10, span, growx, wrap");
		add(new JLabel("NCBI Url: "), "gap 10");
		add(ncbiField, "gap 10, span, growx, wrap");
		add(new JLabel("Pfam Url: "), "gap 10");
		add(pfamField, "gap 10, span, growx, wrap");
		add(new JLabel("Uniprot Url: "), "gap 10");
		add(uniprotField, "gap 10, span, growx, wrap");
		
		add(new JXTitledSeparator("Advice"),"growx, span, wrap, gaptop 10");
		add(showAdvices, 	"gap 10, wrap");
		
		add(new JXTitledSeparator("Apply"),"growx, span, wrap, gaptop 10");
		add(apply, 			"gap 10");
		add(cancel, 		"gap 10");
		add(restore, 		"gap 20");
		
	}
	/**
	 * Check whether the current HMMER3 binary dir
	 * is contains a HMMER3 program
	 * 
	 * @param dir
	 * @return
	 */
	private boolean checkHmmBinDir(File dir) {

		if (!dir.exists()) {
			MessageUtil.showWarning("Warning: could not find required HMMER programs in "+dir.getName());
			return false;
		}
		
		File[] files = dir.listFiles();
		HashMap<String, File> hmmer3bins = new HashMap<String, File>();
			
		if (files.length == 0) {
			MessageUtil.showWarning("Could not find any HMMER programs.");
			return false;
		}
		for (int i=0; i < files.length; i++) {	
			if ( Hmmer3Engine.isSupportedService(files[i].getName()) ) {
				if (!files[i].canExecute()) {
					MessageUtil.showWarning(parentFrame, files[i].getAbsoluteFile()+" is not executable. Exiting.");
					return false;
				}
				hmmer3bins.put(files[i].getName(), files[i]);
			}	
		}	
		if (hmmer3bins.isEmpty()) {
			MessageUtil.showWarning(parentFrame, "Could not find all required HMMER programs (hmmscan, hmmpress).");
			return false;
		}
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
		
		if (!file.exists()) {
			MessageUtil.showWarning("Warning: could not find specified HMMER db file "+file.getName());
			return false;
		}
		
		// we need an instance of hmmer3engine, to be able to check
		// whether hmmpress is available
		boolean pressAvail = true;
		
		// check if hmmpress service is available
		if (!hmmer3Engine.isAvailableService("hmmpress")) {
			// if not, check whether all required bins are in the selected hmm3bin textfield Dir
			if (!hmmer3binTF.getText().equals("")) {
				pressAvail = checkHmmBinDir(new File(hmmer3binTF.getText()));
			}
		}
		
		// check if pressed files are available
		if (!HmmPress.hmmFilePressed(file) && initPress) {
			if (MessageUtil.showDialog("The HMMERDBFILE is not pressed. Do you want AnGSTD to press it now?")) {
				if (!pressAvail) {
					MessageUtil.showInformation("Please first choose a directory with the HMMER3 binaries");
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
