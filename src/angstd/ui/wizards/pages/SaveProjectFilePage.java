package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import angstd.model.workspace.ProjectElement;
import angstd.ui.util.FileDialogs;
import angstd.ui.util.MessageUtil;



/**
 * WizardPage shown within the SaveProjectDialog which enables the user
 * to choose a destination file for project / view export.
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
		JButton browse = new JButton("Browse");
		browse.addActionListener(this);
		
		path = new JTextField();
		path.setEditable(false);
		
		// associate with keys
		path.setName(FILE_KEY);

		// set up the page
		add(new JXTitledSeparator("Select destination folder"), "growx, span, wrap, gap 10");
		add(path, "w 165!, h 25!, gap 10");
		add(browse, "gap 10");
	}
	
	/**
	 * @see ActionListener
	 */
	public void actionPerformed(ActionEvent e) {	
		File file = FileDialogs.openChooseDirectoryDialog(this);
		if(file != null) {
			// ensure that we can write in selected dir
			if (file.canWrite())
				path.setText(file.getAbsolutePath());
			else {
				MessageUtil.showWarning("No permission to write in "+file.getName());
				path.setText("");
			}
		}
	}


	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Selection location";
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
