package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.model.workspace.ProjectElement;
import angstd.ui.util.FileDialogs;

/**
 * WizardPage shown within the SaveProjectDialog which enables the user
 * to choose a destination file for project export.
 * 
 * @author Andreas Held
 *
 */
public class SaveProjectFilePage extends WizardPage implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** the key used to access the selected file after the wizard finished */
	public static final String FILE_KEY = "filepath";
	
	/** text field showing the selected file path */
	protected JTextField path, name;
	
	
	/**
	 * Constructor for a new SaveProjectFilePage
	 */
	public SaveProjectFilePage() {	
		setLayout(new MigLayout());
		
		
		
		// init components
		JButton browse = new JButton("Browse...");
		browse.addActionListener(this);
		
		path = new JTextField(25);
		path.setEditable(false);
		
		// associate with keys
		path.setName(FILE_KEY);

		// set up the page
		add(new JLabel("Select destination file:"), "gap 10");
		add(path, "gap 10");
		add(browse, "gap 10");
	}
	
	/**
	 * @see ActionListener
	 */
	public void actionPerformed(ActionEvent e) {	
		File file = FileDialogs.openChooseDirectoryDialog(this);
		if(file != null) 
			path.setText(file.getAbsolutePath());
	}


	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "File Selection";
    }
    
    /**
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
		if(path.getText().trim().isEmpty())
			return "Please select a location";
        return null;
    }

}
