package angstd.ui.wizards.pages;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.netbeans.spi.wizard.WizardPage;

import angstd.ui.WorkspaceManager;
import angstd.ui.util.MessageUtil;

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
	public CreateProjectPage() {
		super("Select a name");
		
		setLayout(new MigLayout());
		setPreferredSize(p_size);
		
		input = new JTextField();
		input.setName("name");

		add(new JLabel("Select a name for the new Project."), "gap 10, wrap");
		add(new JLabel("Project Name:"), "gap 10, split");
		add(input, "gap 10, growx");
	}
	
	/**
     * Checks if all necessary input is made.
     */
	protected String validateContents(Component component, Object o) {
		
		String projectName = input.getText().trim();
		
		if (projectName.isEmpty())
			return "Select a name";
		
		if (projectName.equals("Default Project"))
			return "Default Project name disallowed";
		
		if (WorkspaceManager.getInstance().ProjectExists(projectName))
			return "Name taken - choose new name";
			
		return null;   
    }
	
}
