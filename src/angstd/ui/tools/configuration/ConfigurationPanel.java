package angstd.ui.tools.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.configuration.Configuration;
import angstd.model.configuration.ConfigurationWriter;


/**
 * Panel which is displayed in the configuration frame to change 
 * for instance lookup addresses. The changed data is stored
 * within the backend data type {@link Configuration}
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class ConfigurationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/** embedding parent frame */
	protected ConfigurationFrame parent;
	
	/** textfields for lookup addresses */
	protected JTextField googleField, ncbiField, pfamField, uniprotField, emailField, hmmer3binTF, hmmer3dbTF;
	
	/** buttons for apply and cancel */
	protected JButton apply, cancel, restore;
	
	/** checkbox indicating whether or not advises should be shown */
	protected JCheckBox showAdvices;
	
	
	/**
	 * Constructor for a new ConfigurationPanel
	 * 
	 * @param parent
	 * 		the embedding frame
	 */
	public ConfigurationPanel(ConfigurationFrame parent) {
		super(new MigLayout());
		
		this.parent = parent;
		initComponents();
		layoutComponents();
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
		parent.dispose();
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
		
		
		showAdvices = new JCheckBox("Show Advices", config.isShowAdvices());
		
		apply = new JButton ("Apply");
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
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
		add(new JLabel("HMMER3 binaries"), "gap 10");
		add(hmmer3binTF, "gap 10, span, growX, wrap");
		add(new JLabel("Default hmm database"), "gap 10");
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

}
