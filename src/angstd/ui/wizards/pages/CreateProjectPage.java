package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.AngstdUI;
import angstd.ui.WorkspaceManager;

/**
 * WizardPage allowing the user to enter a name for a project which is
 * going to be created.
 * 
 * @author Andreas Held
 *
 */
public class CreateProjectPage extends WizardPage {
	private static final long serialVersionUID = 1L;

	/** the key used to access the project name after the wizard finished */
	public static final String ELEMENT_KEY = "name";
	
	/** size of the page */
	private final static Dimension p_size = new Dimension(300,200);

	/** input field for the name */
	protected JTextField input;
	
	
	/**
	 * Constructor for a new CreateProjectPage
	 */
	public CreateProjectPage(String name) {
		super("Select a name");
		
		setLayout(new MigLayout());
		setPreferredSize(p_size);
		
		input = new JTextField();
		if (! (name == null))
			input.setText(name);
		input.setName("name");

		add(new JLabel("Enter a name for the new project"), "gap 10, wrap");
		add(new JLabel("Name:"), "gap 10, split");
		add(input, "h 25!, gap 10, growx");
	}
	
	/**
     * Checks if all necessary input is made.
     */
	protected String validateContents(Component component, Object o) {
		
		String projectName = input.getText().trim();
		
		if (projectName.isEmpty())
			return "Select a name";

		if (projectName.length() > 25)
			return "Name should not exceed 25 characters";
		
		if (projectName.equals("Default Project"))
			return "Default Project name disallowed";
		
		if (WorkspaceManager.getInstance().projectExists(projectName))
			return "Name taken - choose new name";
			
		return null;   
    }
	
}
