package angstd.ui.wizards.pages;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.wizards.GUIComponentFactory;

/**
 * WizardPage allowing the user to choose a project which he wants to 
 * export.
 * 
 * @author Andreas Held
 *
 */
public class ChooseProjectToSavePage extends WizardPage {
	private static final long serialVersionUID = 1L;
	
	/** the key used to access the project after the wizard finished */
	public static final String PROJECT_KEY = "project";
	
	/** the list displaying the available projects */
	private JList list;
	

	/**
	 * Constructor for a new ChooseProjectToSavePage
	 */
	public ChooseProjectToSavePage() {
		setLayout(new MigLayout());
		
		// set up the project list
		list = GUIComponentFactory.createProjectList();
		list.setName(PROJECT_KEY);

		add(new JLabel("Select one of the projects contained in the workspace:"), 	"gap 10, wrap");
		add(new JScrollPane(list), 				"gap 10, span, growx");
	}

	/**
	 * Returns the text on the right side within the wizard
	 * 
	 * @return
	 * 		description for the page
	 */
    public static final String getDescription() {
        return "Project Selection";
    }
    
    /**
     * Checks if all necessary choices are made.
     */
    protected String validateContents (Component component, Object o) {
    	if (list.getSelectedValue() == null)
			return "Please select a project";
        return null;
    }

}
